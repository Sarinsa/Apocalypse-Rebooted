package com.toast.apocalypse.common.event;

import com.toast.apocalypse.api.util.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.difficulty.MobAttributeHandler;
import com.toast.apocalypse.common.core.difficulty.MobEquipmentHandler;
import com.toast.apocalypse.common.core.difficulty.MobPotionHandler;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.entity.living.IFullMoonMob;
import com.toast.apocalypse.common.item.FatherlyToastItem;
import com.toast.apocalypse.common.util.NBTUtil;
import com.toast.apocalypse.common.util.References;
import fathertoast.crust.api.lib.EnvironmentHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

import static com.toast.apocalypse.common.core.config.ApocalypseConfig.DIFFICULTY;

public class EntityEventListener {
    
    /** A map containing an entity instance per entity type in the registry. */
    protected static final Map<EntityType<?>, Entity> ENTITY_FOR_TYPE = new HashMap<>();
    
    
    /** Cancel full moon monsters despawning during full moons. */
    @SubscribeEvent( priority = EventPriority.LOW )
    public void onDespawnCheck( MobSpawnEvent.AllowDespawn event ) {
        if( !event.getLevel().isClientSide() ) {
            if( event.getEntity() instanceof IFullMoonMob && Apocalypse.INSTANCE.getDifficultyManager().isFullMoonNight() ) {
                event.setResult( Event.Result.DENY );
            }
        }
    }
    
    /**
     * Denies mob spawns of mobs that requires the nearest player
     * to have passed a certain difficulty to spawn.
     */
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onSpawnPlacementCheck( MobSpawnEvent.SpawnPlacementCheck event ) {
        MobSpawnType spawnType = event.getSpawnType();
        
        if( spawnType == MobSpawnType.SPAWNER || spawnType == MobSpawnType.SPAWN_EGG || spawnType == MobSpawnType.COMMAND
                || spawnType == MobSpawnType.MOB_SUMMONED || spawnType == MobSpawnType.STRUCTURE )
            return;
        
        // Create or get entity instance to check against the list properly
        EntityType<?> entityType = event.getEntityType();
        final Entity entity = ENTITY_FOR_TYPE.getOrDefault( entityType, null );
        
        // Check if mob can spawn with the difficulty of the closest player
        if( entity != null && DIFFICULTY.GENERAL.mobSpawnDifficulties.contains( entity ) ) {
            final double neededDifficulty = DIFFICULTY.GENERAL.mobSpawnDifficulties.get().getValue( entity );
            final long nearestDifficulty = (PlayerDifficultyManager.getNearestPlayerDifficulty( event.getLevel(), event.getPos() )) / References.DAY_LENGTH;
            
            if( nearestDifficulty < neededDifficulty ) {
                event.setResult( Event.Result.DENY );
                return;
            }
        }
        
        // Completely ignore spawn placement checks if thunderstorm event is running
        if( event.getLevel() instanceof ServerLevel level ) {
            if( ApocalypseConfig.THUNDERSTORM.GENERAL.enabled.get() && level.isThundering() && entity instanceof Enemy ) {
                if( !ApocalypseConfig.THUNDERSTORM.GENERAL.spawnsIgnoreLight.get() ) {
                    event.setResult( Monster.isDarkEnoughToSpawn( level, event.getPos(), event.getRandom() )
                            ? Event.Result.ALLOW
                            : Event.Result.DEFAULT
                    );
                    return;
                }
                event.setResult( Event.Result.ALLOW );
            }
        }
    }
    
    @SubscribeEvent
    public void onSpawnPositionCheck( MobSpawnEvent.PositionCheck event ) {
        if( event.getSpawnType() != MobSpawnType.NATURAL ) return;
        
        if( event.getLevel() instanceof Level level && ApocalypseConfig.THUNDERSTORM.GENERAL.enabled.get() && level.isThundering() ) {
            if( event.getEntity() instanceof Enemy ) {
                event.setResult( Event.Result.ALLOW );
            }
        }
    }
    
    /** Called when a mob spawn is being finalized. */
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onFinalizeSpawn( MobSpawnEvent.FinalizeSpawn event ) {
        if( !EnvironmentHelper.isLoaded( event.getLevel(), BlockPos.containing( event.getX(), event.getY(), event.getZ() ) ) )
            return;
        
        if( NBTUtil.isEntityProcessed( event.getEntity() ) )
            return;
        
        final Mob mob = event.getEntity();
        final ServerLevelAccessor level = event.getLevel();
        final RandomSource random = level.getRandom();
        final long difficulty = PlayerDifficultyManager.getNearestPlayerDifficulty( level, mob );
        final boolean fullMoon = Apocalypse.INSTANCE.getDifficultyManager().isFullMoonNight();
        
        // Don't do anything if the player is still on grace period
        if( difficulty <= 0L )
            return;
        
        // Make sure we skip buffing non-enemies if "enemiesOnly" is enabled.
        if( ApocalypseConfig.MOB_BUFFING.GENERAL.enemiesOnly.get() && !(mob instanceof Enemy) )
            return;
        
        MobAttributeHandler.handleAttributes( mob, difficulty, fullMoon );
        MobPotionHandler.handlePotions( mob, difficulty, fullMoon, random );
        MobEquipmentHandler.handleMobEquipment( mob, difficulty, fullMoon, random );
        
        NBTUtil.markEntityProcessed( mob );
    }
    
    /** Called right before a living entity receives damage. */
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onLivingEntityDamaged( LivingDamageEvent event ) {
        final Entity attacker = event.getSource().getEntity();
        
        if( attacker != null ) {
            float damage = event.getAmount();
            
            if( attacker.getType() == ApocalypseEntities.GHOST.get() ) {
                event.setAmount( Math.max( 1.0F, damage ) );
            }
            else if( attacker.getType() == ApocalypseEntities.GRUMP.get() ) {
                event.setAmount( Math.max( 2.0F, damage ) );
            }
        }
    }
    
    /** Called when an entity is struck by lightning. */
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onEntityStruckByLightning( EntityStruckByLightningEvent event ) {
        if( event.getEntity() instanceof ItemEntity itemEntity ) {
            final Item item = itemEntity.getItem().getItem();
            
            if( item == Items.BREAD ) {
                final Level level = event.getEntity().level();
                final ItemStack stack = new ItemStack( ApocalypseObjects.Items.FATHERLY_TOAST.get(), itemEntity.getItem().getCount() );
                final int toastLevel = level.random.nextInt( 99 ) + 1;
                
                stack.getOrCreateTag().putInt( FatherlyToastItem.KEY_TOAST_LEVEL, toastLevel );
                level.addFreshEntity( new ItemEntity( level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), stack ) );
                itemEntity.discard();
            }
            // Fatherly toast needs to be lightning immune so bread
            // can actually convert instead of just going poof when struck.
            else if( item == ApocalypseObjects.Items.FATHERLY_TOAST.get() )
                event.setCanceled( true );
        }
    }
}
