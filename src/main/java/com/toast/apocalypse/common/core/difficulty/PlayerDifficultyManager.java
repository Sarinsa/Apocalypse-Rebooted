package com.toast.apocalypse.common.core.difficulty;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.CommonConfigReloadListener;
import com.toast.apocalypse.common.core.config.util.ServerConfigHelper;
import com.toast.apocalypse.common.core.mod_event.EventRegistry;
import com.toast.apocalypse.common.core.mod_event.EventType;
import com.toast.apocalypse.common.core.mod_event.events.AbstractEvent;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.network.message.S2CSimpleClientTask;
import com.toast.apocalypse.common.triggers.ApocalypseTriggers;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.RainDamageTickHelper;
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
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class manages player difficulty and mod events
 * like the full moon siege and thunderstorm.
 */
public final class PlayerDifficultyManager {

    /** These are updated when the mod config is loaded/reloaded<br>
     * <br>
     *
     *  @see CommonConfigReloadListener#updateInfo()
     */

    public static double ACID_RAIN_CHANCE;
    public static boolean MULTIPLAYER_DIFFICULTY_SCALING;
    public static double MULTIPLAYER_DIFFICULTY_MULT;
    public static double SLEEP_PENALTY;
    public static double DIMENSION_PENALTY;
    public static List<RegistryKey<World>> DIMENSION_PENALTY_LIST;

    /** Number of ticks per update. */
    public static final int TICKS_PER_UPDATE = 5;
    /** Number of ticks per save. */
    public static final int TICKS_PER_SAVE = 160;
    /** Number of ticks per advancement trigger check. */
    public static final int TICKS_PER_ADV_CHECK = 200;

    /** Time until next server tick update. */
    private int timeUpdate = 0;
    /** Time until next save. */
    private int timeSave = 0;
    /** Time until next advancement trigger check. */
    private int timeAdvCheck = 0;

    /** Contains miscellaneous info about each world. */
    private final Map<World, WorldInfo> worldInfo = new HashMap<>();

    /** A Map containing each online player's current event. */
    private final Map<UUID, AbstractEvent> playerEvents = new HashMap<>();

    /** Manages rain damage. */
    private final RainDamageTickHelper rainDamageHelper;

    /** Server instance. */
    private MinecraftServer server;

    /** Whether the current server instance has been shut down. */
    private boolean serverStopped = false;


    public PlayerDifficultyManager() {
        this.rainDamageHelper = new RainDamageTickHelper();
    }

    public static long queryDayTime(long dayTime) {
        return dayTime % References.DAY_LENGTH;
    }

    /**
     * Used to find the difficulty of the nearest player when
     * calculating mob attribute bonuses and equipment etc.<br>
     * <br>
     *
     * @param world The World :)
     * @param livingEntity The entity to use as reference point.<br>
     * <br>
     * @return The unscaled, raw difficulty of the nearest player.
     *         Defaults to 0 if no player can be found.
     */
    public static long getNearestPlayerDifficulty(IWorld world, LivingEntity livingEntity) {
        PlayerEntity player = world.getNearestPlayer(livingEntity, Double.MAX_VALUE);

        if (player != null) {
            return CapabilityHelper.getPlayerDifficulty(player);
        }
        return 0;
    }

    public boolean isFullMoon() {
        ServerWorld world = this.server.overworld();
        return world.dimensionType().moonPhase(world.getDayTime()) == 0;
    }

    public boolean isFullMoonNight() {
        ServerWorld world = this.server.overworld();
        long dayTime = queryDayTime(world.getDayTime());

        return this.isFullMoon() && dayTime > 13000L && dayTime < 23500L;
    }

    public boolean isRainingAcid(ServerWorld world) {
        return worldInfo.get(world).isRainingAcid();
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
        this.serverStopped = false;
        event.getServer().getAllLevels().forEach((world) -> worldInfo.put(world, new WorldInfo(world)));
    }

    /** Clean up references and save player event data */
    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        for (ServerPlayerEntity player : this.server.getPlayerList().getPlayers()) {
            this.saveEventData(player);
        }
        this.cleanup();
        this.serverStopped = true;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().getCommandSenderWorld().isClientSide) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            ServerWorld overworld = this.server.overworld();
            ServerWorld playerWorld = player.getLevel();

            NetworkHelper.sendUpdatePlayerDifficulty(player);
            NetworkHelper.sendUpdatePlayerDifficultyMult(player);
            NetworkHelper.sendUpdatePlayerMaxDifficulty(player);
            NetworkHelper.sendMobWikiIndexUpdate(player);
            NetworkHelper.sendMoonPhaseUpdate(player, overworld);

            if (isRainingAcid(playerWorld))
                NetworkHelper.sendSimpleClientTaskRequest(player, S2CSimpleClientTask.SET_ACID_RAIN);

            this.loadEventData(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        // Don't bother saving event data
        // if the server has already been stopped
        // as it will have been taken care of already.
        if (this.serverStopped) return;

        if (!event.getPlayer().getCommandSenderWorld().isClientSide) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            this.saveEventData(player);
            this.playerEvents.get(player.getUUID()).stop(player.getLevel());
            this.playerEvents.remove(player.getUUID());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onSleepFinished(SleepFinishedTimeEvent event) {
        if (event.getWorld() instanceof ServerWorld) {
            ServerWorld world = (ServerWorld) event.getWorld();
            long newTime = event.getNewTime();
            long currentTime = world.getDayTime();
            long timeSkipped = newTime - currentTime;

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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();

            this.playerEvents.get(player.getUUID()).onPlayerDeath(player, (ServerWorld) player.level);
        }
    }

    /**
     * Called each game tick to update all players'
     * difficulty properties and Apocalypse events.
     *
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            MinecraftServer server = this.server;

            rainDamageHelper.checkAndPerformRainDamageTick(server.getAllLevels(), this);

            // Update player difficulty and event
            if (++this.timeUpdate >= TICKS_PER_UPDATE) {
                this.timeUpdate = 0;

                // Update player difficulty and event
                for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
                    this.updatePlayerDifficulty(player);
                    this.updatePlayerEvent(player);
                }

                // Update world info
                for (ServerWorld world : server.getAllLevels()) {
                    WorldInfo info = worldInfo.get(world);

                    if (world.isRaining()) {
                        if (!info.justStartedRaining()) {
                            info.setJustStartedRaining(true, world.random);
                        }
                    }
                    else {
                        info.setJustStartedRaining(false, world.random);
                        info.setRainingAcid(false);
                    }
                }
            }

            // Save event data
            if (++this.timeSave >= TICKS_PER_SAVE) {
                this.timeSave = 0;

                for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
                    this.saveEventData(player);
                    // Cheekily sneak in a moon phase update here, since
                    // it doesn't exactly need to happen often.
                    NetworkHelper.sendMoonPhaseUpdate(player, server.overworld());
                }
            }

            // Check if players have passed their grace
            // period and grant the base achievement if so.
            if (++this.timeAdvCheck >= TICKS_PER_ADV_CHECK) {
                this.timeAdvCheck = 0;

                for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
                    ApocalypseTriggers.PASSED_GRACE_PERIOD.trigger(player, CapabilityHelper.getPlayerDifficulty(player));
                }
            }
        }
    }


    /**
     * Updates the player's difficulty.
     */
    private void updatePlayerDifficulty(ServerPlayerEntity player) {
        final int playerCount = this.server.getPlayerCount();
        final long maxDifficulty = CapabilityHelper.getMaxPlayerDifficulty(player);
        double difficultyMultiplier = CapabilityHelper.getPlayerDifficultyMult(player);
        long currentDifficulty = CapabilityHelper.getPlayerDifficulty(player);

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
        boolean maxDifficultyReached = maxDifficulty >= 0 && currentDifficulty >= maxDifficulty;

        if (!maxDifficultyReached && !player.isCreative() || !player.isSpectator()) {
            currentDifficulty += TICKS_PER_UPDATE * difficultyMultiplier;
        }
        // Update player difficulty stuff
        CapabilityHelper.setPlayerDifficulty(player, currentDifficulty);
        CapabilityHelper.setPlayerDifficultyMult(player, difficultyMultiplier);
    }

    /**
     * Updates the given player's current event
     * and checks what event should be active.
     *
     * @param player The player to update event for.
     */
    @SuppressWarnings("ConstantConditions")
    public void updatePlayerEvent(ServerPlayerEntity player) {
        ServerWorld world = player.getLevel();
        ServerWorld overworld = this.server.overworld();
        AbstractEvent currentEvent = getCurrentEvent(player);

        // This should never happen, and it would be super weird if it did
        if (currentEvent == null)
            return;

        EventType<?> eventType = currentEvent.getType();

        // Update current event
        currentEvent.update(world, player, this);

        if (CapabilityHelper.getPlayerDifficulty(player) > 0 && overworld.getGameTime() > 0L) {
            for (EventType<?> type : EventRegistry.EVENTS.values()) {
                if (eventType != type && type.getStartPredicate().test(world, eventType, player, this) && currentEvent.getType().canBeInterrupted()) {
                    // Copy over event generation
                    int generation = currentEvent.getEventGeneration();
                    eventType = this.startEvent(player, currentEvent, type);
                    getCurrentEvent(player).setEventGeneration(generation);
                    break;
                }
            }
            if (!eventType.getPersistPredicate().test(world, eventType, player, this)) {
                endEvent(player);
            }
        }
    }

    /** Starts an event for the given player, if possible.
     *
     * @param player The player to start the event for.
     * @param eventType The event type for the event to start.
     */
    public EventType<?> startEvent(ServerPlayerEntity player, AbstractEvent currentEvent, EventType<?> eventType) {
        if (eventType == null)
            return currentEvent.getType();

        if (currentEvent != null) {
            if (!currentEvent.getType().canBeInterrupted())
                return currentEvent.getType();
            currentEvent.onEnd(server, player);
        }
        AbstractEvent newEvent = eventType.createEvent();
        newEvent.onStart(this.server, player);
        this.playerEvents.put(player.getUUID(), newEvent);
        this.saveEventData(player);

        if (eventType.getEventStartMessage() != null) {
            player.displayClientMessage(new TranslationTextComponent(eventType.getEventStartMessage()), true);
        }
        return eventType;
    }

    public int getEventId(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        return this.playerEvents.containsKey(uuid) ? this.playerEvents.get(uuid).getType().getId() : -1;
    }

    // SHOULD not return null, but who knows
    @Nullable
    public AbstractEvent getCurrentEvent(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        return this.playerEvents.getOrDefault(uuid, null);
    }

    /** Ends the current active event, if any. */
    public void endEvent(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        this.playerEvents.get(uuid).onEnd(server, player);
        this.playerEvents.put(uuid, EventRegistry.NONE.createEvent());
        this.saveEventData(player);
    }

    /** Cleans up the references to things in a server when the server stops. */
    public void cleanup() {
        server = null;
        timeUpdate = 0;
        timeSave = 0;
        timeAdvCheck = 0;
        playerEvents.clear();
        rainDamageHelper.resetTimer();
        worldInfo.clear();
    }

    /** Loads the given player's event data. */
    public void loadEventData(ServerPlayerEntity player) {
        try {
            AbstractEvent currentEvent = EventRegistry.NONE.createEvent();
            CompoundNBT eventData = CapabilityHelper.getEventData(player);

            if (eventData != null && eventData.contains("EventId", Constants.NBT.TAG_INT)) {
                currentEvent = EventRegistry.getFromId(eventData.getInt("EventId")).createEvent();
                currentEvent.read(eventData, player, player.getLevel());
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
            if (this.playerEvents.containsKey(player.getUUID())) {
                AbstractEvent currentEvent = this.playerEvents.get(player.getUUID());
                CompoundNBT eventData = new CompoundNBT();

                currentEvent.write(eventData);
                CapabilityHelper.setEventData(player, eventData);
            }
            else {
                log(Level.ERROR, "No event object found for player " + player.getName().getString() + ". Not good!");
            }
        }
        catch (Exception e) {
            log(Level.ERROR, "Failed to write player event data for player " + player.getName().getString() + "! Not cool beans.");
            e.printStackTrace();
        }
    }

    /** Helper method for logging. */
    private static void log(Level level, String message) {
        Apocalypse.LOGGER.log(level, "[{}] " + message, PlayerDifficultyManager.class.getSimpleName());
    }



    /** Contains miscellaneous info about a world. */
    public static class WorldInfo {

        private final ServerWorld world;

        private boolean justStartedRaining;
        private boolean isRainingAcid;

        private WorldInfo(ServerWorld world) {
            this.world = world;
        }

        protected void setRainingAcid(boolean value) {
            isRainingAcid = value;

            for (ServerPlayerEntity player : world.players()) {
                NetworkHelper.sendSimpleClientTaskRequest(player, value ? S2CSimpleClientTask.SET_ACID_RAIN : S2CSimpleClientTask.REMOVE_ACID_RAIN);
            }
        }

        public boolean isRainingAcid() {
            return isRainingAcid;
        }

        public boolean justStartedRaining() {
            return justStartedRaining;
        }

        public void setJustStartedRaining(boolean value, Random random) {
            justStartedRaining = value;

            if (value && random.nextDouble() <= ACID_RAIN_CHANCE)
                setRainingAcid(true);
        }
    }
}
