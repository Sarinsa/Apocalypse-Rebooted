package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.mod_event.EventType;
import com.toast.apocalypse.common.entity.living.*;
import com.toast.apocalypse.common.register.ApocalypseEntities;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import com.toast.apocalypse.common.util.StorageUtils;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WanderingTraderSpawner;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.ChunkCoordComparator;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.NoteBlockEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;

/**
 * The full moon event. This event can occur every 8 days in game and will interrupt any other event and can not be interrupted
 * by any other event.<br>
 * These are often referred to as "full moon sieges" in other parts of the code and in the properties file.
 */
public final class FullMoonEvent extends AbstractEvent {

    private static final int MAX_GRACE_PERIOD = 800;

    /** The weights for each full moon mob */
    public static double GHOST_SPAWN_WEIGHT;
    public static double BREECHER_SPAWN_WEIGHT;
    public static double GRUMP_SPAWN_WEIGHT;
    public static double SEEKER_SPAWN_WEIGHT;
    public static double DESTROYER_SPAWN_WEIGHT;

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
    /** Whether or not there are any mobs left to spawn */
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
        this.currentMobs.removeIf(mob -> mob == null || mob.isDeadOrDying());

        if (this.canSpawn()) {
            boolean hasMobsLeft = false;

            for (int id : this.mobsToSpawn.keySet()) {
                if (this.mobsToSpawn.get(id) > 0) {
                    hasMobsLeft = true;
                    break;
                }
            }
            this.hasMobsLeft = hasMobsLeft;

            if (hasMobsLeft)
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

        this.mobsToSpawn.put(GHOST_ID, 0);
        this.mobsToSpawn.put(BREECHER_ID, 0);
        this.mobsToSpawn.put(GRUMP_ID, 0);
        this.mobsToSpawn.put(SEEKER_ID, 0);
        this.mobsToSpawn.put(DESTROYER_ID, 0);

        if (GHOST_START >= 0L && GHOST_START <= scaledDifficulty) {
            effectiveDifficulty = (scaledDifficulty - GHOST_START) / MOB_COUNT_TIME_SPAN;
            count = GHOST_MIN_COUNT + (int) (GHOST_ADDITIONAL_COUNT * effectiveDifficulty);
            this.mobsToSpawn.put(GHOST_ID, count);
        }
        if (BREECHER_START >= 0L && BREECHER_START <= scaledDifficulty) {
            effectiveDifficulty = (scaledDifficulty - BREECHER_START) / MOB_COUNT_TIME_SPAN;
            count = BREECHER_MIN_COUNT + (int) (BREECHER_ADDITIONAL_COUNT * effectiveDifficulty);
            this.mobsToSpawn.put(BREECHER_ID, count);
        }
        if (GRUMP_START >= 0L && GRUMP_START <= scaledDifficulty) {
            effectiveDifficulty = (scaledDifficulty - GRUMP_START) / MOB_COUNT_TIME_SPAN;
            count = GRUMP_MIN_COUNT + (int) (GRUMP_ADDITIONAL_COUNT * effectiveDifficulty);
            this.mobsToSpawn.put(GRUMP_ID, count);
        }
        if (SEEKER_START >= 0L && SEEKER_START <= scaledDifficulty) {
            effectiveDifficulty = (scaledDifficulty - SEEKER_START) / MOB_COUNT_TIME_SPAN;
            count = SEEKER_MIN_COUNT + (int) (SEEKER_ADDITIONAL_COUNT * effectiveDifficulty);
            this.mobsToSpawn.put(SEEKER_ID, count);
        }
        if (DESTROYER_START >= 0L && DESTROYER_START <= scaledDifficulty) {
            effectiveDifficulty = (scaledDifficulty - DESTROYER_START) / MOB_COUNT_TIME_SPAN;
            count = DESTROYER_MIN_COUNT + (int) (DESTROYER_ADDITIONAL_COUNT * effectiveDifficulty);
            this.mobsToSpawn.put(DESTROYER_ID, count);
        }
    }

    /** Calculates the interval between each mob spawn */
    private void calculateSpawnTime() {
        int totalMobCount = 0;

        for (int mobId : this.mobsToSpawn.keySet()) {
            totalMobCount += this.mobsToSpawn.get(mobId);
        }
        this.spawnTime = (11000 - MAX_GRACE_PERIOD) / totalMobCount;
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

        ((IFullMoonMob) mob).setPlayerTarget(player);
        this.currentMobs.add(mob);
    }

    @Nullable
    private <T extends MobEntity> T createMob(EntityType<T> entityType, ServerPlayerEntity player, ServerWorld world) {
        BlockPos spawnPos = player.blockPosition();

        // Ghosts clip through blocks, so no need to do anything
        // big for finding a spawn location.
        if (entityType == ApocalypseEntities.GHOST.get()) {
            Random random = world.getRandom();
            BlockPos pos;

            for (int i = 0; i < 10; i++) {
                pos = new BlockPos(random.nextGaussian() * 60, 20 + random.nextInt(60), player.getZ() + random.nextGaussian() * 60);

                if (world.isLoaded(pos)) {
                    spawnPos = pos;
                    break;
                }
            }
        }
        else {
            EntitySpawnPlacementRegistry.PlacementType placementType = entityType == ApocalypseEntities.BREECHER.get() ? EntitySpawnPlacementRegistry.PlacementType.ON_GROUND : EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS;
            Random random = world.getRandom();
            final int minDist = 25;

            // Look for a spawn position with a 70 block radius (Must be at least 25 blocks away from the player)
            for (int i = 0; i < 10; i++) {
                int startX = random.nextInt(2) == 0 ? minDist : -minDist;
                int startZ = random.nextInt(2) == 0 ? minDist : -minDist;
                int x = (int) player.getX() + startX + startX < 0 ? -random.nextInt(46) : random.nextInt(46);
                int z = (int) player.getZ() + startZ + startZ < 0 ? -random.nextInt(46) : random.nextInt(46);
                int y = world.getHeight(Heightmap.Type.WORLD_SURFACE, x, z);
                BlockPos pos = new BlockPos(x, y, z);

                if (world.isLoaded(pos) && WorldEntitySpawner.isSpawnPositionOk(placementType, world, pos, entityType)) {
                    spawnPos = pos;
                    break;
                }
            }
        }
        return entityType.spawn(world, null, null, null, spawnPos, SpawnReason.EVENT, true, true);
    }

    @Override
    public CompoundNBT write(CompoundNBT data) {
        data = super.write(data);
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
            if (mobEntity != null && !mobEntity.isDeadOrDying()) {
                currentMobs.put(String.valueOf(id), mobEntity.serializeNBT());
                ++id;
            }
        }
        data.put("MobsToSpawn", mobsToSpawn);
        data.put("CurrentMobs", currentMobs);

        return data;
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

        for (String s : currentMobs.getAllKeys()) {
            CompoundNBT entityTag = currentMobs.getCompound(s);
            MobEntity mob = (MobEntity) EntityType.loadEntityRecursive(entityTag, world, (entity) -> {
                world.addFreshEntity(entity);
                return entity;
            });
            if (mob != null) {
                spawnSmoke(world, mob);
                this.currentMobs.add(mob);
            }
        }
    }
}
