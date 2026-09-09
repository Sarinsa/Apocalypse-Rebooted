package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.capability.CapabilityHelper;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.toast.apocalypse.common.core.config.ApocalypseConfig.LUNAR_SIEGE;

/**
 * The full moon event. This event can occur every 8 days in game and will interrupt any other event and can not be interrupted
 * by any other event.<br>
 * These are often referred to as "full moon sieges" in other parts of the code and in the properties file.
 */
public final class FullMoonEvent extends AbstractEvent {
    
    private static final String TAG_TIME_UNTIL_NEXT_SPAWN = "TimeNextSpawn";
    private static final String TAG_SPAWN_TIME = "SpawnTime";
    private static final String TAG_DEATH_COUNT = "PlayerDeathCount";
    private static final String TAG_MOBS_TO_SPAWN = "MobsToSpawn";
    
    private static final IRegWrapper<EntityType<?>> ENTITY_REG = IRegWrapper.of( ForgeRegistries.ENTITY_TYPES );
    
    /**
     * The time it takes from when the event is triggered
     * until siege mobs can start spawning.
     */
    private static final int MAX_GRACE_PERIOD = 800;
    
    /** The time between each mob spawn */
    private int spawnTime;
    /** The time until the next mob should be spawned for the player */
    private int timeUntilNextSpawn = MAX_GRACE_PERIOD;
    /** Whether there are any mobs left to spawn */
    private boolean hasMobsLeft = true;
    
    /** A list containing all the full moon mobs that will be spawned */
    private final List<SpawnEntry> mobsToSpawn = new ArrayList<>();
    
    
    public FullMoonEvent( EventType<?> type ) {
        super( type );
    }
    
    @Override
    public void onStart( MinecraftServer server, ServerPlayer player ) {
        long difficulty = CapabilityHelper.getDifficulty( player );
        calculateMobs( difficulty );
        calculateSpawnTime();
    }
    
    @Override
    public void update( ServerLevel level, ServerPlayer player, PlayerDifficultyManager difficultyManager ) {
        // Tick time until next mob spawn
        if( timeUntilNextSpawn > 0 ) {
            timeUntilNextSpawn -= PlayerDifficultyManager.TICKS_PER_UPDATE;
        }
        if( timeUntilNextSpawn > 0 ) return;
        
        if( hasMobsLeft ) {
            RandomSource random = level.getRandom();
            EntityType<?> entityType = drawEntityType( random );
            if( entityType == null ) return;
            
            if( level.getDifficulty() != Difficulty.PEACEFUL ) {
                spawnMob( entityType, level, player );
            }
            timeUntilNextSpawn = spawnTime;
        }
    }
    
    @Override
    public boolean shouldContinueRunning( ServerLevel level, ServerPlayer player, double scaledDifficulty, PlayerDifficultyManager difficultyManager ) {
        return LUNAR_SIEGE.GENERAL.enableLunarSieges.get() && difficultyManager.isFullMoonNight();
    }
    
    @Override
    public void onEnd( MinecraftServer server, ServerPlayer player ) {
    
    }
    
    @Override
    public void stop( ServerLevel level, ServerPlayer player ) {}
    
    /**
     * Returns true if it is time to spawn a new full moon mob.
     */
    private boolean canSpawn() {
        return hasMobsLeft && timeUntilNextSpawn <= 0;
    }
    
    /**
     * Calculates the amount of full moon mobs that should be spawned for this even's player.
     *
     * @param difficulty The player's difficulty.
     */
    private void calculateMobs( long difficulty ) {
        final double scaledDifficulty = CapabilityHelper.fractalDivByDayLength( difficulty );
        for( var entry : LUNAR_SIEGE.SIEGE_MOB_PROPS.mobSpawnSettings.entries() ) {
            if( entry != null ) {
                int count = entry.value().getCount( scaledDifficulty );
                if( count > 0 ) mobsToSpawn.add( new SpawnEntry( entry.key(), count ) );
            }
        }
    }
    
    /** Calculates the interval between each mob spawn */
    private void calculateSpawnTime() {
        int totalMobCount = getSpawnsRemaining();
        spawnTime = totalMobCount > 0 ? 9_700 / totalMobCount : 666;
    }
    
    /** Calculates the number of remaining spawns */
    private int getSpawnsRemaining() {
        int totalCount = 0;
        for( SpawnEntry entry : mobsToSpawn ) totalCount += entry.count;
        return totalCount;
    }
    
    /**
     * Returns a random mob id for the mob types remaining,
     * or null if there are no mobs left to spawn.
     */
    @Nullable
    private EntityType<?> drawEntityType( RandomSource random ) {
        int totalMobCount = getSpawnsRemaining();
        hasMobsLeft = totalMobCount > 1; // Account for the one we are about to yoink
        if( totalMobCount > 0 ) {
            int choice = random.nextInt( totalMobCount );
            for( Iterator<SpawnEntry> iterator = mobsToSpawn.iterator(); iterator.hasNext(); ) {
                SpawnEntry entry = iterator.next();
                choice -= entry.count;
                if( choice < 0 ) {
                    if( entry.removeOne() ) iterator.remove();
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
        Entity entity = trySpawn( entityType, level, player );
        if( entity == null ) return;
        
        if( entity instanceof IFullMoonMob fullMoonMob ) {
            fullMoonMob.setPlayerTargetUUID( player.getUUID() );
            fullMoonMob.setPlayerDeathCount( getPlayerDeathCount() );
        }
        level.addFreshEntity( entity );
    }
    
    @Nullable
    private Entity trySpawn( EntityType<?> entityType, ServerLevel level, ServerPlayer player ) {
        RandomSource random = level.getRandom();
        BlockPos playerPos = player.blockPosition();
        BlockPos spawnPos = null;
        final int minDist = 25;
        
        // The ghost is a special case, and we don't need to care about placement at all really
        if( entityType == ApocalypseEntities.GHOST.get() ) {
            BlockPos pos;
            
            for( int i = 0; i < 10; i++ ) {
                int startX = random.nextBoolean() ? minDist : -minDist;
                int startZ = random.nextBoolean() ? minDist : -minDist;
                int x = playerPos.getX() + startX + (startX < 1 ? -random.nextInt( 40 ) : random.nextInt( 40 ));
                int z = playerPos.getZ() + startZ + (startZ < 1 ? -random.nextInt( 40 ) : random.nextInt( 40 ));
                pos = new BlockPos( x, 20 + random.nextInt( 60 ), z );
                
                if( level.isLoaded( pos ) ) {
                    spawnPos = pos;
                    break;
                }
            }
        }
        else {
            SpawnPlacements.Type placementType = SpawnPlacements.getPlacementType( entityType );
            
            for( int tries = 0; tries < 10; tries++ ) {
                int startX = random.nextBoolean() ? minDist : -minDist;
                int startZ = random.nextBoolean() ? minDist : -minDist;
                int x = playerPos.getX() + startX + (startX < 1 ? -random.nextInt( 46 ) : random.nextInt( 46 ));
                int z = playerPos.getZ() + startZ + (startZ < 1 ? -random.nextInt( 46 ) : random.nextInt( 46 ));
                int y = level.getHeight( SpawnPlacements.getHeightmapType( entityType ), x, z );
                BlockPos.MutableBlockPos pos = new BlockPos( x, y, z ).mutable();
                
                // If we are in a dimension with a ceiling,
                // move the position down until we find a pocket of air
                if( level.dimensionType().hasCeiling() ) {
                    do {
                        pos.move( Direction.DOWN );
                    }
                    while( !level.getBlockState( pos ).isAir() );
                    
                    do {
                        pos.move( Direction.DOWN );
                    }
                    while( level.getBlockState( pos ).isAir() && pos.getY() > 0 );
                }
                if( placementType == SpawnPlacements.Type.ON_GROUND ) {
                    BlockPos blockpos = pos.below();
                    if( level.getBlockState( blockpos ).isPathfindable( level, blockpos, PathComputationType.LAND ) ) {
                        pos = blockpos.mutable();
                    }
                }
                if( level.isLoaded( pos ) && NaturalSpawner.isSpawnPositionOk( placementType, level, pos, entityType ) ) {
                    // We check for collisions around the entity and a bit above if we are dealing with a flying
                    // entity, so we can spawn it in the air a bit above ground to help prevent them getting stuck in the ground.
                    if( isFlyingType( entityType ) ) {
                        pos = pos.above( 20 ).mutable();
                        
                        if( level.noCollision( entityType.getAABB( (double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D )
                                .inflate( 0.0D, 2.0D, 0.5D )
                                .move( 0.0D, 2.0D, 0.0D ) ) ) {
                            spawnPos = pos.immutable();
                            break;
                        }
                    }
                    else {
                        if( level.noCollision( entityType.getAABB( (double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D ) ) ) {
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
        double envValue = LUNAR_SIEGE.GENERAL.siegeSpawningConditions
                .getOrElse( EnvironmentContext.withTarget( level, spawnPos ), 1.0 );
        if( envValue > 0.0 && random.nextDouble() < envValue ) {
            return entityType.create( level, null, null, spawnPos, MobSpawnType.EVENT, true, true );
        }
        else {
            // Conditions weren't met, nothing spawned
            return null;
        }
    }
    
    private static boolean isFlyingType( EntityType<?> entityType ) {
        return entityType.is( ApocalypseEntityTags.FLYING_ENTITIES );
    }
    
    @Override
    public void writeAdditional( CompoundTag data ) {
        data.putInt( TAG_TIME_UNTIL_NEXT_SPAWN, timeUntilNextSpawn );
        data.putInt( TAG_SPAWN_TIME, spawnTime );
        data.putInt( TAG_DEATH_COUNT, deathCount );
        
        CompoundTag spawnsTag = new CompoundTag();
        mobsToSpawn.forEach( entry -> {
            String key = toString( entry.type );
            if( key != null && entry.count > 0 ) {
                spawnsTag.putInt( key, entry.count );
            }
        } );
        data.put( TAG_MOBS_TO_SPAWN, spawnsTag );
    }
    
    @Override
    public void read( CompoundTag data, ServerPlayer player, ServerLevel level ) {
        if( NBTHelper.containsNumber( data, TAG_TIME_UNTIL_NEXT_SPAWN ) )
            timeUntilNextSpawn = data.getInt( TAG_TIME_UNTIL_NEXT_SPAWN );
        if( NBTHelper.containsNumber( data, TAG_SPAWN_TIME ) )
            spawnTime = data.getInt( TAG_SPAWN_TIME );
        if( NBTHelper.containsNumber( data, TAG_DEATH_COUNT ) )
            deathCount = data.getInt( TAG_DEATH_COUNT );
        
        if( NBTHelper.containsCompound( data, TAG_MOBS_TO_SPAWN ) ) {
            mobsToSpawn.clear();
            CompoundTag spawnsTag = data.getCompound( TAG_MOBS_TO_SPAWN );
            spawnsTag.getAllKeys().forEach( key -> {
                EntityType<?> entityType = fromString( key );
                if( entityType != null ) {
                    mobsToSpawn.add( new SpawnEntry( entityType, spawnsTag.getInt( key ) ) );
                }
            } );
        }
    }
    
    @Nullable
    private static String toString( EntityType<?> entityType ) {
        ResourceLocation key = ENTITY_REG.getKey( entityType );
        return key == null ? null : key.toString();
    }
    
    @Nullable
    private static EntityType<?> fromString( String entityType ) {
        RegObjKey.Basic<EntityType<?>> key = RegObjKey.Basic.parse( ENTITY_REG, entityType, false );
        return key == null ? null : key.asValue();
    }
    
    
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
}