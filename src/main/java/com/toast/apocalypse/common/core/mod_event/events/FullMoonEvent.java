package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.mod_event.EventType;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.entity.living.IFullMoonMob;
import com.toast.apocalypse.common.tag.ApocalypseEntityTags;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.DataStructureUtils;
import com.toast.apocalypse.common.util.References;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Random;

/**
 * The full moon event. This event can occur every 8 days in game and will interrupt any other event and can not be interrupted
 * by any other event.<br>
 * These are often referred to as "full moon sieges" in other parts of the code and in the properties file.
 */
public final class FullMoonEvent extends AbstractEvent {

    /** The time it takes from when the event is triggered
     *  until siege mobs can start spawning.
     */
    private static final int MAX_GRACE_PERIOD = 800;

    /** The difficulty until mob counts increase */
    public static double MOB_COUNT_TIME_SPAN;

    /** The starting difficulty for when the various full moon mobs can start spawning */
    public static double GHOST_START;
    public static double BREECHER_START;
    public static double GRUMP_START;
    public static double SEEKER_START;
    public static double DESTROYER_START;

    /** The minimum amount of each mob type to be spawned (depends on difficulty) */
    public static int GHOST_MIN_COUNT;
    public static int BREECHER_MIN_COUNT;
    public static int GRUMP_MIN_COUNT;
    public static int SEEKER_MIN_COUNT;
    public static int DESTROYER_MIN_COUNT;

    /** Additional mob counts for each mob type scaled with difficulty */
    public static double GHOST_ADDITIONAL_COUNT;
    public static double BREECHER_ADDITIONAL_COUNT;
    public static double GRUMP_ADDITIONAL_COUNT;
    public static double SEEKER_ADDITIONAL_COUNT;
    public static double DESTROYER_ADDITIONAL_COUNT;

    /** The maximum amount of each mob type that can spawn */
    public static int GHOST_MAX_COUNT;
    public static int BREECHER_MAX_COUNT;
    public static int GRUMP_MAX_COUNT;
    public static int SEEKER_MAX_COUNT;
    public static int DESTROYER_MAX_COUNT;


    /** Numeric IDs representing each full moon mob */
    private static final int GHOST_ID = 0;
    private static final int BREECHER_ID = 1;
    private static final int GRUMP_ID = 2;
    private static final int SEEKER_ID = 3;
    private static final int DESTROYER_ID = 4;

    /** Time until mobs can start spawning. */
    private int gracePeriod;

    /** The time between each mob spawn */
    private int spawnTime = 600;

    /** The time until the next mob should be spawned for the player */
    private int timeUntilNextSpawn = 0;

    /** Whether there are any mobs left to spawn */
    private boolean hasMobsLeft = true;

    /** A map containing all the full moon mobs that will be spawned for the player */
    private final HashMap<Integer, Integer> mobsToSpawn = new HashMap<>();

    public FullMoonEvent(EventType<?> type) {
        super(type);
    }

    @Override
    public void onStart(MinecraftServer server, ServerPlayerEntity player) {
        long difficulty = CapabilityHelper.getPlayerDifficulty(player);
        this.calculateMobs(difficulty);
        this.calculateSpawnTime();
        this.gracePeriod = MAX_GRACE_PERIOD;
    }

    @Override
    public void update(ServerWorld world, ServerPlayerEntity player, PlayerDifficultyManager difficultyManager) {
        // Tick grace period
        if (this.gracePeriod > 0) {
            this.gracePeriod -= PlayerDifficultyManager.TICKS_PER_UPDATE;
        }
        // Tick time until next mob spawn
        if (this.timeUntilNextSpawn > 0) {
            timeUntilNextSpawn -= PlayerDifficultyManager.TICKS_PER_UPDATE;
        }

        if (canSpawn()) {
            boolean hasMobsLeft = false;

            for (int id : mobsToSpawn.keySet()) {
                if (mobsToSpawn.get(id) > 0) {
                    hasMobsLeft = true;
                    break;
                }
            }
            this.hasMobsLeft = hasMobsLeft;

            if (!hasMobsLeft)
                return;

            Random random = world.getRandom();
            int mobIndex = getRandomMobIndex(random);

            if (mobIndex < 0)
                return;

            int currentCount = mobsToSpawn.get(mobIndex);
            mobsToSpawn.put(mobIndex, --currentCount);
            spawnMobFromIndex(mobIndex, world, player);

            timeUntilNextSpawn = spawnTime;
        }
    }

    @Override
    public void onEnd(MinecraftServer server, ServerPlayerEntity player) {

    }

    @Override
    public void stop(ServerWorld world) {}

    /**
     * Returns true if it is time to spawn a new full moon mob.
     */
    private boolean canSpawn() {
        return gracePeriod <= 0 && hasMobsLeft && timeUntilNextSpawn <= 0;
    }

    /**
     * Calculates the amount of full moon mobs that should be spawned for this even's player.
     *
     * @param difficulty The player's difficulty.
     */
    private void calculateMobs(long difficulty) {
        final double scaledDifficulty = (double) difficulty / References.DAY_LENGTH;
        double effectiveDifficulty;
        int count;

        // Ensure nothing is null before proceeding.
        mobsToSpawn.put(GHOST_ID, 0);
        mobsToSpawn.put(BREECHER_ID, 0);
        mobsToSpawn.put(GRUMP_ID, 0);
        mobsToSpawn.put(SEEKER_ID, 0);
        mobsToSpawn.put(DESTROYER_ID, 0);

        if (GHOST_START >= 0L && GHOST_START <= scaledDifficulty) {
            effectiveDifficulty = (scaledDifficulty - GHOST_START) / MOB_COUNT_TIME_SPAN;
            count = GHOST_MIN_COUNT + (int) (GHOST_ADDITIONAL_COUNT * effectiveDifficulty);
            this.mobsToSpawn.put(GHOST_ID, Math.min(count, GHOST_MAX_COUNT));
        }
        if (BREECHER_START >= 0L && BREECHER_START <= scaledDifficulty) {
            effectiveDifficulty = (scaledDifficulty - BREECHER_START) / MOB_COUNT_TIME_SPAN;
            count = BREECHER_MIN_COUNT + (int) (BREECHER_ADDITIONAL_COUNT * effectiveDifficulty);
            this.mobsToSpawn.put(BREECHER_ID, Math.min(count, BREECHER_MAX_COUNT));
        }
        if (GRUMP_START >= 0L && GRUMP_START <= scaledDifficulty) {
            effectiveDifficulty = (scaledDifficulty - GRUMP_START) / MOB_COUNT_TIME_SPAN;
            count = GRUMP_MIN_COUNT + (int) (GRUMP_ADDITIONAL_COUNT * effectiveDifficulty);
            this.mobsToSpawn.put(GRUMP_ID, Math.min(count, GRUMP_MAX_COUNT));
        }
        if (SEEKER_START >= 0L && SEEKER_START <= scaledDifficulty) {
            effectiveDifficulty = (scaledDifficulty - SEEKER_START) / MOB_COUNT_TIME_SPAN;
            count = SEEKER_MIN_COUNT + (int) (SEEKER_ADDITIONAL_COUNT * effectiveDifficulty);
            this.mobsToSpawn.put(SEEKER_ID, Math.min(count, SEEKER_MAX_COUNT));
        }
        if (DESTROYER_START >= 0L && DESTROYER_START <= scaledDifficulty) {
            effectiveDifficulty = (scaledDifficulty - DESTROYER_START) / MOB_COUNT_TIME_SPAN;
            count = DESTROYER_MIN_COUNT + (int) (DESTROYER_ADDITIONAL_COUNT * effectiveDifficulty);
            this.mobsToSpawn.put(DESTROYER_ID, Math.min(count, DESTROYER_MAX_COUNT));
        }
    }

    /** Calculates the interval between each mob spawn */
    private void calculateSpawnTime() {
        final int defaultSpawnTime = 500;
        int totalMobCount = 0;

        for (int mobId : mobsToSpawn.keySet()) {
            totalMobCount += mobsToSpawn.get(mobId);
        }
        this.spawnTime = totalMobCount <= 0 ? defaultSpawnTime : (10500 - MAX_GRACE_PERIOD) / totalMobCount;
    }

    /**
     * Returns a random mob index for the mob types remaining,
     * or -1 if there are no mob types left to spawn.
     */
    private int getRandomMobIndex(Random random) {
        Integer type = DataStructureUtils.randomMapKeyFiltered(random, mobsToSpawn, (id, count) -> count > 0);
        return type != null ? type : -1;
    }

    /**
     * Spawns a full moon mob. The type of mob depends on the mob index given.
     *
     * @param mobType The index of what type of mob to spawn.
     * @param world The world to spawn this mob in.
     * @param player The player to spawn this mob for.
     */
    private void spawnMobFromIndex(int mobType, ServerWorld world, ServerPlayerEntity player) {
        MobEntity mob;

        switch (mobType) {
            default:
            case GHOST_ID:
                mob = spawnMob(ApocalypseEntities.GHOST.get(), player, world);
                break;
            case BREECHER_ID:
                mob = spawnMob(ApocalypseEntities.BREECHER.get(), player, world);
                break;
            case GRUMP_ID:
                mob = spawnMob(ApocalypseEntities.GRUMP.get(), player, world);
                break;
            case SEEKER_ID:
                mob = spawnMob(ApocalypseEntities.SEEKER.get(), player, world);
                break;
            case DESTROYER_ID:
                mob = spawnMob(ApocalypseEntities.DESTROYER.get(), player, world);
                break;
        }
        if (mob == null)
            return;

        ((IFullMoonMob) mob).setPlayerTargetUUID(player.getUUID());
        ((IFullMoonMob) mob).setEventGeneration(getEventGeneration());
    }


    @SuppressWarnings("deprecation")
    @Nullable
    private <T extends MobEntity & IFullMoonMob> T spawnMob(EntityType<T> entityType, ServerPlayerEntity player, ServerWorld world) {
        Random random = world.getRandom();
        BlockPos playerPos = player.blockPosition();
        BlockPos spawnPos = null;
        final int minDist = 25;

        // The ghost is a special case, and we don't need to care about placement at all really
        if (entityType == ApocalypseEntities.GHOST.get()) {
            BlockPos pos;

            for (int i = 0; i < 10; i++) {
                int startX = random.nextBoolean() ? minDist : -minDist;
                int startZ = random.nextBoolean() ? minDist : -minDist;
                int x = playerPos.getX() + startX + (startX < 1 ? -random.nextInt(40) : random.nextInt(40));
                int z = playerPos.getZ() + startZ + (startZ < 1 ? -random.nextInt(40) : random.nextInt(40));
                pos = new BlockPos(x, 20 + random.nextInt(60), z);

                if (world.isLoaded(pos)) {
                    spawnPos = pos;
                    break;
                }
            }
        }
        else {
            EntitySpawnPlacementRegistry.PlacementType placementType = EntitySpawnPlacementRegistry.getPlacementType(entityType);

            for (int tries = 0; tries < 10; tries++) {
                int startX = random.nextBoolean() ? minDist : -minDist;
                int startZ = random.nextBoolean() ? minDist : -minDist;
                int x = playerPos.getX() + startX + (startX < 1 ? -random.nextInt(46) : random.nextInt(46));
                int z = playerPos.getZ() + startZ + (startZ < 1 ? -random.nextInt(46) : random.nextInt(46));
                int y = world.getHeight(EntitySpawnPlacementRegistry.getHeightmapType(entityType), x, z);
                BlockPos.Mutable pos = new BlockPos(x, y, z).mutable();

                if (world.dimensionType().hasCeiling()) {
                    do {
                        pos.move(Direction.DOWN);
                    }
                    while(!world.getBlockState(pos).isAir(world, pos));

                    do {
                        pos.move(Direction.DOWN);
                    }
                    while(world.getBlockState(pos).isAir(world, pos) && pos.getY() > 0);
                }
                if (placementType == EntitySpawnPlacementRegistry.PlacementType.ON_GROUND) {
                    BlockPos blockpos = pos.below();
                    if (world.getBlockState(blockpos).isPathfindable(world, blockpos, PathType.LAND)) {
                        pos = blockpos.mutable();
                    }
                }
                if (world.isLoaded(pos) && WorldEntitySpawner.isSpawnPositionOk(placementType, world, pos, entityType)) {
                    // We check for collisions around the entity and a bit above if we are dealing with a flying
                    // entity, so we can spawn it in the air a bit above ground to help prevent them getting stuck in the ground.
                    if (isFlyingType(entityType)) {
                        pos = pos.above(20).mutable();

                        if (world.noCollision(entityType.getAABB((double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D)
                                .inflate(0.0D, 2.0D, 0.5D)
                                .move(0.0D, 2.0D, 0.0D))) {
                            spawnPos = pos.immutable();
                            break;
                        }
                    }
                    else {
                        if (world.noCollision(entityType.getAABB((double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D))) {
                            spawnPos = pos.immutable();
                            break;
                        }
                    }
                }
            }
        }
        if (spawnPos == null)
            return null;

        return entityType.spawn(world, null, null, null, spawnPos, SpawnReason.EVENT, true, true);
    }

    private static boolean isFlyingType(EntityType<?> entityType) {
        return ApocalypseEntityTags.FLYING_ENTITIES.contains(entityType);
    }

    @Override
    public void writeAdditional(CompoundNBT data) {
        data.putInt("GracePeriod", gracePeriod);
        data.putInt("TimeNextSpawn", timeUntilNextSpawn);
        data.putInt("SpawnTime", spawnTime);
        data.putInt("EventGeneration", eventGeneration);

        CompoundNBT mobsToSpawn = new CompoundNBT();
        mobsToSpawn.putInt("Ghost", this.mobsToSpawn.getOrDefault(GHOST_ID, 0));
        mobsToSpawn.putInt("Breecher", this.mobsToSpawn.getOrDefault(BREECHER_ID, 0));
        mobsToSpawn.putInt("Grump", this.mobsToSpawn.getOrDefault(GRUMP_ID, 0));
        mobsToSpawn.putInt("Seeker", this.mobsToSpawn.getOrDefault(SEEKER_ID, 0));
        mobsToSpawn.putInt("Destroyer", this.mobsToSpawn.getOrDefault(DESTROYER_ID, 0));

        data.put("MobsToSpawn", mobsToSpawn);
    }

    @Override
    public void read(CompoundNBT data, ServerPlayerEntity player, ServerWorld world) {
        gracePeriod = data.getInt("GracePeriod");
        timeUntilNextSpawn = data.getInt("TimeNextSpawn");
        spawnTime = data.getInt("SpawnTime");
        eventGeneration = data.getInt("EventGeneration");

        CompoundNBT mobsToSpawn = data.getCompound("MobsToSpawn");
        this.mobsToSpawn.put(GHOST_ID, mobsToSpawn.getInt("Ghost"));
        this.mobsToSpawn.put(BREECHER_ID, mobsToSpawn.getInt("Breecher"));
        this.mobsToSpawn.put(GRUMP_ID, mobsToSpawn.getInt("Grump"));
        this.mobsToSpawn.put(SEEKER_ID, mobsToSpawn.getInt("Seeker"));
        this.mobsToSpawn.put(DESTROYER_ID, mobsToSpawn.getInt("Destroyer"));
    }
}
