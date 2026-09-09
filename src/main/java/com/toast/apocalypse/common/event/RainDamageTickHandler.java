package com.toast.apocalypse.common.event;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.misc.ApocalypseDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.toast.apocalypse.common.core.config.ApocalypseConfig.ACID_RAIN;

// TODO - DON'T FORGET!!!!! Rain damage is currently only applied to entities in the overworld!

/**
 * This listener is responsible for calculating acid rain tick damage
 * and dealing damage to entities that are exposed to the rain (or snow).
 *
 * @see com.toast.apocalypse.common.core.mod_event.EventRegistry#ACID_RAIN
 */
@Mod.EventBusSubscriber( modid = Apocalypse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE )
public final class RainDamageTickHandler {
    
    /** Whether acid snow is enabled. */
    private static boolean acidSnowEnabled = false;
    /** The amount of ticks until the next damage check should be performed. */
    private static int timeNextDamageCheck;
    
    
    /** Called when the server has finished loading and is ready to play. */
    @SubscribeEvent
    public static void onServerStarted( ServerStartedEvent event ) {
        acidSnowEnabled = ACID_RAIN.GENERAL.acidSnow.get();
    }
    
    /**
     * Called each tick on the server.
     * <br><br>
     * Checks if it is time to apply acid rain tick damage,
     * and applies damage to all exposed living entities (or only players depending on config).
     */
    @SubscribeEvent
    public static void onServerTick( TickEvent.ServerTickEvent event ) {
        if( event.phase != TickEvent.Phase.END ) return;
        
        final boolean isRainingAcid = Apocalypse.INSTANCE.getDifficultyManager().isRainingAcid( event.getServer().overworld() );
        
        if( !isRainingAcid || ACID_RAIN.GENERAL.rainDamage.get() <= 0.0 ) return;
        
        if( ++timeNextDamageCheck >= ACID_RAIN.GENERAL.damageTicks.get() ) {
            timeNextDamageCheck = 0;
            final ServerLevel overworld = event.getServer().overworld();
            final boolean playersOnly = !ACID_RAIN.GENERAL.damageMobs.get();
            
            final Iterable<? extends LivingEntity> entities = playersOnly
                    ? overworld.players()
                    : overworld.getEntities( EntityTypeTest.forClass( LivingEntity.class ),
                    ( entity ) -> !ACID_RAIN.GENERAL.mobBlacklist.contains( entity ) );
            
            for( LivingEntity entity : entities ) {
                if( EnchantmentHelper.hasAquaAffinity( entity ) ) continue;
                
                boolean rainingAcidAt = acidSnowEnabled
                        ? isRainingOrSnowingAt( overworld, entity.blockPosition().offset( 0, (int) entity.getEyeHeight(), 0 ) )
                        : overworld.isRainingAt( entity.blockPosition().offset( 0, (int) entity.getEyeHeight(), 0 ) );
                
                if( !rainingAcidAt ) continue;
                
                final ItemStack headStack = entity.getItemBySlot( EquipmentSlot.HEAD );
                
                if( !headStack.isEmpty() ) {
                    if( headStack.getItem() == ApocalypseObjects.Items.BUCKET_HELM.get() || headStack.getItem().getMaxDamage( headStack ) <= 0 ) {
                        continue;
                    }
                    // TODO configurable damage to the equipped head stack
                    headStack.hurtAndBreak( entity.getRandom().nextInt( 2 ), entity, ( playerEntity ) -> entity.broadcastBreakEvent( EquipmentSlot.HEAD ) );
                }
                else {
                    entity.hurt( ApocalypseDamageSources.of( overworld, ApocalypseDamageSources.ACID_RAIN ), ACID_RAIN.GENERAL.rainDamage.getFloat() );
                }
            }
        }
    }
    
    /** @return True if it is currently raining or snowing at the given block position. */
    public static boolean isRainingOrSnowingAt( Level level, BlockPos pos ) {
        if( !level.isRaining() ) return false;
        if( !level.canSeeSky( pos ) ) return false;
        if( level.getHeightmapPos( Heightmap.Types.MOTION_BLOCKING, pos ).getY() > pos.getY() ) return false;
        else {
            Biome biome = level.getBiome( pos ).value();
            return biome.getPrecipitationAt( pos ) == Biome.Precipitation.RAIN || biome.getPrecipitationAt( pos ) == Biome.Precipitation.SNOW;
        }
    }
    
    
    // Utility class
    private RainDamageTickHandler() {}
}