package com.toast.apocalypse.common.core.difficulty;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ServerConfigHelper;
import com.toast.apocalypse.common.core.mod_event.events.AbstractEvent;
import com.toast.apocalypse.common.core.mod_event.EventRegistry;
import com.toast.apocalypse.common.core.mod_event.EventType;
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
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class manages player difficulty and mod events
 * like full moon sieges and thunderstorm events.
 */
public final class PlayerDifficultyManager {

    /** These are updated when the mod config is loaded/reloaded
     *
     *  @see CommonConfigReloadListener#updateInfo()
     */
    public static boolean MULTIPLAYER_DIFFICULTY_SCALING;
    public static double MULTIPLAYER_DIFFICULTY_MULT;
    public static double SLEEP_PENALTY;
    public static double DIMENSION_PENALTY;

    /** Number of ticks per update. */
    public static final int TICKS_PER_UPDATE = 5;
    /** Number of ticks per save. */
    public static final int TICKS_PER_SAVE = 120;

    /** Time until next server tick update. */
    private int timeUntilUpdate = 0;
    /** Time until next save */
    private int timeUntilSave = 0;

    public static List<RegistryKey<World>> DIMENSION_PENALTY_LIST;

    /** Server instance */
    private MinecraftServer server;

    private final HashMap<UUID, AbstractEvent> playerEvents = new HashMap<>();

    // Unused
    /** A map containing each world's player group list. */
    private final HashMap<RegistryKey<World>, List<PlayerGroup>> playerGroups = new HashMap<>();


    public static boolean isFullMoon(IWorld world) {
        return world.getMoonBrightness() == 1.0F;
    }

    public static long getNearestPlayerDifficulty(IWorld world, LivingEntity livingEntity) {
        PlayerEntity player = world.getNearestPlayer(livingEntity, Double.MAX_VALUE);

        if (player != null) {
            return CapabilityHelper.getPlayerDifficulty(player);
        }
        return 0;
    }

    /** Fetch the server instance and update integrated server mod server config. */
    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        this.server = event.getServer();
        if (!server.isDedicatedServer()) {
            ServerConfigHelper.updateModServerConfig();
        }
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        // :)
    }

    /** Clean up references and save player event data */
    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        for (ServerPlayerEntity player : this.server.getPlayerList().getPlayers()) {
            this.saveEventData(player);
        }
        this.cleanup();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().getCommandSenderWorld().isClientSide) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getPlayer();

            NetworkHelper.sendUpdatePlayerDifficulty(serverPlayer);
            NetworkHelper.sendUpdatePlayerDifficultyMult(serverPlayer);
            NetworkHelper.sendUpdatePlayerMaxDifficulty(serverPlayer);

            this.loadEventData(serverPlayer);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.getPlayer().getCommandSenderWorld().isClientSide) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getPlayer();
            this.saveEventData(serverPlayer);
            this.playerEvents.get(serverPlayer.getUUID()).stop(serverPlayer.getLevel());
            this.playerEvents.remove(serverPlayer.getUUID());
        }
    }

    // Unused
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

    private void updatePlayer(ServerPlayerEntity player) {
        int playerCount = this.server.getPlayerCount();
        double difficultyMultiplier = CapabilityHelper.getPlayerDifficultyMult(player);
        long currentDifficulty = CapabilityHelper.getPlayerDifficulty(player);
        long maxDifficulty = CapabilityHelper.getMaxPlayerDifficulty(player);

        // Apply multiplayer difficulty multiplier, if enabled.
        if (MULTIPLAYER_DIFFICULTY_SCALING) {
            if (playerCount > 1) {
                difficultyMultiplier = 1.0D + ((playerCount - 1.0D) * MULTIPLAYER_DIFFICULTY_MULT);
            } else {
                difficultyMultiplier = 1.0D;
            }
        }

        // Apply dimension difficulty rate penalty if any player is in a dimension marked for penalty
        if (DIMENSION_PENALTY > 0.0D) {
            if (!player.isSpectator() && DIMENSION_PENALTY_LIST.contains(player.getCommandSenderWorld().dimension())) {
                difficultyMultiplier *= 1.0 + DIMENSION_PENALTY;
            }
        }
        boolean maxDifficultyReached = maxDifficulty >= 0 && currentDifficulty >= maxDifficulty;

        if (!maxDifficultyReached && !player.isCreative() || !player.isSpectator()) {
            currentDifficulty += TICKS_PER_UPDATE * difficultyMultiplier;
        }
        // Update player difficulty stuff
        CapabilityHelper.setPlayerDifficulty(player, currentDifficulty);
        CapabilityHelper.setPlayerDifficultyMult(player, difficultyMultiplier);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onSleepFinished(SleepFinishedTimeEvent event) {
        if (event.getWorld() instanceof ServerWorld) {
            ServerWorld world = (ServerWorld) event.getWorld();
            long timeSkipped = event.getNewTime() - world.getDayTime();

            if (timeSkipped > 20L) {
                for (ServerPlayerEntity player : world.players()) {
                    long playerDifficulty = CapabilityHelper.getPlayerDifficulty(player);
                    long playerMaxDifficulty = CapabilityHelper.getMaxPlayerDifficulty(player);
                    double difficultyMult = CapabilityHelper.getPlayerDifficultyMult(player);

                    playerDifficulty += ((timeSkipped * SLEEP_PENALTY) * difficultyMult);
                    CapabilityHelper.setPlayerDifficulty(player, Math.min(playerDifficulty, playerMaxDifficulty));

                    player.displayClientMessage(new TranslationTextComponent(References.SLEEP_PENALTY), true);
                    player.playSound(SoundEvents.AMBIENT_CAVE, 1.0F, 0.8F);
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

                // Update player difficulty
                for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
                    this.updatePlayer(player);
                }

                // Update player events
                for (ServerWorld world : server.getAllLevels()) {
                    this.updatePlayerEvent(world);
                }
            }

            // Save event data
            if (++this.timeUntilSave >= TICKS_PER_SAVE) {
                this.timeUntilSave = 0;

                for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
                    this.saveEventData(player);
                }
            }
        }
    }

    /**
     * Loops through all players in the given world
     * and updates their event status.
     *
     * @param world The world being updated.
     */
    public void updatePlayerEvent(ServerWorld world) {
        if (world == null)
            return;

        for (ServerPlayerEntity player : world.players()) {
            AbstractEvent currentEvent = this.playerEvents.get(player.getUUID());
            EventType<?> eventType = currentEvent.getType();

            if (CapabilityHelper.getPlayerDifficulty(player) > 0) {
                // Starts the full moon event
                if (world.getGameTime() > 0L && eventType != EventRegistry.FULL_MOON) {
                    if (isFullMoon(world) && world.getDayTime() > 13000L) {
                        this.startEvent(player, EventRegistry.FULL_MOON);
                    }
                }

                // Stop the full moon event when it becomes day time.
                if (world.getDayTime() < 13000L && eventType == EventRegistry.FULL_MOON) {
                    this.endEvent(player);
                }

                // Starts the thunderstorm event
                if (world.isThundering() && eventType != EventRegistry.THUNDERSTORM) {
                    this.startEvent(player, EventRegistry.THUNDERSTORM);
                }
            }
            // Update current event
            currentEvent.update(world, player);
        }
    }

    /** Helper method for logging. */
    private static void log(Level level, String message) {
        Apocalypse.LOGGER.log(level, "[{}] " + message, PlayerDifficultyManager.class.getSimpleName());
    }

    // Unused
    public Iterable<PlayerGroup> getPlayerGroups(World world) {
        return this.playerGroups.get(world.dimension());
    }


    /** Starts an event for the given player, if possible.
     *
     * @param player The player to start the event for.
     * @param eventType The event type for the event to start.
     */
    public void startEvent(ServerPlayerEntity player, EventType<?> eventType) {
        if (eventType == null)
            return;

        AbstractEvent currentEvent = this.playerEvents.get(player.getUUID());

        if (currentEvent != null) {
            if (!currentEvent.getType().canBeInterrupted())
                return;
            currentEvent.onEnd();
        }
        AbstractEvent newEvent = eventType.createEvent();
        newEvent.onStart(this.server, player);
        this.playerEvents.put(player.getUUID(), newEvent);

        if (eventType.getEventStartMessage() != null) {
            player.displayClientMessage(new TranslationTextComponent(eventType.getEventStartMessage()), true);
        }
    }

    public int getEventId(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        return this.playerEvents.containsKey(uuid) ? this.playerEvents.get(uuid).getType().getId() : -1;
    }

    /** Ends the current active event, if any. */
    public void endEvent(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        this.playerEvents.get(uuid).onEnd();
        this.playerEvents.put(uuid, EventRegistry.NONE.createEvent());
    }

    /** Cleans up the references to things in a server when the server stops. */
    public void cleanup() {
        this.server = null;
        this.timeUntilUpdate = 0;
        this.timeUntilSave = 0;
        this.playerGroups.clear();
        this.playerEvents.clear();
    }

    /** Loads the given player's event data. */
    public void loadEventData(ServerPlayerEntity player) {
        try {
            AbstractEvent currentEvent = EventRegistry.NONE.createEvent();
            CompoundNBT eventData = CapabilityHelper.getEventData(player);

            if (eventData != null && eventData.contains("EventId", Constants.NBT.TAG_INT)) {
                currentEvent = EventRegistry.getFromId(eventData.getInt("EventId")).createEvent();
                currentEvent.read(eventData, player.getLevel());
            }
            this.playerEvents.put(player.getUUID(), currentEvent);
        }
        catch (Exception e) {
            log(Level.ERROR, "Failed to read world save data for player " + player.getName().getString() + ". That shouldn't happen.");
            e.printStackTrace();
        }
    }

    /** Saves the data of the player's current event. */
    public void saveEventData(ServerPlayerEntity player) {
        try {
            AbstractEvent currentEvent = this.playerEvents.get(player.getUUID());
            CompoundNBT eventData = new CompoundNBT();

            if (currentEvent != null) {
                eventData = currentEvent.write(eventData);
            }
            CapabilityHelper.setEventData(player, eventData);
        }
        catch (Exception e) {
            log(Level.ERROR, "Failed to write player event data for player " + player.getName().getString() + "! Not cool beans.");
            e.printStackTrace();
        }
    }
}
