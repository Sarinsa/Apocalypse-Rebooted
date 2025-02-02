package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.mod_event.EventType;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.entity.living.IFullMoonMob;
import com.toast.apocalypse.common.tag.ApocalypseEntityTags;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.DataStructureUtils;
import com.toast.apocalypse.common.util.References;
import fathertoast.crust.api.config.common.value.EntityList;
import fathertoast.crust.api.config.common.value.RegistryEntryValueList;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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

    /** Time until mobs can start spawning. */
    private int gracePeriod;
    /** The time between each mob spawn */
    private int spawnTime = 600;
    /** The time until the next mob should be spawned for the player */
    private int timeUntilNextSpawn = 0;
    /** Whether there are any mobs left to spawn */
    private boolean hasMobsLeft = true;

    /** A map containing all the full moon mobs that will be spawned for the player */
    private final Map<ResourceLocation, Integer> mobsToSpawn = new HashMap<>();



    public FullMoonEvent(EventType<?> type) {
        super(type);
    }

    @Override
    public void onStart(MinecraftServer server, ServerPlayer player) {
        long difficulty = CapabilityHelper.getPlayerDifficulty(player);
        calculateMobs(difficulty);
        calculateSpawnTime();
        gracePeriod = MAX_GRACE_PERIOD;
    }

    @Override
    public void update(ServerLevel level, ServerPlayer player, PlayerDifficultyManager difficultyManager) {
        // Tick grace period
        if (gracePeriod > 0) {
            gracePeriod -= PlayerDifficultyManager.TICKS_PER_UPDATE;
        }
        // Tick time until next mob spawn
        if (timeUntilNextSpawn > 0) {
            timeUntilNextSpawn -= PlayerDifficultyManager.TICKS_PER_UPDATE;
        }

        if (canSpawn()) {
            boolean hasMobsLeft = false;

            for (ResourceLocation id : mobsToSpawn.keySet()) {
                if (mobsToSpawn.get(id) > 0) {
                    hasMobsLeft = true;
                    break;
                }
            }
            this.hasMobsLeft = hasMobsLeft;

            if (!hasMobsLeft)
                return;

            RandomSource random = level.getRandom();
            ResourceLocation mobId = getRandomMobID(random);

            if (mobId == null)
                return;

            int currentCount = mobsToSpawn.get(mobId);
            mobsToSpawn.put(mobId, --currentCount);

            if (level.getDifficulty() != Difficulty.PEACEFUL) {
                spawnMobFromId(mobId, level, player);
            }
            timeUntilNextSpawn = spawnTime;
        }
    }

    @Override
    public void onEnd(MinecraftServer server, ServerPlayer player) {

    }

    @Override
    public void stop(ServerLevel level) {}

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
        final double difficultyPerIncrease = ApocalypseConfig.LUNAR_SIEGE.SIEGE_MOB_PROPS.difficultyPerIncrease.get();
        final double scaledDifficulty = (double) difficulty / References.DAY_LENGTH;

        double effectiveDifficulty;
        int count;

        final RegistryEntryValueList<EntityType<?>> entryList = ApocalypseConfig.LUNAR_SIEGE.SIEGE_MOB_PROPS.mobSpawnSettings.get();

        for (RegistryValueEntry<EntityType<?>> entry : entryList.getEntries()) {
            final double startDifficulty = entry.VALUES[0];
            final int minSpawnCount = (int) entry.VALUES[1];
            final int maxSpawnCount = (int) entry.VALUES[2];
            final double additionalSpawnCount = entry.VALUES[3];

            if (startDifficulty >= 0 && startDifficulty <= scaledDifficulty) {

                effectiveDifficulty = (scaledDifficulty - startDifficulty) / difficultyPerIncrease;
                count = minSpawnCount + (int) (additionalSpawnCount * effectiveDifficulty);
                mobsToSpawn.put(entry.REG_KEY, Math.min(count, maxSpawnCount));
            }
        }
    }

    /** Calculates the interval between each mob spawn */
    private void calculateSpawnTime() {
        final int defaultSpawnTime = 500;
        int totalMobCount = 0;

        for (ResourceLocation mobId : mobsToSpawn.keySet()) {
            totalMobCount += mobsToSpawn.get(mobId);
        }
        this.spawnTime = totalMobCount <= 0 ? defaultSpawnTime : (10500 - MAX_GRACE_PERIOD) / totalMobCount;
    }

    /**
     * Returns a random mob id for the mob types remaining,
     * or null if there are no mobs left to spawn.
     */
    @Nullable
    private ResourceLocation getRandomMobID(RandomSource random) {
        return DataStructureUtils.randomMapKeyFiltered(random, mobsToSpawn, (id, count) -> count > 0);
    }

    /**
     * Spawns a full moon mob. The type of mob depends on the mob id given.
     *
     * @param mobId The registry key of the entity type of the mob to spawn.
     * @param level The world to spawn this mob in.
     * @param player The player to spawn this mob for.
     */
    private void spawnMobFromId(ResourceLocation mobId, ServerLevel level, ServerPlayer player) {
        EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(mobId);

        if (entityType == null) return;

        Entity entity = spawnMob(entityType, player, level);

        if (entity == null)
            return;

        if (entity instanceof IFullMoonMob fullMoonMob) {
            fullMoonMob.setPlayerTargetUUID(player.getUUID());
            fullMoonMob.setPlayerDeathCount(getPlayerDeathCount());
        }
        level.addFreshEntity(entity);
    }

    @Nullable
    private Entity spawnMob(EntityType<?> entityType, ServerPlayer player, ServerLevel level) {
        RandomSource random = level.getRandom();
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

                if (level.isLoaded(pos)) {
                    spawnPos = pos;
                    break;
                }
            }
        }
        else {
            SpawnPlacements.Type placementType = SpawnPlacements.getPlacementType(entityType);

            for (int tries = 0; tries < 10; tries++) {
                int startX = random.nextBoolean() ? minDist : -minDist;
                int startZ = random.nextBoolean() ? minDist : -minDist;
                int x = playerPos.getX() + startX + (startX < 1 ? -random.nextInt(46) : random.nextInt(46));
                int z = playerPos.getZ() + startZ + (startZ < 1 ? -random.nextInt(46) : random.nextInt(46));
                int y = level.getHeight(SpawnPlacements.getHeightmapType(entityType), x, z);
                BlockPos.MutableBlockPos pos = new BlockPos(x, y, z).mutable();

                if (level.dimensionType().hasCeiling()) {
                    do {
                        pos.move(Direction.DOWN);
                    }
                    while(!level.getBlockState(pos).isAir());

                    do {
                        pos.move(Direction.DOWN);
                    }
                    while(level.getBlockState(pos).isAir() && pos.getY() > 0);
                }
                if (placementType == SpawnPlacements.Type.ON_GROUND) {
                    BlockPos blockpos = pos.below();
                    if (level.getBlockState(blockpos).isPathfindable(level, blockpos, PathComputationType.LAND)) {
                        pos = blockpos.mutable();
                    }
                }
                if (level.isLoaded(pos) && NaturalSpawner.isSpawnPositionOk(placementType, level, pos, entityType)) {
                    // We check for collisions around the entity and a bit above if we are dealing with a flying
                    // entity, so we can spawn it in the air a bit above ground to help prevent them getting stuck in the ground.
                    if (isFlyingType(entityType)) {
                        pos = pos.above(20).mutable();

                        if (level.noCollision(entityType.getAABB((double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D)
                                .inflate(0.0D, 2.0D, 0.5D)
                                .move(0.0D, 2.0D, 0.0D))) {
                            spawnPos = pos.immutable();
                            break;
                        }
                    }
                    else {
                        if (level.noCollision(entityType.getAABB((double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D))) {
                            spawnPos = pos.immutable();
                            break;
                        }
                    }
                }
            }
        }
        if (spawnPos == null)
            return null;

        return entityType.create(level, null, null, spawnPos, MobSpawnType.EVENT, true, true);
    }

    private static boolean isFlyingType(EntityType<?> entityType) {
        return entityType.is(ApocalypseEntityTags.FLYING_ENTITIES);
    }

    @Override
    public void writeAdditional(CompoundTag data) {
        data.putInt("GracePeriod", gracePeriod);
        data.putInt("TimeNextSpawn", timeUntilNextSpawn);
        data.putInt("SpawnTime", spawnTime);
        data.putInt("PlayerDeathCount", deathCount);

        CompoundTag spawnsTag = new CompoundTag();

        mobsToSpawn.forEach((id, count) -> {
            spawnsTag.putInt(id.toString(), count);
        });

        data.put("MobsToSpawn", spawnsTag);
    }

    @Override
    public void read(CompoundTag data, ServerPlayer player, ServerLevel level) {
        gracePeriod = data.getInt("GracePeriod");
        timeUntilNextSpawn = data.getInt("TimeNextSpawn");
        spawnTime = data.getInt("SpawnTime");
        deathCount = data.getInt("PlayerDeathCount");

        CompoundTag spawnsTag = data.getCompound("MobsToSpawn");
        Set<String> keys = spawnsTag.getAllKeys();

        for (String key : keys) {
            ResourceLocation id = ResourceLocation.tryParse(key);

            if (id == null) continue;

            mobsToSpawn.put(id, Math.max(0, spawnsTag.getInt(key)));
        }
    }
}
