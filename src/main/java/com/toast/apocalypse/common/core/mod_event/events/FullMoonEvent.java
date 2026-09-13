package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.capability.CapabilityHelper;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.mod_event.EventType;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.entity.living.IFullMoonMob;
import com.toast.apocalypse.common.tag.ApocalypseEntityTags;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import fathertoast.crust.api.config.common.value.collection.key.RegObjKey;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The full moon event. This event can occur every 8 days in game and will interrupt any other event and can not be interrupted
 * by any other event.<br>
 * These are often referred to as "full moon sieges" in other parts of the code and in the properties file.
 */
public final class FullMoonEvent extends AbstractEvent {
    // NBT tag names
    private static final String TAG_TIME_UNTIL_NEXT_SPAWN = "TimeNextSpawn";
    private static final String TAG_SPAWN_TIME = "SpawnTime";
    private static final String TAG_DEATH_COUNT = "PlayerDeathCount";
    private static final String TAG_TOTAL_SPAWNS = "TotalMobs";
    private static final String TAG_MOBS_TO_SPAWN = "MobsToSpawn";
    private static final String TAG_SIEGE_MOBS = "SiegeMobs";
    private static final String TAG_STATE = "State";
    
    private static final IRegWrapper<EntityType<?>> ENTITY_REG = IRegWrapper.of( ForgeRegistries.ENTITY_TYPES );
    
    /** Number of spawn placements attempted for each spawn before discarding. */
    private static final int SPAWN_ATTEMPTS = 30;
    
    /** The base component used for the boss event overlay. */
    private static final Component EVENT_NAME_COMPONENT = Component.translatable( "event.apocalypse.full_moon.title" );
    /** The component used for the boss event overlay to show a successful completion. */
    private static final Component EVENT_VICTORY_COMPONENT = EVENT_NAME_COMPONENT.copy().append( " - " )
            .append( Component.translatable( "event.minecraft.raid.victory" ) );
    /** The component used for the boss event overlay to show a failure. */
    private static final Component EVENT_DEFEAT_COMPONENT = EVENT_NAME_COMPONENT.copy().append( " - " )
            .append( Component.translatable( "event.minecraft.raid.defeat" ) );
    
    /** @return A new component for the boss event overlay displaying the given number of remaining mobs. */
    private static Component eventRemainingComponent( int remaining ) {
        return EVENT_NAME_COMPONENT.copy().append( " - " )
                .append( Component.translatable( "event.apocalypse.full_moon.remaining", remaining ) );
    }
    
    
    /** The 'boss health bar' we use to show information about this event. */
    private final ServerBossEvent bossEvent = new ServerBossEvent( EVENT_NAME_COMPONENT,
            BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_10 );
    
    /** A list containing all living mobs that were spawned by this siege. */
    private final List<LivingEntity> spawnedSiegeMobs = new ArrayList<>();
    
    /** This event's current state. */
    private State state = State.GRACE_PERIOD;
    /** The time until the next mob should be spawned for the player. */
    private int timer = ApocalypseConfig.LUNAR_SIEGE.PACING.gracePeriod.getInt();
    
    /** The time between each mob spawn. */
    private int spawnTime;
    /** The total number of mobs this event spawns. */
    private int totalSpawns;
    /** A list containing all the mobs that will be spawned. */
    private final List<SpawnEntry> mobsToSpawn = new ArrayList<>();
    /** Number of mobs left to spawn. */
    private int remainingMobsToSpawn;
    
    
    public FullMoonEvent( EventType<?> type ) {
        super( type );
        bossEvent.setProgress( 0.0F );
    }
    
    /** Called when this event starts. */
    @Override
    public void onStart( MinecraftServer server, ServerPlayer player ) {
        timer = ApocalypseConfig.LUNAR_SIEGE.PACING.gracePeriod.getInt();
        setState( State.GRACE_PERIOD );
        
        bossEvent.setVisible( true );
        bossEvent.addPlayer( player );
    }
    
    /**
     * Called every 5 ticks on the server to tick this event.
     *
     * @param player The player to update this event for.
     */
    @Override
    public void update( ServerLevel level, ServerPlayer player, PlayerDifficultyManager difficultyManager ) {
        switch( state ) {
            case GRACE_PERIOD -> updateGracePeriod( level, player, difficultyManager );
            case SPAWNING -> updateSpawning( level, player, difficultyManager );
            case CLEANUP -> updateCleanup( level, player, difficultyManager );
            case VICTORY, DEFEAT -> updateCompleted( level, player, difficultyManager );
        }
    }
    
    /** Called every 5 ticks on the server during the grace period state. */
    private void updateGracePeriod( ServerLevel level, ServerPlayer player, PlayerDifficultyManager difficultyManager ) {
        if( decrementTimer() ) {
            long difficulty = CapabilityHelper.getDifficulty( player );
            final double scaledDifficulty = CapabilityHelper.fractalDivByDayLength( difficulty );
            for( var entry : ApocalypseConfig.LUNAR_SIEGE.SIEGE_MOBS.mobSpawnSettings.entries() ) {
                if( entry != null ) {
                    int count = entry.value().getCount( scaledDifficulty );
                    if( count > 0 ) {
                        mobsToSpawn.add( new SpawnEntry( entry.key(), count ) );
                        remainingMobsToSpawn += count;
                    }
                }
            }
            totalSpawns = remainingMobsToSpawn;
            spawnTime = remainingMobsToSpawn > 0 ? ApocalypseConfig.LUNAR_SIEGE.PACING.spawnDuration.getInt() / remainingMobsToSpawn : 666;
            setState( State.SPAWNING );
        }
        int gracePeriod = ApocalypseConfig.LUNAR_SIEGE.PACING.gracePeriod.getInt();
        bossEvent.setProgress( Mth.clamp( (float) (gracePeriod - timer) / gracePeriod, 0.0F, 1.0F ) );
    }
    
    /** Called every 5 ticks on the server during the spawning state. */
    private void updateSpawning( ServerLevel level, ServerPlayer player, PlayerDifficultyManager difficultyManager ) {
        if( decrementTimer() ) {
            RandomSource random = level.getRandom();
            EntityType<?> entityType = drawEntityType( random );
            if( entityType != null ) {
                spawnMob( entityType, level, player );
                timer = spawnTime;
            }
            if( remainingMobsToSpawn <= 0 ) {
                timer = ApocalypseConfig.LUNAR_SIEGE.PACING.timeoutDuration.getInt();
                setState( State.CLEANUP );
            }
        }
        refreshRemainingMobCount();
    }
    
    /** Called every 5 ticks on the server during the cleanup state. */
    private void updateCleanup( ServerLevel level, ServerPlayer player, PlayerDifficultyManager difficultyManager ) {
        int totalRemaining = refreshRemainingMobCount();
        if( totalRemaining <= 0 ) {
            //TODO perhaps generate some reward?
            timer = ApocalypseConfig.LUNAR_SIEGE.PACING.resultsDuration.getInt();
            setState( State.VICTORY );
        }
        else if( decrementTimer() ) {
            if( ApocalypseConfig.LUNAR_SIEGE.SIEGE_MOBS.despawnMobsOnTimeout.get() ) {
                spawnedSiegeMobs.forEach( entity -> {
                    if( entity instanceof Mob mob ) IFullMoonMob.spawnSmoke( level, mob );
                    entity.discard();
                } );
            }
            timer = ApocalypseConfig.LUNAR_SIEGE.PACING.resultsDuration.getInt();
            setState( State.DEFEAT );
        }
        else {
            bossEvent.setName( totalRemaining < 10 ? eventRemainingComponent( totalRemaining ) : EVENT_NAME_COMPONENT );
        }
    }
    
    /** Called every 5 ticks on the server during the victory or defeat state. */
    private void updateCompleted( ServerLevel level, ServerPlayer player, PlayerDifficultyManager difficultyManager ) {
        decrementTimer();
    }
    
    /** Called to ensure the default settings for the boss event are in-sync with the event state. */
    private void setState( State newState ) {
        switch( newState ) {
            case GRACE_PERIOD -> {
                bossEvent.setName( EVENT_NAME_COMPONENT );
                int gracePeriod = ApocalypseConfig.LUNAR_SIEGE.PACING.gracePeriod.getInt();
                bossEvent.setProgress( Mth.clamp( (float) (gracePeriod - timer) / gracePeriod, 0.0F, 1.0F ) );
            }
            case SPAWNING -> {
                bossEvent.setName( EVENT_NAME_COMPONENT );
                refreshRemainingMobCount();
            }
            case CLEANUP -> {
                int totalRemaining = refreshRemainingMobCount();
                bossEvent.setName( totalRemaining < 10 ? eventRemainingComponent( totalRemaining ) : EVENT_NAME_COMPONENT );
            }
            case VICTORY -> {
                bossEvent.setName( EVENT_VICTORY_COMPONENT );
                bossEvent.setProgress( 0.0F );
            }
            case DEFEAT -> {
                bossEvent.setName( EVENT_DEFEAT_COMPONENT );
                bossEvent.setColor( BossEvent.BossBarColor.RED );
                bossEvent.setProgress( 0.0F );
            }
        }
        state = newState;
    }
    
    /** Reduces the timer and returns true if the time has run out. */
    private boolean decrementTimer() {
        if( timer > 0 ) timer -= PlayerDifficultyManager.TICKS_PER_UPDATE;
        return timer <= 0;
    }
    
    /** @return The total number of mobs remaining (i.e., siege mobs yet to be spawned plus all currently alive). */
    private int refreshRemainingMobCount() {
        // Stop tracking dead/despawned siege mobs
        spawnedSiegeMobs.removeIf( mob -> !mob.isAlive() || mob.isRemoved() ||
                mob instanceof OwnableEntity ownable && ownable.getOwner() instanceof Player );
        
        // Update event overlay progress
        int totalRemaining = spawnedSiegeMobs.size() + remainingMobsToSpawn;
        bossEvent.setProgress( Mth.clamp( (float) totalRemaining / totalSpawns, 0.0F, 1.0F ) );
        return totalRemaining;
    }
    
    /** Called before each update to check if this event should keep running. */
    @Override
    public boolean shouldContinueRunning( ServerLevel level, ServerPlayer player, double scaledDifficulty, PlayerDifficultyManager difficultyManager ) {
        return timer > 0 || state != State.VICTORY && state != State.DEFEAT;
    }
    
    /** Called when the event ends naturally. */
    @Override
    public void onEnd( MinecraftServer server, ServerPlayer player ) {
        bossEvent.removeAllPlayers();
        bossEvent.setVisible( false );
    }
    
    /** Called when the player disconnects before the event can end naturally. */
    @Override
    public void stop( ServerLevel level, ServerPlayer player ) {
        unloadAllMobs();
    }
    
    /** @return A random entity type from the remaining spawns, or null if there are no mobs left to spawn. */
    @Nullable
    private EntityType<?> drawEntityType( RandomSource random ) {
        if( remainingMobsToSpawn > 0 ) {
            int choice = random.nextInt( remainingMobsToSpawn );
            for( Iterator<SpawnEntry> iterator = mobsToSpawn.iterator(); iterator.hasNext(); ) {
                SpawnEntry entry = iterator.next();
                choice -= entry.count;
                if( choice < 0 ) {
                    if( entry.removeOne() ) iterator.remove();
                    remainingMobsToSpawn--;
                    return entry.type;
                }
            }
        }
        return null;
    }
    
    /**
     * Spawns a full moon mob of the entity type given.
     *
     * @param entityType The entity type of the mob to spawn.
     * @param level      The world to spawn this mob in.
     * @param player     The player to spawn this mob for.
     */
    private void spawnMob( EntityType<?> entityType, ServerLevel level, ServerPlayer player ) {
        if( level.getDifficulty() != Difficulty.PEACEFUL ) {
            if( trySpawn( entityType, level, player ) instanceof LivingEntity entity ) {
                onMobSpawned( entity, player );
                LivingEntity driver = entity.getControllingPassenger();
                if( driver != null ) {
                    onMobSpawned( driver, player );
                    totalSpawns++; // We seem to have triggered an additional spawn :O
                }
                
                level.addFreshEntity( entity );
                return;
            }
        }
        // Spawn consumed, but failed; total number of spawns reduced :(
        totalSpawns--;
    }
    
    /** Called on each siege mob as it is spawned. Adds the mob to the tracker and sets its target. */
    private void onMobSpawned( LivingEntity entity, ServerPlayer player ) {
        if( entity instanceof NeutralMob mob ) {
            mob.setTarget( player );
            mob.setPersistentAngerTarget( player.getUUID() );
            mob.setRemainingPersistentAngerTime( Integer.MAX_VALUE );
        }
        else if( entity instanceof Mob mob ) {
            mob.setTarget( player );
        }
        
        if( entity instanceof IFullMoonMob mob ) {
            mob.setPlayerTargetUUID( player.getUUID() );
            mob.setPlayerDeathCount( getPlayerDeathCount() );
        }
        //        else {
        //            //TODO Maybe we can do this to support non-full-moon mobs; alternatively, we could use the list of spawned siege mobs
        //            AttributeInstance followRange = entity.getAttribute( Attributes.FOLLOW_RANGE );
        //            if( followRange != null ) {
        //                // Increase to be greater or equal to max spawn distance
        //            }
        //        }
        
        spawnedSiegeMobs.add( entity );
    }
    
    /** @return The spawned mob, if the spawn attempt was successful. Null if we were unabled to spawn the mob. */
    @Nullable
    private Entity trySpawn( EntityType<?> entityType, ServerLevel level, ServerPlayer player ) {
        RandomSource random = level.getRandom();
        BlockPos playerPos = player.blockPosition();
        BlockPos spawnPos = null;
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        final int minDist = 25;
        
        // The ghost is a special case, and we don't need to care about placement at all really
        if( entityType == ApocalypseEntities.GHOST.get() ) {
            for( int i = 0; i < SPAWN_ATTEMPTS; i++ ) {
                float spawnDir = Mth.TWO_PI * random.nextFloat();
                float spawnDist = minDist + 40 * random.nextFloat();
                int x = playerPos.getX() + Mth.floor( Mth.cos( spawnDir ) * spawnDist );
                int z = playerPos.getZ() + Mth.floor( Mth.sin( spawnDir ) * spawnDist );
                pos.set( x, playerPos.getY() + random.nextInt( 60 ), z );
                
                if( level.isLoaded( pos ) ) {
                    spawnPos = pos;
                    break;
                }
            }
        }
        else {
            SpawnPlacements.Type placementType = SpawnPlacements.getPlacementType( entityType );
            for( int tries = 0; tries < SPAWN_ATTEMPTS; tries++ ) {
                float spawnDir = Mth.TWO_PI * random.nextFloat();
                float spawnDist = minDist + 40 * random.nextFloat();
                int x = playerPos.getX() + Mth.floor( Mth.cos( spawnDir ) * spawnDist );
                int z = playerPos.getZ() + Mth.floor( Mth.sin( spawnDir ) * spawnDist );
                int y = level.getHeight( SpawnPlacements.getHeightmapType( entityType ), x, z );
                pos.set( x, y, z );
                
                if( level.dimensionType().hasCeiling() ) {
                    // Move the position down until we find a pocket of air
                    do { pos.move( Direction.DOWN ); }
                    while( !level.getBlockState( pos ).isAir() );
                    // Then continue to move down until we find the ground within that air pocket
                    do { pos.move( Direction.DOWN ); }
                    while( !level.getBlockState( pos ).isAir() && pos.getY() > level.getMinBuildHeight() );
                    pos.move( Direction.UP ); // For parity with height map
                }
                if( placementType == SpawnPlacements.Type.ON_GROUND ) {
                    BlockPos groundPos = pos.below();
                    if( level.getBlockState( groundPos ).isPathfindable( level, groundPos, PathComputationType.LAND ) ) {
                        pos.move( Direction.DOWN );
                    }
                }
                if( level.isLoaded( pos ) && NaturalSpawner.isSpawnPositionOk( placementType, level, pos, entityType ) ) {
                    // We check for collisions around the entity and a bit above if we are dealing with a flying
                    // entity, so we can spawn it in the air a bit above ground to help prevent them getting stuck in the ground.
                    if( isFlyingType( entityType ) ) {
                        pos.move( Direction.UP, 10 + random.nextInt( 20 ) );
                        if( level.noCollision( entityType.getAABB(
                                        pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5 )
                                .inflate( 0.5, 2.0, 0.5 )
                                .move( 0.0, 2.0, 0.0 ) ) ) {
                            spawnPos = pos.immutable();
                            break;
                        }
                    }
                    else {
                        if( level.noCollision( entityType.getAABB(
                                pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5 ) ) ) {
                            spawnPos = pos.immutable();
                            break;
                        }
                    }
                }
            }
        }
        // No viable spawn position found, abort
        if( spawnPos == null ) return null;
        
        // Check environment conditions before spawning.
        double envValue = ApocalypseConfig.LUNAR_SIEGE.GENERAL.siegeSpawningConditions
                .getOrElse( EnvironmentContext.withTarget( level, spawnPos ), 1.0 );
        if( envValue > 0.0 && random.nextDouble() < envValue ) {
            return entityType.create( level, null, null, spawnPos,
                    MobSpawnType.EVENT, true, true );
        }
        else {
            // Conditions weren't met, nothing spawned
            return null;
        }
    }
    
    private static boolean isFlyingType( EntityType<?> entityType ) {
        return entityType.is( ApocalypseEntityTags.FLYING_ENTITIES );
    }
    
    /**
     * Saves this event's data to NBT.
     *
     * @param data The tag to write to.
     */
    @Override
    public void writeAdditional( CompoundTag data ) {
        data.putInt( TAG_TIME_UNTIL_NEXT_SPAWN, timer );
        data.putInt( TAG_SPAWN_TIME, spawnTime );
        data.putInt( TAG_DEATH_COUNT, deathCount );
        data.putInt( TAG_TOTAL_SPAWNS, totalSpawns );
        
        CompoundTag spawnsTag = new CompoundTag();
        mobsToSpawn.forEach( entry -> {
            String key = toString( entry.type );
            if( key != null && entry.count > 0 ) {
                spawnsTag.putInt( key, entry.count );
            }
        } );
        data.put( TAG_MOBS_TO_SPAWN, spawnsTag );
        
        ListTag mobsTag = new ListTag();
        spawnedSiegeMobs.forEach( mob -> {
            CompoundTag mobTag = new CompoundTag();
            if( mob.isPassenger() ) {
                Entity vehicle = mob.getRootVehicle();
                //noinspection SuspiciousMethodCalls
                if( !spawnedSiegeMobs.contains( vehicle ) && vehicle.save( mobTag ) ) mobsTag.add( mobTag );
            }
            else if( mob.save( mobTag ) ) mobsTag.add( mobTag );
        } );
        data.put( TAG_SIEGE_MOBS, mobsTag );
        
        data.putByte( TAG_STATE, (byte) state.ordinal() );
    }
    
    /**
     * Loads this event's data from the given NBT.
     *
     * @param data the tag to read from.
     */
    @Override
    public void read( CompoundTag data, ServerPlayer player, ServerLevel level ) {
        if( NBTHelper.containsNumber( data, TAG_TIME_UNTIL_NEXT_SPAWN ) )
            timer = data.getInt( TAG_TIME_UNTIL_NEXT_SPAWN );
        if( NBTHelper.containsNumber( data, TAG_SPAWN_TIME ) )
            spawnTime = data.getInt( TAG_SPAWN_TIME );
        if( NBTHelper.containsNumber( data, TAG_DEATH_COUNT ) )
            deathCount = data.getInt( TAG_DEATH_COUNT );
        if( NBTHelper.containsNumber( data, TAG_TOTAL_SPAWNS ) )
            totalSpawns = data.getInt( TAG_TOTAL_SPAWNS );
        
        if( NBTHelper.containsCompound( data, TAG_MOBS_TO_SPAWN ) ) {
            mobsToSpawn.clear();
            remainingMobsToSpawn = 0;
            CompoundTag spawnsTag = data.getCompound( TAG_MOBS_TO_SPAWN );
            spawnsTag.getAllKeys().forEach( key -> {
                EntityType<?> entityType = fromString( key );
                if( entityType != null ) {
                    int count = spawnsTag.getInt( key );
                    mobsToSpawn.add( new SpawnEntry( entityType, count ) );
                    remainingMobsToSpawn += count;
                }
            } );
        }
        
        if( NBTHelper.containsCompoundList( data, TAG_SIEGE_MOBS ) ) {
            unloadAllMobs();
            List<CompoundTag> mobs = NBTHelper.getCompoundList( data, TAG_SIEGE_MOBS );
            EntityType.loadEntitiesRecursive( mobs, level ).forEachOrdered( entity -> {
                if( entity instanceof LivingEntity mob ) onMobSpawned( mob, player );
                LivingEntity driver = entity.getControllingPassenger();
                if( driver != null ) onMobSpawned( driver, player );
                level.addFreshEntity( entity );
            } );
        }
        
        State[] states = State.values();
        if( NBTHelper.containsNumber( data, TAG_STATE ) )
            setState( states[Mth.clamp( data.getByte( TAG_STATE ), 0, states.length - 1 )] );
        
        bossEvent.setVisible( true );
        bossEvent.addPlayer( player );
    }
    
    /** Unloads all siege mobs with the intent of reloading them all whenever the player logs back in. :) */
    private void unloadAllMobs() {
        spawnedSiegeMobs.forEach( mob -> {
            if( mob.isPassenger() ) {
                Entity vehicle = mob.getRootVehicle();
                //noinspection SuspiciousMethodCalls
                if( !spawnedSiegeMobs.contains( vehicle ) )
                    vehicle.remove( Entity.RemovalReason.UNLOADED_WITH_PLAYER );
            }
            mob.remove( Entity.RemovalReason.UNLOADED_WITH_PLAYER );
        } );
        spawnedSiegeMobs.clear();
    }
    
    /** @return A string serialized from the entity type. */
    @Nullable
    private static String toString( EntityType<?> entityType ) {
        ResourceLocation key = ENTITY_REG.getKey( entityType );
        return key == null ? null : key.toString();
    }
    
    /** @return An entity type deserialized from the string. */
    @Nullable
    private static EntityType<?> fromString( String entityType ) {
        RegObjKey.Basic<EntityType<?>> key = RegObjKey.Basic.parse( ENTITY_REG, entityType, false );
        return key == null ? null : key.asValue();
    }
    
    
    /**
     * Represents one type of entity to spawn and the number of them we should try to spawn.
     */
    private static class SpawnEntry {
        final EntityType<?> type;
        int count;
        
        SpawnEntry( EntityType<?> entityType, int entityCount ) {
            type = entityType;
            count = entityCount;
        }
        
        /** Removes one from this entry's count and returns true if it is now zero or less. */
        boolean removeOne() {
            count--;
            return count <= 0;
        }
    }
    
    
    private enum State { GRACE_PERIOD, SPAWNING, CLEANUP, VICTORY, DEFEAT }
}