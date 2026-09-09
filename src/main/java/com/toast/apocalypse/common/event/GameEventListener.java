package com.toast.apocalypse.common.event;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.capability.CapabilityHelper;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.difficulty.MobAttributeHandler;
import com.toast.apocalypse.common.core.difficulty.MobEquipmentHandler;
import com.toast.apocalypse.common.core.difficulty.MobPotionHandler;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.entity.living.Grump;
import com.toast.apocalypse.common.entity.living.IFullMoonMob;
import com.toast.apocalypse.common.item.FatherlyToastItem;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.network.message.S2CSimpleClientTask;
import com.toast.apocalypse.common.util.NBTUtil;
import com.toast.apocalypse.common.util.References;
import com.toast.apocalypse.common.util.VersionCheckHelper;
import fathertoast.crust.api.lib.EnvironmentHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

import static com.toast.apocalypse.common.core.config.ApocalypseConfig.DIFFICULTY;

@SuppressWarnings( "resource" )
public final class GameEventListener {
    
    /** A map containing an entity instance per entity type in the registry. */
    private static final Map<EntityType<?>, Entity> ENTITY_FOR_TYPE = new HashMap<>();
    
    
    //------------------------------------------------------------------
    //                            PLAYER
    //------------------------------------------------------------------
    
    /** Called when a player logs into the server. */
    @SubscribeEvent
    public void onPlayerLoggedIn( PlayerEvent.PlayerLoggedInEvent event ) {
        // Check if we should send update notification
        if( ApocalypseConfig.MISC.VERSION_CHECK.sendUpdateMessage.get() ) {
            String updateMessage = VersionCheckHelper.getUpdateMessage();
            
            if( updateMessage != null ) {
                event.getEntity().sendSystemMessage( Component.literal( updateMessage ) );
            }
        }
        // Send misc sync packets to the client
        if( !event.getEntity().level().isClientSide ) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            
            NetworkHelper.sendUpdatePlayerDifficulty( player );
            NetworkHelper.sendUpdatePlayerDifficultyMult( player );
            NetworkHelper.sendUpdatePlayerMaxDifficulty( player );
            
            NetworkHelper.sendSimpleClientTaskRequest( player,
                    ApocalypseConfig.ACID_RAIN.GENERAL.acidSnow.get()
                            ? S2CSimpleClientTask.ENABLE_ACID_SNOW
                            : S2CSimpleClientTask.DISABLE_ACID_SNOW );
        }
    }
    
    /** Called when a player joins a level. */
    @SubscribeEvent
    public void onPlayerJoinLevel( EntityJoinLevelEvent event ) {
        if( event.getEntity() instanceof ServerPlayer player ) {
            // Send any level-specific sync packets to the client
            PlayerDifficultyManager difficultyManager = Apocalypse.INSTANCE.getDifficultyManager();
            NetworkHelper.sendSimpleClientTaskRequest( player,
                    difficultyManager.isRainingAcid( player.serverLevel() )
                            ? S2CSimpleClientTask.SET_ACID_RAIN
                            : S2CSimpleClientTask.REMOVE_ACID_RAIN );
        }
    }
    
    /** Called when a player sleeps in a bed. */
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onPlayerSleepInBed( PlayerSleepInBedEvent event ) {
        // Prevent players from sleeping during full moons, if enabled
        Player player = event.getEntity();
        
        if( player.isSleeping() || !player.isAlive() ) return;
        
        if( !player.level().isClientSide
                && Apocalypse.INSTANCE.getDifficultyManager().isFullMoon()
                && ApocalypseConfig.LUNAR_SIEGE.GENERAL.denySleep.get() ) {
            event.setResult( Player.BedSleepingProblem.NOT_POSSIBLE_HERE );
            player.displayClientMessage( Component.translatable( References.TRY_SLEEP_FULL_MOON ), true );
        }
    }
    
    /** Called when all players are sleeping and the new world time is about to be set. */
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onSleepFinished( SleepFinishedTimeEvent event ) {
        // Punish players for sleeping with a difficulty penalty, if enabled
        if( event.getLevel() instanceof ServerLevel serverLevel ) {
            long newTime = event.getNewTime();
            long currentTime = serverLevel.getDayTime();
            long timeSkipped = newTime - currentTime;
            
            // No point caring if the amount of time skipped is so to speak insignificant
            if( timeSkipped > 200L ) {
                for( ServerPlayer player : serverLevel.players() ) {
                    long playerDifficulty = CapabilityHelper.getDifficulty( player );
                    long playerMaxDifficulty = CapabilityHelper.getMaxDifficulty( player );
                    double difficultyMult = CapabilityHelper.getDifficultyMult( player );
                    
                    playerDifficulty += (long) ((timeSkipped * ApocalypseConfig.DIFFICULTY.GENERAL.sleepPenaltyMultiplier.get()) * difficultyMult);
                    CapabilityHelper.setDifficulty( player, Math.min( playerDifficulty, playerMaxDifficulty ) );
                    
                    player.displayClientMessage( Component.translatable( References.SLEEP_PENALTY ), true );
                    // Play spooky sound
                    player.connection.send( new ClientboundSoundPacket(
                            SoundEvents.AMBIENT_CAVE,
                            SoundSource.AMBIENT,
                            player.getX(), player.getY(), player.getZ(),
                            1.0F,
                            0.8F,
                            serverLevel.random.nextLong() ) );
                }
            }
        }
    }
    
    /** Called when a player changes dimension. */
    @SubscribeEvent
    public void onPlayerChangeDimension( PlayerEvent.PlayerChangedDimensionEvent event ) {
        if( event.getEntity() instanceof ServerPlayer serverPlayer ) {
            // Update difficulty multiplier on client
            NetworkHelper.sendUpdatePlayerDifficulty( serverPlayer );
        }
    }
    
    /**
     * Called when a player entity is cloned.
     */
    @SubscribeEvent
    public void onPlayerCloned( PlayerEvent.Clone event ) {
        // Makes sure necessary capability data from Apocalypse
        // persists when the player respawns.
        if( event.getEntity() instanceof ServerPlayer newPlayer ) {
            ServerPlayer originalPlayer = (ServerPlayer) event.getOriginal();
            originalPlayer.reviveCaps();
            
            long difficulty = CapabilityHelper.getDifficulty( originalPlayer );
            long maxDifficulty = CapabilityHelper.getMaxDifficulty( originalPlayer );
            
            originalPlayer.invalidateCaps();
            
            CapabilityHelper.setDifficulty( newPlayer, difficulty );
            CapabilityHelper.setMaxDifficulty( newPlayer, maxDifficulty );
        }
    }
    
    /** Called when player entities are being ticked. */
    @SubscribeEvent
    public void onPlayerTick( TickEvent.PlayerTickEvent event ) {
        if( event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer serverPlayer ) {
            // Prevent players from getting kicked from dedicated servers when riding a grump
            if( serverPlayer.getVehicle() instanceof Grump ) {
                serverPlayer.connection.clientVehicleIsFloating = false;
            }
        }
    }
    
    
    //------------------------------------------------------------------
    //                            SPAWNING
    //------------------------------------------------------------------
    
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
        // Create or get entity instance to check against the list properly
        EntityType<?> entityType = event.getEntityType();
        final Entity entity = ENTITY_FOR_TYPE.get( entityType );
        if( entity == null ) return;
        
        // Check if mob can spawn with the difficulty of the closest player
        MobSpawnType spawnType = event.getSpawnType();
        if( spawnType == MobSpawnType.NATURAL ) {
            double neededDifficulty = DIFFICULTY.GENERAL.mobSpawnDifficulties.getOrElse( entity, -1.0 );
            if( neededDifficulty >= 0.0 ) {
                final long nearestDifficulty = PlayerDifficultyManager.getNearestPlayerDifficulty( event.getLevel(), event.getPos() );
                final long scaledDifficulty = CapabilityHelper.mulByDayLength( neededDifficulty );
                
                if( nearestDifficulty < scaledDifficulty ) {
                    event.setResult( Event.Result.DENY );
                    return;
                }
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
        if( ApocalypseConfig.MOB_BUFFING.GENERAL.enemiesOnly.get() && !(mob instanceof Enemy) ||
                ApocalypseConfig.MOB_BUFFING.GENERAL.buffBlacklist.contains( mob ) )
            return;
        
        double scaledDifficulty = CapabilityHelper.fractalDivByDayLength( difficulty );
        MobAttributeHandler.handleAttributes( mob, scaledDifficulty, fullMoon );
        MobPotionHandler.handlePotions( mob, scaledDifficulty, fullMoon, random );
        MobEquipmentHandler.handleMobEquipment( mob, scaledDifficulty, fullMoon, random );
        
        NBTUtil.markEntityProcessed( mob );
    }
    
    
    //------------------------------------------------------------------
    //                            ENTITY
    //------------------------------------------------------------------
    
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
    
    
    //------------------------------------------------------------------
    //                        SERVER LIFECYCLE
    //------------------------------------------------------------------
    
    /** Called when the server has finished loading and is ready to play. */
    @SubscribeEvent
    public static void onServerStarted( ServerStartedEvent event ) {
        for( EntityType<?> type : ForgeRegistries.ENTITY_TYPES ) {
            if( type == EntityType.PLAYER ) continue;
            
            try {
                Entity entity = type.create( event.getServer().overworld() );
                // We only bother with living entities.
                if( entity instanceof LivingEntity ) {
                    GameEventListener.ENTITY_FOR_TYPE.put( type, entity );
                }
                else if( entity == null ) {
                    Apocalypse.LOGGER.error( "Failed to create entity instance for type {}! Mob spawn difficulty config list will not work for this type!", type );
                }
            }
            catch( Exception ignored ) {
                // If this explodes, no worries (probably isn't a normal living entity anyway)
            }
        }
    }
    
    /** Called when the server has fully shut down. */
    @SubscribeEvent
    public static void onServerStopping( ServerStoppedEvent event ) {
        ENTITY_FOR_TYPE.clear();
    }
    
    
    //------------------------------------------------------------------
    //                              MISC
    //------------------------------------------------------------------
    
    /** Called when villager trades are being created. */
    @SubscribeEvent
    public void onTrade( VillagerTradesEvent event ) {
        if( event.getType() == VillagerProfession.CLERIC ) {
            event.getTrades().get( 2 ).add( new VillagerTrades.EmeraldForItems( ApocalypseObjects.Items.FRAGMENTED_SOUL.get(), 2, 10, 10 ) );
            event.getTrades().get( 5 ).add( new VillagerTrades.ItemsForEmeralds( ApocalypseObjects.Items.LUNAR_CLOCK.get(), 34, 1, 30 ) );
        }
    }
}