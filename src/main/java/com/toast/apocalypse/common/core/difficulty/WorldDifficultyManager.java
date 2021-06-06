package com.toast.apocalypse.common.core.difficulty;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.mod_event.AbstractEvent;
import com.toast.apocalypse.common.core.mod_event.EventRegister;
import com.toast.apocalypse.common.event.CommonConfigReloadListener;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.jmx.Server;

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
    /** Number of ticks per save. */
    public static final int TICKS_PER_SAVE = 60;

    /** Time until next server tick update. */
    private int timeUntilUpdate = 0;
    /** Time until next save */
    private int timeUntilSave = 0;

    /** These are updated when the mod config is loaded/reloaded
     *
     *  @see CommonConfigReloadListener#updateInfo()
     */
    public static boolean MULTIPLAYER_DIFFICULTY_SCALING;
    public static double MULTIPLAYER_DIFFICULTY_MULT;
    public static double SLEEP_PENALTY;
    public static double DIMENSION_PENALTY;

    public static List<RegistryKey<World>> DIMENSION_PENALTY_LIST;

    /** Server instance */
    private MinecraftServer server;

    /** The current running event. */
    private AbstractEvent currentEvent = null;

    /** Used to prevent full moons from constantly happening. */
    private boolean checkedFullMoon;

    /** The world difficulty multiplier */
    private double lastWorldDifficultyRate;

    /** A map containing each world's player group list. */
    private final HashMap<RegistryKey<World>, List<PlayerGroup>> playerGroups = new HashMap<>();


    /** Fetch the server */
    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        this.server = event.getServer();
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        // Would this really ever be anything else?
        if (this.server.overworld().dimension() == World.OVERWORLD) {
            this.loadEventData();
        }
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        this.cleanup();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().getCommandSenderWorld().isClientSide) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getPlayer();

            NetworkHelper.sendUpdatePlayerDifficulty(serverPlayer);
            NetworkHelper.sendUpdatePlayerDifficultyMult(serverPlayer);
            NetworkHelper.sendUpdatePlayerMaxDifficulty(serverPlayer);
        }
    }

    /**
     * Returns the PlayerGroup in the world closest to the specified entity.
     *
     * @param world The world to check for player groups.
     * @param livingEntity The entity to check distance from.
     */
    public PlayerGroup getNearestGroup(World world, LivingEntity livingEntity) {
        RegistryKey<World> key = world.dimension();

        if (this.playerGroups.containsKey(key)) {
            PlayerGroup playerGroup = null;
            double smallestDist = -1.0D;

            for (PlayerGroup group : this.playerGroups.get(key)) {
                double dist = group.distanceTo(livingEntity);

                if (smallestDist == -1.0D || dist < smallestDist) {
                    smallestDist = dist;
                    playerGroup = group;
                }
            }
            return playerGroup;
        }
        else {
            return null;
        }
    }

    public static long getNearestPlayerDifficulty(IWorld world, LivingEntity livingEntity) {
        PlayerEntity player = world.getNearestPlayer(livingEntity, Double.MAX_VALUE);

        if (player != null) {
            return CapabilityHelper.getPlayerDifficulty(player);
        }
        return 0;
    }

    private void updatePlayer(ServerPlayerEntity player) {
        int playerCount = this.server.getPlayerCount();
        double difficultyMultiplier = CapabilityHelper.getPlayerDifficultyMult(player);

        // Apply multiplayer difficulty multiplier, if enabled.
        if (MULTIPLAYER_DIFFICULTY_SCALING) {
            if (playerCount > 1) {
                difficultyMultiplier = 1.0D + ((playerCount - 1.0D) * MULTIPLAYER_DIFFICULTY_MULT);
            }
            else {
                difficultyMultiplier = 1.0D;
            }
        }

        // Apply dimension difficulty rate penalty if any player is in a dimension marked for penalty
        if (DIMENSION_PENALTY > 0.0D) {
            if (!player.isSpectator() && DIMENSION_PENALTY_LIST.contains(player.getCommandSenderWorld().dimension())) {
                difficultyMultiplier *= 1.0 + DIMENSION_PENALTY;
            }
        }
        long currentDifficulty = CapabilityHelper.getPlayerDifficulty(player);
        long maxDifficulty = CapabilityHelper.getMaxPlayerDifficulty(player);

        boolean maxDifficultyReached = maxDifficulty >= 0 && currentDifficulty >= maxDifficulty;

        if (!maxDifficultyReached) {
            currentDifficulty += TICKS_PER_UPDATE * difficultyMultiplier;
        }

        // Update player difficulty stuff
        if (!player.getCommandSenderWorld().isClientSide) {
            CapabilityHelper.setPlayerDifficulty(player, currentDifficulty);
            CapabilityHelper.setPlayerDifficultyMult(player, difficultyMultiplier);
            NetworkHelper.sendUpdatePlayerDifficulty(player, currentDifficulty);
            NetworkHelper.sendUpdatePlayerDifficultyMult(player, difficultyMultiplier);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onSleepFinished(SleepFinishedTimeEvent event) {
        if (event.getWorld() instanceof World) {
            World world = (World) event.getWorld();
            long timeSkipped = event.getNewTime() - world.getGameTime();

            if (!world.isClientSide && timeSkipped > 20L) {
                for (PlayerEntity player : world.players()) {
                    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

                    long playerDifficulty = CapabilityHelper.getPlayerDifficulty(serverPlayer);
                    long playerMaxDifficulty = CapabilityHelper.getMaxPlayerDifficulty(serverPlayer);
                    double difficultyMult = CapabilityHelper.getPlayerDifficultyMult(serverPlayer);

                    playerDifficulty += (timeSkipped * SLEEP_PENALTY * difficultyMult);
                    CapabilityHelper.setPlayerDifficulty(serverPlayer, Math.min(playerDifficulty, playerMaxDifficulty));

                    player.displayClientMessage(new TranslationTextComponent(References.SLEEP_PENALTY), true);
                }
            }
        }
    }

    /**
     * Called each game tick to update world difficulty rate
     * and the currently running Apocalypse event.
     *
     * TickEvent.Type type = the type of tick.
     * Side side = the side this tick is on.
     * TickEvent.Phase phase = the phase of this tick (START, END).
     *
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            MinecraftServer server = this.server;

            // Counter to update the world
            if (++this.timeUntilUpdate >= TICKS_PER_UPDATE) {
                this.timeUntilUpdate = 0;

                // Update all the players' difficulty
                for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
                    this.updatePlayer(player);
                }

                // Update active event
                if (this.currentEvent != null) {
                    this.currentEvent.update();
                }

                // Update each world
                for (ServerWorld world : server.getAllLevels()) {
                    this.updateWorld(world);
                }
            }

            // Save event data
            if (++this.timeUntilSave >= TICKS_PER_SAVE) {
                this.timeUntilSave = 0;
                this.saveEventData();
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
     */
    public void updateWorld(ServerWorld world) {
        if (world == null)
            return;

        // Starts the full moon event
        if (world.getGameTime() > 0L && this.currentEvent != EventRegister.FULL_MOON) {
            int dayTime = (int) (world.getGameTime() % 24000);
            if (dayTime < 13000) {
                this.checkedFullMoon = false;
            }
            else if (!this.checkedFullMoon && isFullMoon(world)) {
                this.checkedFullMoon = true;
                this.startEvent(EventRegister.FULL_MOON);
            }
        }

        // Stop the full moon event when it becomes day time.
        if (world.isDay() && this.currentEvent == EventRegister.FULL_MOON) {
            this.endEvent();
        }

        // Update event and players
        if (this.currentEvent != null) {
            this.currentEvent.update(world);

            for (PlayerEntity playerEntity : world.players()) {
                this.currentEvent.update(playerEntity);
            }
        }
    }

    /** Helper method for logging. */
    private static void log(Level level, String message) {
        Apocalypse.LOGGER.log(level, "[{}] " + message, WorldDifficultyManager.class.getSimpleName());
    }

    public Iterable<PlayerGroup> getPlayerGroups(World world) {
        return this.playerGroups.get(world.dimension());
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
    }

    /** Ends the current active event, if any. */
    public void endEvent() {
        this.currentEvent.onEnd();
        this.currentEvent = null;
    }

    public int getCurrentEventId() {
        return this.currentEvent == null ? -1 : this.currentEvent.getId();
    }

    /** Cleans up the references to things in a server when the server stops. */
    public void cleanup() {
        this.saveEventData();
        this.server = null;
        this.timeUntilUpdate = 0;
        this.timeUntilSave = 0;
        this.checkedFullMoon = false;
        this.playerGroups.clear();
    }

    public void loadEventData() {
        try {
            // Load event data
            World world = this.server.overworld();
            CompoundNBT eventData = CapabilityHelper.getEventData(world);

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

    public void saveEventData() {
        try {
            // Save event data
            World world = this.server.overworld();
            CompoundNBT eventData = new CompoundNBT();

            if (this.currentEvent != null) {
                eventData = this.currentEvent.write(eventData);
            }
            CapabilityHelper.setEventData(world, eventData);
        }
        catch (Exception e) {
            log(Level.ERROR, "Failed to write world save data! Not cool beans.");
            e.printStackTrace();
        }
    }
}
