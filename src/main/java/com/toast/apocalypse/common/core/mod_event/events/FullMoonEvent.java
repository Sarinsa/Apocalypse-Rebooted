package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.mod_event.EventType;
import com.toast.apocalypse.common.entity.living.*;
import com.toast.apocalypse.common.util.CapabilityHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * The full moon event. This event can occur every 8 days in game and will interrupt any other event and can not be interrupted
 * by any other event.<br>
 * These are often referred to as "full moon sieges" in other parts of the code and in the properties file.
 */
public class FullMoonEvent extends AbstractEvent {

    /** The weights for each full moon mob */
    public static double GHOST_SPAWN_WEIGHT;
    public static double BREECHER_SPAWN_WEIGHT;
    public static double GRUMP_SPAWN_WEIGHT;
    public static double SEEKER_SPAWN_WEIGHT;
    public static double DESTROYER_SPAWN_WEIGHT;

    /** The difficulty until mob counts increase */
    public static double MOB_COUNT_TIME_SPAN;

    /** The starting difficulty for when the various full moon mobs can start spawning */
    public static long GHOST_START;
    public static long BREECHER_START;
    public static long GRUMP_START;
    public static long SEEKER_START;
    public static long DESTROYER_START;

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
    private int spawnTime;
    /** The time until the next mob should be spawned for the player */
    private int timeUntilNextSpawn;
    /** A map containing all the full moon mobs that will be spawned for the player */
    private final HashMap<Integer, Integer> mobsToSpawn = new HashMap<>();
    /** A List of mobs that have already been spawned and are still alive */
    private final List<MobEntity> currentMobs = new ArrayList<>();

    public FullMoonEvent(EventType<?> type, ServerPlayerEntity player) {
        super(type, player);
    }

    @Override
    public void onStart(MinecraftServer server) {
        long difficulty = CapabilityHelper.getPlayerDifficulty(this.player);
        this.calculateMobs(difficulty);
        this.gracePeriod = 2000;
    }

    @Override
    public void update(ServerWorld world) {
        if (this.gracePeriod > 0) {
            --this.gracePeriod;
        }
        if (this.timeUntilNextSpawn > 0) {
            --timeUntilNextSpawn;
        }

        if (this.canSpawn()) {
            Random random = world.getRandom();
            int mobId = this.mobsToSpawn.get(random.nextInt(this.mobsToSpawn.size()));
            this.mobsToSpawn.put(mobId, this.mobsToSpawn.get(mobId) - 1);

            if (this.mobsToSpawn.get(mobId) <= 1) {
                this.mobsToSpawn.remove(mobId);
            }
            this.spawnMob(mobId, world);
            timeUntilNextSpawn = spawnTime;
        }
    }

    @Override
    public void update(PlayerEntity player) {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public void stop() {
        ServerWorld world = this.player.getLevel();
        for (MobEntity mob : this.currentMobs) {
            spawnSmoke(world, mob);
            mob.remove();
        }
    }

    private static void spawnSmoke(ServerWorld world, MobEntity mob) {
        world.sendParticles(ParticleTypes.CLOUD, mob.getX(), mob.getY(), mob.getZ(), 4, 0.1, 0.1, 0.1, 0.2);
    }

    private boolean canSpawn() {
        boolean canSpawn = this.gracePeriod <= 0 && this.timeUntilNextSpawn <= 0 && !this.mobsToSpawn.isEmpty();
        Apocalypse.LOGGER.info("Can spawn: " + canSpawn);
        return canSpawn;
    }

    private void calculateMobs(long difficulty) {
        double effectiveDifficulty;
        this.mobsToSpawn.put(GHOST_ID, 0);
        this.mobsToSpawn.put(BREECHER_ID, 0);
        this.mobsToSpawn.put(GRUMP_ID, 0);
        this.mobsToSpawn.put(SEEKER_ID, 0);
        this.mobsToSpawn.put(DESTROYER_ID, 0);

        if (GHOST_START >= 0L && GHOST_START <= difficulty) {
            effectiveDifficulty = (double) (difficulty - GHOST_START) / MOB_COUNT_TIME_SPAN;
            int count = GHOST_MIN_COUNT + (int) (GHOST_ADDITIONAL_COUNT * effectiveDifficulty);
            this.mobsToSpawn.put(GHOST_ID, count);
        }
        if (BREECHER_START >= 0L && BREECHER_START <= difficulty) {
            effectiveDifficulty = (double) (difficulty - BREECHER_START) / MOB_COUNT_TIME_SPAN;
            int count = BREECHER_MIN_COUNT + (int) (BREECHER_ADDITIONAL_COUNT * effectiveDifficulty);
            this.mobsToSpawn.put(BREECHER_ID, count);
        }
        if (GRUMP_START >= 0L && GRUMP_START <= difficulty) {
            effectiveDifficulty = (double) (difficulty - GRUMP_START) / MOB_COUNT_TIME_SPAN;
            int count = GRUMP_MIN_COUNT + (int) (GRUMP_ADDITIONAL_COUNT * effectiveDifficulty);
            this.mobsToSpawn.put(GRUMP_ID, count);
        }
        if (SEEKER_START >= 0L && SEEKER_START <= difficulty) {
            effectiveDifficulty = (double) (difficulty - SEEKER_START) / MOB_COUNT_TIME_SPAN;
            int count = SEEKER_MIN_COUNT + (int) (SEEKER_ADDITIONAL_COUNT * effectiveDifficulty);
            this.mobsToSpawn.put(SEEKER_ID, count);
        }
        if (DESTROYER_START >= 0L && DESTROYER_START <= difficulty) {
            effectiveDifficulty = (double) (difficulty - DESTROYER_START) / MOB_COUNT_TIME_SPAN;
            int count = DESTROYER_MIN_COUNT + (int) (DESTROYER_ADDITIONAL_COUNT * effectiveDifficulty);
            this.mobsToSpawn.put(DESTROYER_ID, count);
        }
        this.spawnTime = 600;
    }

    private void spawnMob(int mobType, ServerWorld world) {
        PlayerEntity player = this.player;
        Random random = world.getRandom();
        MobEntity mob;

        switch (mobType) {
            default:
            case GHOST_ID:
                mob = new GhostEntity(world, player);
                break;
            case BREECHER_ID:
                mob = new BreecherEntity(world, player);
                break;
            case GRUMP_ID:
                mob = new GrumpEntity(world, player);
                break;
            case SEEKER_ID:
                mob = new SeekerEntity(world, player);
                break;
            case DESTROYER_ID:
                mob = new DestroyerEntity(world, player);
                break;
        }
        // Ghosts can pass through blocks, so we can just place it so to speak anywhere.
        if (mob instanceof GhostEntity) {
            mob.setPos(player.getX() + (random.nextGaussian() * 60), random.nextInt(150), player.getZ() + (random.nextGaussian() * 60));
        }
        else {
            mob.setPos(player.getX(), player.getY(), player.getZ());
        }
        ((IFullMoonMob) mob).setPlayerTarget(player);
        this.currentMobs.add(mob);
    }

    @Override
    public CompoundNBT write(CompoundNBT data) {
        data = super.write(data);
        data.putInt("GracePeriod", this.gracePeriod);

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
    public void read(CompoundNBT data) {
        this.gracePeriod = data.getInt("GracePeriod");

        CompoundNBT mobsToSpawn = data.getCompound("MobsToSpawn");
        this.mobsToSpawn.put(GHOST_ID, mobsToSpawn.getInt("Ghost"));
        this.mobsToSpawn.put(BREECHER_ID, mobsToSpawn.getInt("Breecher"));
        this.mobsToSpawn.put(GRUMP_ID, mobsToSpawn.getInt("Grump"));
        this.mobsToSpawn.put(SEEKER_ID, mobsToSpawn.getInt("Seeker"));
        this.mobsToSpawn.put(DESTROYER_ID, mobsToSpawn.getInt("Destroyer"));

        CompoundNBT currentMobs = data.getCompound("CurrentMobs");

        for (String s : currentMobs.getAllKeys()) {
            CompoundNBT entityTag = currentMobs.getCompound(s);
            MobEntity mob = (MobEntity) EntityType.loadEntityRecursive(entityTag, this.player.level, (entity) -> {
                this.player.level.addFreshEntity(entity);
                return entity;
            });
            spawnSmoke(this.player.getLevel(), mob);
            this.currentMobs.add(mob);
        }
    }
}
