package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
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
        if( event.phase != TickEvent.Phase.END ||
                // Do nothing if all acid rain damage is disabled
                ACID_RAIN.GENERAL.healthDamage.getDouble() <= 0.0 && ACID_RAIN.GENERAL.durabilityDamage.getInt() <= 0 ) {
            return;
        }
        
        if( ++timeNextDamageCheck >= ACID_RAIN.GENERAL.damageTicks.get() ) {
            timeNextDamageCheck = 0;
            
            final PlayerDifficultyManager difficultyManager = Apocalypse.INSTANCE.getDifficultyManager();
            event.getServer().getAllLevels().forEach( level -> {
                if( PlayerDifficultyManager.canRain( level ) && difficultyManager.isRainingAcid( level ) ) {
                    doAcidRainDamage( level );
                }
            } );
        }
    }
    
    /** Applies acid rain damage to all valid entities in the level. */
    private static void doAcidRainDamage( ServerLevel level ) {
        final Iterable<? extends LivingEntity> entities = ACID_RAIN.GENERAL.damageMobs.get() ?
                level.getEntities( EntityTypeTest.forClass( LivingEntity.class ),
                        entity -> !ACID_RAIN.GENERAL.mobBlacklist.contains( entity ) ) :
                level.players();
        
        for( LivingEntity entity : entities ) {
            if( EnchantmentHelper.hasAquaAffinity( entity ) ) continue;
            
            BlockPos headPos = BlockPos.containing( entity.getX(), entity.getY( 1.0 ), entity.getZ() );
            boolean rainingAcidAt = acidSnowEnabled ? isRainingOrSnowingAt( level, headPos ) :
                    level.isRainingAt( headPos );
            if( !rainingAcidAt ) continue;
            
            // Deal health damage
            final ItemStack headStack = entity.getItemBySlot( EquipmentSlot.HEAD );
            if( headStack.isEmpty() || ACID_RAIN.GENERAL.nonProtectingItems.contains( headStack ) ) {
                entity.hurt( ApocalypseDamageSources.of( level, ApocalypseDamageSources.ACID_RAIN ), ACID_RAIN.GENERAL.healthDamage.getFloat() );
            }
            
            // Deal durability damage
            damageEquipmentFromAcidRain( entity, EquipmentSlot.HEAD );
            if( ACID_RAIN.GENERAL.damageAllEquipment.get() ) {
                damageEquipmentFromAcidRain( entity, EquipmentSlot.CHEST );
                damageEquipmentFromAcidRain( entity, EquipmentSlot.LEGS );
                damageEquipmentFromAcidRain( entity, EquipmentSlot.FEET );
            }
        }
    }
    
    /** Applies acid rain's durability damage to the item equipped in a particular slot, if applicable. */
    private static void damageEquipmentFromAcidRain( LivingEntity entity, EquipmentSlot slot ) {
        final ItemStack item = entity.getItemBySlot( slot );
        if( !item.isEmpty() && item.getItem().getMaxDamage( item ) > 0 && !ACID_RAIN.GENERAL.rainImmuneItems.contains( item ) ) {
            item.hurtAndBreak( ACID_RAIN.GENERAL.durabilityDamage.getInt(), entity,
                    e -> e.broadcastBreakEvent( slot ) );
        }
    }
    
    /** @return True if it is currently raining or snowing at the given block position. */
    public static boolean isRainingOrSnowingAt( Level level, BlockPos pos ) {
        if( !level.isRaining() ) return false;
        if( !level.canSeeSky( pos ) ) return false;
        if( level.getHeightmapPos( Heightmap.Types.MOTION_BLOCKING, pos ).getY() > pos.getY() ) return false;
        else {
            Biome biome = level.getBiome( pos ).value();
            return biome.getPrecipitationAt( pos ) != Biome.Precipitation.NONE;
        }
    }
    
    
    // Utility class
    private RainDamageTickHandler() { }
}