package com.toast.apocalypse.common.core;

import com.toast.apocalypse.api.IDifficultyProvider;
import com.toast.apocalypse.common.core.mod_event.AbstractEvent;
import com.toast.apocalypse.common.core.mod_event.EventRegister;
import com.toast.apocalypse.common.event.CommonConfigReloadListener;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.List;

/**
 * The major backbone of Apocalypse, this class manages everything to do with the world difficulty - increases it over time,
 * saves and loads it to and from the disk, and notifies clients of changes to it.<br>
 * In addition, it houses many helper methods related to world difficulty and save data.
 */
public final class WorldDifficultyManager implements IDifficultyProvider {

    /** Number of ticks per update. */
    public static final int TICKS_PER_UPDATE = 5;

    /** These are updated when the mod config is loaded/reloaded
     *
     *  @see CommonConfigReloadListener#updateInfo()
     */
    public static long MAX_DIFFICULTY;
    public static boolean MULTIPLAYER_DIFFICULTY_SCALING;
    public static double DIFFICULTY_MULTIPLIER;
    public static double SLEEP_PENALTY;
    public static double DIMENSION_PENALTY;

    public static List<RegistryKey<World>> DIMENSION_PENALTY_LIST;

    /** Server instance */
    private MinecraftServer server;

    /** Map of all worlds to their respective player-based difficulties. */
    private static final HashMap<RegistryKey<World>, WorldDifficultyData> WORLD_MAP = new HashMap<>();

    /** The current running event. */
    private AbstractEvent currentEvent = null;

    /** Used to prevent full moons from constantly happening. */
    private boolean checkedFullMoon;

    /** The world difficulty */
    private long worldDifficulty;

    /** Used to temporarily store the new
     *  so that it will not be updated
     *  before we checked if we have
     *  reached the maximum difficulty
     */
    private long nextDifficulty;

    /** The world difficulty multiplier */
    private double worldDifficultyRateMul;
    private double lastWorldDifficultyRate;

    /** Time until next server tick update. */
    private int timeUntilUpdate = 0;


    /** Fetch the server */
    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        this.server = event.getServer();
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        if (this.server.overworld().dimension().equals(World.OVERWORLD)) {
            this.load();
        }
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        this.cleanup();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        NetworkHelper.sendUpdateWorldDifficulty(this.worldDifficulty);
        NetworkHelper.sendUpdateWorldDifficultyRate(this.worldDifficultyRateMul);
    }

    /**
     * Called each game tick.
     * TickEvent.Type type = the type of tick.
     * Side side = the side this tick is on.
     * TickEvent.Phase phase = the phase of this tick (START, END).
     *
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Counter to update the world
            if (++this.timeUntilUpdate >= TICKS_PER_UPDATE) {
                this.timeUntilUpdate = 0;
                // Update active event
                if (this.currentEvent != null) {
                    this.currentEvent.update();
                }

                MinecraftServer server = this.server;
                Iterable<ServerWorld> worlds = server.getAllLevels();
                // Update difficulty rate, scaled per person
                this.worldDifficultyRateMul = MULTIPLAYER_DIFFICULTY_SCALING ? 1.0D : server.getPlayerCount();

                if (this.worldDifficultyRateMul > 1.0) {
                    this.worldDifficultyRateMul = (this.worldDifficultyRateMul - 1.0) * DIFFICULTY_MULTIPLIER + 1.0;
                }
                Apocalypse.LOGGER.info("Difficulty rate multiplier: " + this.worldDifficultyRateMul);
                // Apply dimension difficulty rate penalty if any player is in another dimension
                if (DIMENSION_PENALTY > 0.0D) {
                    for (PlayerEntity player : server.getPlayerList().getPlayers()) {
                        if (!player.isSpectator() && DIMENSION_PENALTY_LIST.contains(player.getCommandSenderWorld().dimension())) {
                            this.worldDifficultyRateMul *= 1.0 + DIMENSION_PENALTY;
                            break;
                        }
                    }
                }
                Apocalypse.LOGGER.info("Dimension penalty: " + DIMENSION_PENALTY);

                this.nextDifficulty = worldDifficulty;
                this.nextDifficulty += WorldDifficultyManager.TICKS_PER_UPDATE * this.worldDifficultyRateMul;

                // Update each world
                long mostSkippedTime = 0L;
                for (ServerWorld world : worlds) {
                    mostSkippedTime = this.updateWorld(world, mostSkippedTime);
                }
                // Handle sleep penalty
                if (mostSkippedTime > 20L) {
                    this.nextDifficulty += mostSkippedTime * SLEEP_PENALTY * this.worldDifficultyRateMul;
                    // Send skipped time messages
                    for (PlayerEntity playerEntity : server.getPlayerList().getPlayers()) {
                          playerEntity.displayClientMessage(new TranslationTextComponent(References.SLEEP_PENALTY), true);
                    }
                }
                // Only update difficulty if it is
                // below the maximum level
                if (this.nextDifficulty < MAX_DIFFICULTY) {
                    this.worldDifficulty = nextDifficulty;
                    this.updateWorldDifficulty();
                }
            }
            // TODO: Move to separate event listener
            // Initialize any spawned entities
            /*
            if (!WorldDifficultyManager.ENTITY_STACK.isEmpty()) {
                int count = 10;
                EntityLivingBase entity;
                while (count-- > 0) {
                    entity = WorldDifficultyManager.ENTITY_STACK.pollFirst();
                    if (entity == null) {
                        break;
                    }
                    EventHandler.initializeEntity(entity);
                    entity.getEntityData().setByte(WorldDifficultyManager.TAG_INIT, (byte) 1);
                }
            }

             */
        }
    }

    public static boolean isFullMoon(IWorld world) {
        return world.getMoonBrightness() == 1.0F;
    }

    /**
     * Updates the world and all players and the event in it. Handles difficulty changes.
     *
     * @param world The world to update.
     * @param mostSkippedTime The largest time difference of any other world since the last update.
     * @return If the time difference in this world is larger than mostSkippedTime, then that time difference is
     * 		returned - otherwise mostSkippedTime is returned.
     */
    public long updateWorld(ServerWorld world, long mostSkippedTime) {
        if (world == null)
            return mostSkippedTime;
        WorldDifficultyData worldData = WORLD_MAP.get(world.dimension());

        // Check for time jumps (aka sleeping in bed)
        long skippedTime = 0L;
        if (world.dimension() == World.OVERWORLD) { // TEST - base time jumps only on overworld
            if (this.worldDifficultyRateMul > 0.0 && worldData != null && SLEEP_PENALTY > 0.0) {
                skippedTime = world.getGameTime() - worldData.lastWorldTime; // normally == 5
            }
        }

        // Starts the full moon event
        if (this.worldDifficulty > 0L && this.currentEvent != EventRegister.FULL_MOON) {
            int dayTime = (int) (world.getGameTime() % 24000);
            if (dayTime < 13000) {
                this.checkedFullMoon = false;
            }
            else if (!this.checkedFullMoon && isFullMoon(world)) {
                this.checkedFullMoon = true;
                this.startEvent(EventRegister.FULL_MOON);
            }
        }

        // Update event and players
        if (this.currentEvent != null) {
            this.currentEvent.update(world);

            for (PlayerEntity playerEntity : world.players()) {
                this.currentEvent.update(playerEntity);
            }
        }

        if (worldData == null) {
            RegistryKey<World> dimensionId = world.dimension();
            WorldDifficultyManager.WORLD_MAP.put(dimensionId, worldData = new WorldDifficultyData(dimensionId));
        }
        worldData.lastWorldTime = world.getGameTime();
        return Math.max(mostSkippedTime, skippedTime);
    }

    /** Helper method for logging. */
    private static void log(Level level, String message) {
        Apocalypse.LOGGER.log(level, "[{}] " + message, WorldDifficultyManager.class.getSimpleName());
    }

    /** Saves the world's difficulty and updates the clients, if needed. */
    private void updateWorldDifficulty() {
        if (this.worldDifficultyRateMul != this.lastWorldDifficultyRate) {
            NetworkHelper.sendUpdateWorldDifficultyRate(this.worldDifficultyRateMul);
            this.lastWorldDifficultyRate = this.worldDifficultyRateMul;
        }
        NetworkHelper.sendUpdateWorldDifficulty(this.worldDifficulty);
        // Might be tad too excessive to write every update
        //this.write();
    }

    public long getDifficulty() {
        return this.worldDifficulty;
    }

    public double getDifficultyRate() {
        return this.worldDifficultyRateMul;
    }

    public void setDifficulty(long difficulty) {
        this.worldDifficulty = difficulty;
    }

    public void setDifficultyRate(double rate) {
        this.worldDifficultyRateMul = rate;
    }

    /** Starts an event, if possible.
     * @param event The event to start.
     */
    public void startEvent(AbstractEvent event) {
        if (event == null)
            return;
        if (this.currentEvent != null) {
            if (!this.currentEvent.canBeInterrupted(event))
                return;
            this.currentEvent.onEnd();
        }
        event.onStart();
        Iterable<ServerWorld> worlds = this.server.getAllLevels();
        for (ServerWorld world : worlds) {
            if (world != null) {
                for (PlayerEntity player : world.players()) {
                    player.displayClientMessage(new TranslationTextComponent(event.getEventStartMessage()), true);
                }
            }
        }
        this.currentEvent = event;
        this.updateWorldDifficulty();
    }

    /** Ends the current active event, if any. */
    public void endEvent() {
        this.currentEvent = null;
        this.updateWorldDifficulty();
    }

    /** Cleans up the references to things in a server when the server stops. */
    public void cleanup() {
        this.save();
        this.server = null;
        this.worldDifficulty = 0L;
        this.checkedFullMoon = false;
    }

    public void load() {
        try {
            // Load difficulty
            this.worldDifficulty = CapabilityHelper.getWorldDifficulty(this.server.overworld());
            CompoundNBT eventData = CapabilityHelper.getEventData(this.server.overworld());

            if (eventData != null && eventData.contains("EventId", 3)) {
                this.currentEvent = EventRegister.EVENTS.get(eventData.getInt("EventId"));
                this.currentEvent.read(eventData);
            }
        }
        catch (Exception e) {
            log(Level.ERROR, "Failed to read world save data! That shouldn't happen.");
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            // Save difficulty
            CapabilityHelper.setWorldDifficulty(this.server.overworld(), this.worldDifficulty);

            CompoundNBT eventData = new CompoundNBT();

            if (this.currentEvent != null) {
                this.currentEvent.write(eventData);
            }
            CapabilityHelper.setEventData(this.server.overworld(), eventData);
        }
        catch (Exception e) {
            log(Level.ERROR, "Failed to write world save data! Not cool beans.");
            e.printStackTrace();
        }
    }

    /** Contains info related to this mod about a world. */
    public static class WorldDifficultyData {

        /** The dimension id of the world. */
        public final RegistryKey<World> worldId;
        /** The last recorded world time of the world. */
        public long lastWorldTime;

        /** Constructs WorldDifficultyData for a world to store information needed to manage the world difficulty.
         * @param worldId The world's dimension id. */
        public WorldDifficultyData(RegistryKey<World> worldId) {
            this.worldId = worldId;
        }
    }
}
