package com.toast.apocalypse.common.core.mod_event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.entity.living.IFullMoonMob;
import com.toast.apocalypse.common.util.References;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * The full moon event. This event can occur every 8 days in game and will interrupt any other event and can not be interrupted
 * by any other event.<br>
 * These are often referred to as "full moon sieges" in other parts of the code and in the properties file.
 */
public class FullMoonEvent extends AbstractEvent {

    /** The weights for each full moon mob */
    public static int GHOST_SPAWN_WEIGHT;
    public static int BREECHER_SPAWN_WEIGHT;
    public static int GRUMP_SPAWN_WEIGHT;
    public static int SEEKER_SPAWN_WEIGHT;
    public static int DESTROYER_SPAWN_WEIGHT;

    /** Time until mobs can start spawning. */
    private int gracePeriod;
    /** The time until the next time a mob should be spawned for a player */
    private int timeUntilNextSpawn;
    /** A map containing all the full moon mobs that will be spawned for each player */
    private final HashMap<UUID, HashMap<Class<? extends IFullMoonMob>, Integer>> mobsToSpawn = new HashMap<>();

    /**
     * Checks if the event should be postponed.
     * The full moon sieges should for example not
     * be skippable on servers, so to prevent players
     * from just logging out during full moons and wait
     * out the siege event, this is used to pause
     * the event as long as the server's player count is 0.
     */
    private boolean waiting;

    public FullMoonEvent(int id) {
        super(id);
    }

    @Override
    public String getEventStartMessage() {
        return References.FULL_MOON;
    }

    @Override
    public void onStart(MinecraftServer server) {
        this.gracePeriod = 500;
        this.timeUntilNextSpawn = 0;

        for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
            this.calculateMobsForPlayer(player);
        }
    }

    @Override
    public void update() {
        if (this.isWaiting())
            return;

        if (this.gracePeriod > 0) {
            this.gracePeriod -= PlayerDifficultyManager.TICKS_PER_UPDATE;
        }

        if (this.timeUntilNextSpawn > 0) {
            this.timeUntilNextSpawn -= PlayerDifficultyManager.TICKS_PER_UPDATE;
        }
    }

    @Override
    public void update(ServerWorld world) {
        // Stop the full moon siege when it is day.
        if (world.isDay()) {
            this.onEnd();
            return;
        }

        // Pause the event if no players are online.
        // At least ONE player should suffer! >:D
        this.setWaiting(world.getServer().getPlayerCount() < 1);
    }

    @Override
    public void update(PlayerEntity player) {
        if (!this.canSpawn())
            return;

        if (this.mobsToSpawn.containsKey(player.getUUID())) {

        }
    }

    @Override
    public void onEnd() {
        this.mobsToSpawn.clear();
        this.setWaiting(false);
    }

    private boolean canSpawn() {
        return !this.isWaiting() && this.gracePeriod <= 0 && this.timeUntilNextSpawn <= 0;
    }

    private void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    private boolean isWaiting() {
        return this.waiting;
    }

    private void calculateMobsForPlayer(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();

    }

    @Override
    public CompoundNBT write(CompoundNBT data) {
        data.putInt("EventId", this.getId());
        data.putInt("GracePeriod", this.gracePeriod);
        data.putInt("TimeNextSpawn", this.timeUntilNextSpawn);

        CompoundNBT playerMobs = new CompoundNBT();

        for (UUID uuid : this.mobsToSpawn.keySet()) {
            HashMap<Class<? extends IFullMoonMob>, Integer> mobCounts = this.mobsToSpawn.get(uuid);
            CompoundNBT mobCountTag = new CompoundNBT();

            for (Class<?> clazz : mobCounts.keySet()) {
                mobCountTag.putInt(clazz.getSimpleName(), mobCounts.get(clazz));
            }
            playerMobs.put(uuid.toString(), mobCountTag);
        }
        data.put("PlayerMobs", playerMobs);

        Apocalypse.LOGGER.info("Event tag: " + data);
        return data;
    }

    @Override
    public void read(CompoundNBT data) {
        this.gracePeriod = data.getInt("GracePeriod");
        this.timeUntilNextSpawn = data.getInt("TimeNextSpawn");

        CompoundNBT playerMobs = data.getCompound("PlayerMobs");
        Collection<UUID> uuids = new ArrayList<>();

        for (String s : playerMobs.getAllKeys()) {
            uuids.add(UUID.fromString(s));
        }

        
    }
}
