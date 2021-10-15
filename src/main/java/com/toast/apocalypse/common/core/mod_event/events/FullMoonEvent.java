package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.mod_event.EventType;
import com.toast.apocalypse.common.entity.living.IFullMoonMob;
import com.toast.apocalypse.common.register.ApocalypseEntities;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import com.toast.apocalypse.common.util.StorageUtils;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    /** A List of mobs that have already been spawned and are still alive */
    private final List<MobEntity> currentMobs = new ArrayList<>();

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
    public void update(ServerWorld world, ServerPlayerEntity player) {
        // Tick grace period
        if (this.gracePeriod > 0) {
            this.gracePeriod -= PlayerDifficultyManager.TICKS_PER_UPDATE;
        }
        // Tick time until next mob spawn
        if (this.timeUntilNextSpawn > 0) {
            timeUntilNextSpawn -= PlayerDifficultyManager.TICKS_PER_UPDATE;
        }
        // Update the list of current mobs. Remove any entries of null or dead mobs.
        this.currentMobs.removeIf(mob -> mob == null || !mob.isAlive());

        if (this.canSpawn()) {
            boolean hasMobsLeft = false;

            for (int id : this.mobsToSpawn.keySet()) {
                if (this.mobsToSpawn.get(id) > 0) {
                    hasMobsLeft = true;
                    break;
                }
            }
            this.hasMobsLeft = hasMobsLeft;

            if (!hasMobsLeft)
                return;

            Random random = world.getRandom();
            int mobIndex = this.getRandomMobIndex(random);

            if (mobIndex < 0)
                return;

            int currentCount = this.mobsToSpawn.get(mobIndex);
            this.mobsToSpawn.put(mobIndex, --currentCount);
            this.spawnMob(mobIndex, world, player);

            timeUntilNextSpawn = spawnTime;
        }
    }

    @Override
    public void onEnd() {
        this.currentMobs.clear();
    }

    @Override
    public void stop(ServerWorld world) {
        for (MobEntity mob : this.currentMobs) {
            spawnSmoke(world, mob);
            mob.remove();
        }
    }

    /**
     * Helper method for spawning smoke particles
     * when an existing mob is despawned on player
     * logout or if the player changes dimension.
     */
    private static void spawnSmoke(ServerWorld world, MobEntity mob) {
        world.sendParticles(ParticleTypes.CLOUD, mob.getX(), mob.getY(), mob.getZ(), 4, 0.1, 0.1, 0.1, 0.2);
    }

    /**
     * Returns true if it is time to spawn a new full moon mob.
     */
    private boolean canSpawn() {
        return this.gracePeriod <= 0 && this.hasMobsLeft && this.timeUntilNextSpawn <= 0;
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
        this.mobsToSpawn.put(GHOST_ID, 0);
        this.mobsToSpawn.put(BREECHER_ID, 0);
        this.mobsToSpawn.put(GRUMP_ID, 0);
        this.mobsToSpawn.put(SEEKER_ID, 0);
        this.mobsToSpawn.put(DESTROYER_ID, 0);

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

        for (int mobId : this.mobsToSpawn.keySet()) {
            totalMobCount += this.mobsToSpawn.get(mobId);
        }
        this.spawnTime = totalMobCount <= 0 ? defaultSpawnTime : (10500 - MAX_GRACE_PERIOD) / totalMobCount;
    }

    /**
     * Returns a random mob index for the mob types remaining,
     * or -1 if there are no mob types left to spawn.
     */
    private int getRandomMobIndex(Random random) {
        Integer type = StorageUtils.getRandomMapKeyFiltered(random, this.mobsToSpawn, (id, count) -> count > 0);
        return type != null ? type : -1;
    }

    /**
     * Spawns a full moon mob. The type of mob depends on the mob index given.
     *
     * @param mobType The index of what type of mob to spawn.
     * @param world The world to spawn this mob in.
     * @param player The player to spawn this mob for.
     */
    private void spawnMob(int mobType, ServerWorld world, ServerPlayerEntity player) {
        MobEntity mob;

        switch (mobType) {
            default:
            case GHOST_ID:
                mob = createMob(ApocalypseEntities.GHOST.get(), player, world);
                break;
            case BREECHER_ID:
                mob = createMob(ApocalypseEntities.BREECHER.get(), player, world);
                break;
            case GRUMP_ID:
                mob = createMob(ApocalypseEntities.GRUMP.get(), player, world);
                break;
            case SEEKER_ID:
                mob = createMob(ApocalypseEntities.SEEKER.get(), player, world);
                break;
            case DESTROYER_ID:
                mob = createMob(ApocalypseEntities.DESTROYER.get(), player, world);
                break;
        }
        if (mob == null)
            return;

        ((IFullMoonMob) mob).setPlayerTargetUUID(player.getUUID());
    }

    @Nullable
    private <T extends MobEntity & IFullMoonMob> T createMob(EntityType<T> entityType, ServerPlayerEntity player, ServerWorld world) {
        BlockPos playerPos = player.blockPosition();
        BlockPos spawnPos = null;
        final int minDist = 25;

        if (entityType == ApocalypseEntities.GHOST.get()) {
            Random random = world.getRandom();
            BlockPos pos;

            for (int i = 0; i < 10; i++) {
                int startX = random.nextInt(2) == 1 ? minDist : -minDist;
                int startZ = random.nextInt(2) == 1 ? minDist : -minDist;
                int x = playerPos.getX() + startX + (startX < 1 ? -random.nextInt(46) : random.nextInt(46));
                int z = playerPos.getZ() + startZ + (startZ < 1 ? -random.nextInt(46) : random.nextInt(46));
                pos = new BlockPos(x, 20 + random.nextInt(60), z);

                if (world.isLoaded(pos)) {
                    spawnPos = pos;
                    break;
                }
            }
        }
        else {
            EntitySpawnPlacementRegistry.PlacementType placementType = entityType == ApocalypseEntities.BREECHER.get() ? EntitySpawnPlacementRegistry.PlacementType.ON_GROUND : EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS;
            Random random = world.getRandom();

            for (int i = 0; i < 10; i++) {
                int startX = random.nextInt(2) == 1 ? minDist : -minDist;
                int startZ = random.nextInt(2) == 1 ? minDist : -minDist;
                int x = playerPos.getX() + startX + (startX < 1 ? -random.nextInt(46) : random.nextInt(46));
                int z = playerPos.getZ() + startZ + (startZ < 1 ? -random.nextInt(46) : random.nextInt(46));
                int y = world.getHeight(Heightmap.Type.WORLD_SURFACE, x, z);
                BlockPos pos = new BlockPos(x, y, z);

                if (world.isLoaded(pos) && WorldEntitySpawner.isSpawnPositionOk(placementType, world, pos, entityType)) {
                    if (world.noCollision(entityType.getAABB((double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D))) {
                        spawnPos = pos;
                        break;
                    }
                }
            }
        }
        if (spawnPos == null)
            return null;

        return entityType.spawn(world, null, null, null, spawnPos, SpawnReason.EVENT, true, true);
    }

    @Override
    public void writeAdditional(CompoundNBT data) {
        data.putInt("GracePeriod", this.gracePeriod);
        data.putInt("TimeNextSpawn", this.timeUntilNextSpawn);
        data.putInt("SpawnTime", this.spawnTime);

        CompoundNBT mobsToSpawn = new CompoundNBT();
        mobsToSpawn.putInt("Ghost", this.mobsToSpawn.getOrDefault(GHOST_ID, 0));
        mobsToSpawn.putInt("Breecher", this.mobsToSpawn.getOrDefault(BREECHER_ID, 0));
        mobsToSpawn.putInt("Grump", this.mobsToSpawn.getOrDefault(GRUMP_ID, 0));
        mobsToSpawn.putInt("Seeker", this.mobsToSpawn.getOrDefault(SEEKER_ID, 0));
        mobsToSpawn.putInt("Destroyer", this.mobsToSpawn.getOrDefault(DESTROYER_ID, 0));

        CompoundNBT currentMobs = new CompoundNBT();
        int id = 0;
        for (MobEntity mobEntity : this.currentMobs) {
            if (mobEntity != null && mobEntity.isAlive()) {
                currentMobs.put(String.valueOf(id), mobEntity.serializeNBT());
                ++id;
            }
        }
        data.put("MobsToSpawn", mobsToSpawn);
        data.put("CurrentMobs", currentMobs);
    }

    @Override
    public void read(CompoundNBT data, ServerWorld world) {
        this.gracePeriod = data.getInt("GracePeriod");
        this.timeUntilNextSpawn = data.getInt("TimeNextSpawn");
        this.spawnTime = data.getInt("SpawnTime");

        CompoundNBT mobsToSpawn = data.getCompound("MobsToSpawn");
        this.mobsToSpawn.put(GHOST_ID, mobsToSpawn.getInt("Ghost"));
        this.mobsToSpawn.put(BREECHER_ID, mobsToSpawn.getInt("Breecher"));
        this.mobsToSpawn.put(GRUMP_ID, mobsToSpawn.getInt("Grump"));
        this.mobsToSpawn.put(SEEKER_ID, mobsToSpawn.getInt("Seeker"));
        this.mobsToSpawn.put(DESTROYER_ID, mobsToSpawn.getInt("Destroyer"));

        CompoundNBT currentMobs = data.getCompound("CurrentMobs");

        // TODO - This shit is a terrible idea, think of something else
        /*
        for (String s : currentMobs.getAllKeys()) {
            CompoundNBT entityTag = currentMobs.getCompound(s);
            MobEntity mob = (MobEntity) EntityType.loadEntityRecursive(entityTag, world, (entity) -> entity);
            if (mob != null) {
                spawnSmoke(world, mob);
                this.currentMobs.add(mob);
            }
        }

         */
    }
}
