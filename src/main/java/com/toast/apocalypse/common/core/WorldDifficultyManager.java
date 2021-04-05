package com.toast.apocalypse.common.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.core.mod_event.AbstractEvent;
import com.toast.apocalypse.common.core.mod_event.EventRegister;
import com.toast.apocalypse.common.event.CommonConfigReloadListener;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;

/**
 * The major backbone of Apocalypse, this class manages everything to do with the world difficulty - increases it over time,
 * saves and loads it to and from the disk, and notifies clients of changes to it.<br>
 * In addition, it houses many helper methods related to world difficulty and save data.
 */
public final class WorldDifficultyManager {

    /** Number of ticks per update. */
    public static final int TICKS_PER_UPDATE = 5;

    /** These are updated when the mod config is loaded/reloaded
     *
     *  @see CommonConfigReloadListener#updateInfo()
     */
    public static boolean MULTIPLAYER_DIFFICULTY_SCALING;
    public static double DIFFICULTY_MULTIPLIER;
    public static double SLEEP_PENALTY;
    public static double DIMENSION_PENALTY;
    public static List<ResourceLocation> DIMENSION_PENALTY_LIST;

    /** Server instance */
    private MinecraftServer server;

    /** Gson object for writing and reading the world difficulty data. */
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /** Map of all worlds to their respective player-based difficulties. */
    private static final HashMap<ResourceLocation, WorldDifficultyData> WORLD_MAP = new HashMap<>();

    /** The base directory for the server or local save in question. */
    private File dataDir;

    /** The current running event. */
    private AbstractEvent currentEvent = null;

    /** Used to prevent full moons from constantly happening. */
    private boolean checkedFullMoon;

    /** The world difficulty */
    private long worldDifficulty;
    private long lastWorldDifficulty;

    /** The world difficulty multiplier */
    private double worldDifficultyRateMul;
    private double lastWorldDifficultyRate;

    /** Time until next server tick update. */
    private int timeUntilUpdate = 0;

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
        WorldDifficultyData worldData = WORLD_MAP.get(world.dimension().getRegistryName());

        // Check for time jumps (aka sleeping in bed)
        long skippedTime = 0L;
        if (world.dimension().getRegistryName().equals(DimensionSettings.OVERWORLD.getRegistryName())) { // TEST - base time jumps only on overworld
            if (this.worldDifficultyRateMul > 0.0 && worldData != null && ApocalypseCommonConfig.COMMON.getSleepPenalty() > 0.0) {
                skippedTime = world.getGameTime() - worldData.lastWorldTime; // normally == 5
            }
        }

        // Starts the full moon event
        if (this.worldDifficulty > 0L && this.currentEvent != EventRegister.FULL_MOON) {
            int dayTime = (int) (world.getGameTime() % 24000);
            if (dayTime < 13000) {
                this.checkedFullMoon = false;
            }
            else if (!this.checkedFullMoon && WorldDifficultyManager.isFullMoon(world)) {
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
            ResourceLocation dimensionId = world.dimension().getRegistryName();
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
        if (this.currentEvent != null || this.worldDifficulty != this.lastWorldDifficulty) {
            if ((int) (this.worldDifficulty / 24000L) != (int) (this.lastWorldDifficulty / 24000L) || (int) (this.worldDifficulty % 24000L / 2400) != (int) (this.lastWorldDifficulty % 24000L / 2400)) {
                NetworkHelper.sendUpdateWorldDifficulty(this.worldDifficulty);
            }
            this.lastWorldDifficulty = this.worldDifficulty;
            // Might be tad too excessive to write every update
            //this.write();
        }
    }

    /** Initializes server files. Called during FMLServerAboutToStartEvent.
     * @param server The server about to be started. */
    public void init(MinecraftServer server) {
        this.server = server;

        try {
            this.dataDir = server.getFile(server.getWorldPath(FolderName.ROOT) + "/" + Apocalypse.MODID);
            if (!this.dataDir.mkdirs()) {
                this.read();
            }
        }
        catch (Exception e) {
            log(Level.ERROR, "Failed to initialize data storage! You should probably reload the world as soon as possible.");
            e.printStackTrace();
            this.dataDir = null;
        }
    }

    /** Cleans up the references to things in a server when the server stops. */
    public void cleanup() {
        Apocalypse.LOGGER.info("CLEANED UP REFERENCES");
        this.write();

        this.server = null;
        this.worldDifficulty = 0L;
        this.dataDir = null;
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
            if (++this.timeUntilUpdate >= WorldDifficultyManager.TICKS_PER_UPDATE) {
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
                // Apply dimension difficulty rate penalty if any player is in another dimension
                if (SLEEP_PENALTY > 0.0D) {
                    for (PlayerEntity player : server.getPlayerList().getPlayers()) {
                        if (DIMENSION_PENALTY_LIST.contains(player.getCommandSenderWorld().dimension().getRegistryName())) {
                            this.worldDifficultyRateMul *= 1.0 + DIMENSION_PENALTY;
                            break;
                        }
                    }
                }
                this.worldDifficulty += WorldDifficultyManager.TICKS_PER_UPDATE * this.worldDifficultyRateMul;

                // Update each world
                long mostSkippedTime = 0L;
                for (ServerWorld world : worlds) {
                    mostSkippedTime = this.updateWorld(world, mostSkippedTime);
                }
                // Handle sleep penalty
                if (mostSkippedTime > 20L) {
                    this.worldDifficulty += mostSkippedTime * SLEEP_PENALTY * this.worldDifficultyRateMul;
                    // Send skipped time messages
                    for (PlayerEntity playerEntity : server.getPlayerList().getPlayers()) {
                          playerEntity.sendMessage(new TranslationTextComponent(References.SLEEP_PENALTY), Util.NIL_UUID);
                    }
                }
                this.updateWorldDifficulty();
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

    public long getWorldDifficulty() {
        return this.worldDifficulty;
    }

    public double getWorldDifficultyRate() {
        return this.worldDifficultyRateMul;
    }

    public void setWorldDifficulty(long difficulty) {
        this.worldDifficulty = difficulty;
    }

    public void setWorldDifficultyRate(double rate) {
        this.worldDifficultyRateMul = rate;
    }

    /** Starts an event, if possible.
     * @param event The event to start.
     * @return True if the event was successfully started. */
    public boolean startEvent(AbstractEvent event) {
        if (event == null)
            return false;
        if (this.currentEvent != null) {
            if (!this.currentEvent.canBeInterrupted(event))
                return false;
            this.currentEvent.onEnd();
        }
        event.onStart();
        Iterable<ServerWorld> worlds = this.server.getAllLevels();
        for (ServerWorld world : worlds) {
            if (world != null) {
                for (PlayerEntity player : world.players()) {
                    player.sendMessage(new TranslationTextComponent(event.getEventStartMessage()), Util.NIL_UUID);
                }
            }
        }
        this.currentEvent = event;
        this.updateWorldDifficulty();
        return true;
    }

    /** Ends the current active event, if any. */
    public void endEvent() {
        this.currentEvent = null;
        this.updateWorldDifficulty();
    }

    public void read() {
        try {
            File difficultyFile = new File(this.dataDir, "difficulty.json");
            File eventDataFile = new File(this.dataDir, "event_data.json");

            FileReader difficultyReader = new FileReader(difficultyFile);
            FileReader eventReader = new FileReader(eventDataFile);

            JsonObject difficultyData = JSONUtils.parse(difficultyReader);
            JsonObject eventData = JSONUtils.parse(eventReader);

            this.worldDifficulty = difficultyData.getAsLong();
            this.currentEvent = EventRegister.EVENTS.get(eventData.get("id").getAsInt());
            this.currentEvent.read(eventData);
        }
        catch (FileNotFoundException e) {
            log(Level.ERROR, "Failed to read world save data! That shouldn't happen.");
            e.printStackTrace();
        }
    }

    public void write() {
        try {
            File difficultyFile = new File(this.dataDir, "difficulty.json");
            File eventDataFile = new File(this.dataDir, "event_data.json");

            FileWriter difficultyWriter = new FileWriter(difficultyFile);
            FileWriter eventWriter = new FileWriter(eventDataFile);

            JsonObject difficultyData = new JsonObject();
            JsonObject eventData = new JsonObject();

            difficultyData.addProperty("difficulty", this.worldDifficulty);

            if (this.currentEvent != null) {
                this.currentEvent.write(eventData);
            }

            if (this.dataDir == null) {
                log(Level.ERROR, "World directory is null. This can't be right?");
                return;
            }
            difficultyWriter.write(gson.toJson(difficultyData));
            eventWriter.write(gson.toJson(eventData));
        }
        catch (Exception e) {
            log(Level.ERROR, "Failed to write world save data! Not cool beans.");
            e.printStackTrace();
        }
    }

    /** Contains info related to this mod about a world. */
    public static class WorldDifficultyData {

        /** The dimension id of the world. */
        public final ResourceLocation dimensionId;
        /** The last recorded world time of the world. */
        public long lastWorldTime;

        /** Constructs WorldDifficultyData for a world to store information needed to manage the world difficulty.
         * @param dimensionId The world's dimension id. */
        public WorldDifficultyData(ResourceLocation dimensionId) {
            this.dimensionId = dimensionId;
        }
    }
}
