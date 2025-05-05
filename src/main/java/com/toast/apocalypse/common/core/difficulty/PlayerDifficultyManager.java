package com.toast.apocalypse.common.core.difficulty;

import com.google.common.collect.ImmutableSet;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.config.util.ServerConfigHelper;
import com.toast.apocalypse.common.core.mod_event.EventRegistry;
import com.toast.apocalypse.common.core.mod_event.EventType;
import com.toast.apocalypse.common.core.mod_event.IEventPredicate;
import com.toast.apocalypse.common.core.mod_event.events.AbstractEvent;
import com.toast.apocalypse.common.core.register.ApocalypseSounds;
import com.toast.apocalypse.common.item.LunarArmorItem;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.network.message.S2CSimpleClientTask;
import com.toast.apocalypse.common.triggers.ApocalypseTriggers;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * This class manages player difficulty and mod events
 * like the full moon siege and thunderstorm.
 */
public final class PlayerDifficultyManager {

    /** The tag key for Apocalypse event data. */
    private static final String EVENT_DATA_LIST_KEY = "ApocalypseRBOOTDEventData";

    /** Number of ticks per update. */
    public static final int TICKS_PER_UPDATE = 5;
    /** Number of ticks per save. */
    public static final int TICKS_PER_SAVE = 160;
    /** Number of ticks per advancement trigger check. */
    public static final int TICKS_PER_ADV_CHECK = 200;

    /**
     * Used by {@link com.toast.apocalypse.common.item.LunarArmorItem} to determine
     * which attribute modifiers to use.
     */
    private int currentLunarArmorIndex = 0;
    /** Periodically changes during full moon nights. */
    private int sporadicLunarArmorIndex = 0;
    private int sporadicLunarIndexTime = 0;


    /** Time until next server tick update. */
    private int timeUpdate = 0;
    /** Time until next save. */
    private int timeSave = 0;
    /** Time until next advancement trigger check. */
    private int timeAdvCheck = 0;


    /** Contains miscellaneous info about each world. */
    private final Map<Level, WorldInfo> worldInfo = new HashMap<>();

    /** A Map containing each online player's current event. */
    private final Map<UUID, Map<EventType<?>, AbstractEvent>> playerEvents = new HashMap<>();

    /** Server instance. */
    private MinecraftServer server;

    /** Whether the current server instance has been shut down. */
    private boolean serverStopped = false;


    public PlayerDifficultyManager() {
    }

    public static long queryDayTime(long dayTime) {
        return dayTime % References.DAY_LENGTH;
    }

    /**
     * Used to find the difficulty of the nearest player when
     * calculating mob attribute bonuses and equipment etc.<br>
     * <br>
     *
     * @param level The World :)
     * @param livingEntity The entity to use as reference point.<br>
     * <br>
     * @return The unscaled, raw difficulty of the nearest player.
     *         Defaults to 0 if no player can be found.
     */
    public static long getNearestPlayerDifficulty(LevelAccessor level, LivingEntity livingEntity) {
        Player player = level.getNearestPlayer(livingEntity, Double.MAX_VALUE);

        if (player != null) {
            return CapabilityHelper.getPlayerDifficulty(player);
        }
        return 0;
    }

    public static long getNearestPlayerDifficulty(LevelAccessor level, BlockPos pos) {
        Player player = level.getNearestPlayer(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, Double.MAX_VALUE, false);

        if (player != null) {
            return CapabilityHelper.getPlayerDifficulty(player);
        }
        return 0;
    }

    /**
     *  @return True if the current moon phase in the overworld is "new moon".<br>
     *          Always returns false if {@link PlayerDifficultyManager#server} is null.
     */
    public boolean isNewMoon() {
        if (server == null) return false;

        ServerLevel world = server.overworld();
        return world.dimensionType().moonPhase(world.getDayTime()) == 4;
    }

    /**
     *  @return True if the current moon phase in the overworld is "full moon".<br>
     *          Always returns false if {@link PlayerDifficultyManager#server} is null.
     */
    public boolean isFullMoon() {
        if (server == null) return false;

        ServerLevel world = server.overworld();
        return world.dimensionType().moonPhase(world.getDayTime()) == 0;
    }

    /**
     *  @return True if the current moon phase in the overworld is "new moon" and it is nighttime.<br>
     *          Always returns false if {@link PlayerDifficultyManager#server} is null.
     */
    public boolean isFullMoonNight() {
        if (server == null) return false;

        ServerLevel world = server.overworld();
        long dayTime = queryDayTime(world.getDayTime());

        return isFullMoon() && dayTime > 13000L && dayTime < 23500L;
    }

    public boolean isRainingAcid(ServerLevel world) {
        return worldInfo.get(world).isRainingAcid();
    }

    /** Fetch the server instance and update integrated server mod server config. */
    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        server = event.getServer();
        if (!server.isDedicatedServer()) {
            ServerConfigHelper.updateModServerConfig();
        }
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        serverStopped = false;
        event.getServer().getAllLevels().forEach((world) -> worldInfo.put(world, new WorldInfo(world)));
    }

    /** Clean up references and save player event data */
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            saveEventData(player);
        }
        cleanup();
        serverStopped = true;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            ServerLevel playerLevel = player.serverLevel();

            // Load event data
            playerEvents.put(player.getUUID(), new HashMap<>());
            loadEventData(player);

            // Update equipped lunar armor
            for (EquipmentSlot slot : MobEquipmentHandler.ARMOR_SLOTS) {
                ItemStack armorStack = player.getItemBySlot(slot);

                if (armorStack.getItem() instanceof LunarArmorItem) {
                    LunarArmorItem.writeIndexToNBT(armorStack, playerLevel);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        // Don't bother saving event data
        // if the server has already been stopped
        // as it will have been taken care of already.
        if (serverStopped) return;

        if (!event.getEntity().level().isClientSide) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            saveEventData(player);

            for (AbstractEvent abstractEvent : playerEvents.get(player.getUUID()).values()) {
                abstractEvent.stop(player.serverLevel(), player);
            }
            playerEvents.remove(player.getUUID());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            for (AbstractEvent abstractEvent : playerEvents.get(serverPlayer.getUUID()).values()) {
                abstractEvent.onPlayerDeath(serverPlayer, serverPlayer.serverLevel());
            }
            long difficulty = CapabilityHelper.getPlayerDifficulty(serverPlayer);

            // Reduce difficulty if we should
            if (difficulty > 0) {
                ReductionType reductionType = ApocalypseConfig.DIFFICULTY.GENERAL.reductionType.get();

                switch (reductionType) {
                    case RESET -> CapabilityHelper.setPlayerDifficulty(serverPlayer, 0);
                    case LEVEL -> {
                        long newDifficulty = difficulty - (ApocalypseConfig.DIFFICULTY.GENERAL.reductionLevel.get() * References.DAY_LENGTH);
                        CapabilityHelper.setPlayerDifficulty(serverPlayer, Math.max(0, newDifficulty));
                    }
                    case PERCENTAGE -> {
                        double multiplier = 1.0 - ApocalypseConfig.DIFFICULTY.GENERAL.reductionPercentage.get();
                        CapabilityHelper.setPlayerDifficulty(serverPlayer, (long) (difficulty * multiplier));
                    }
                }
            }
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
            ServerLevel overworld = server.overworld();

            // Update lunar armor modifier index.
            calculateLunarArmorIndex(server);

            if (++timeUpdate >= TICKS_PER_UPDATE) {
                timeUpdate = 0;

                if (server.overworld().getGameTime() > 0L) {
                    // Update player difficulty and event
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        if (player.level().isLoaded(BlockPos.containing(player.position()))) {
                            updatePlayerDifficulty(player);

                            if (playerEvents.containsKey(player.getUUID())) {
                                updatePlayerEvent(player);
                            }
                        }
                    }

                    // Update world info
                    if (!worldInfo.isEmpty()) {
                        WorldInfo overworldInfo = worldInfo.get(overworld);

                        if (overworldInfo != null) {
                            if (overworld.isRaining()) {
                                if (!overworldInfo.justStartedRaining()) {
                                    overworldInfo.setJustStartedRaining(true, overworld.random);
                                }
                            } else {
                                overworldInfo.setJustStartedRaining(false, overworld.random);
                                overworldInfo.setRainingAcid(false);
                            }
                        }

                        // Currently only triggering acid rain in the overworld
                        /*
                        for (ServerLevel level : server.getAllLevels()) {
                            WorldInfo info = worldInfo.get(level);

                            if (info == null)
                                continue;

                            if (level.isRaining()) {
                                if (!info.justStartedRaining()) {
                                    info.setJustStartedRaining(true, level.random);
                                }
                            } else {
                                info.setJustStartedRaining(false, level.random);
                                info.setRainingAcid(false);
                            }
                        }

                         */
                    }
                }
            }

            // Save event data
            if (++timeSave >= TICKS_PER_SAVE) {
                timeSave = 0;

                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    saveEventData(player);
                }
            }

            // Check if players have passed their grace
            // period and grant the base achievement if so.
            if (++timeAdvCheck >= TICKS_PER_ADV_CHECK) {
                timeAdvCheck = 0;

                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    ApocalypseTriggers.PASSED_GRACE_PERIOD.trigger(player, CapabilityHelper.getPlayerDifficulty(player));
                }
            }
        }
    }

    /**
     * Calculates the Lunar Armor attribute modifier index and also writes it
     * to the NBT of any Lunar Armor pieces the players have equipped.<br><br>
     * Used in {@link LunarArmorItem} to decide which armor attribute modifiers to use.
     */
    private void calculateLunarArmorIndex(MinecraftServer server) {
        if (++sporadicLunarIndexTime >= (ApocalypseConfig.MISC.OTHER.lunarEquipmentUpdateTime.get() * 20)) {
            sporadicLunarIndexTime = 0;
            sporadicLunarArmorIndex = server.overworld().random.nextInt(LunarArmorItem.MAX_INDEX) + 1;
        }
        int lunarArmorModIndex;

        if (isFullMoonNight()) {
            lunarArmorModIndex = sporadicLunarArmorIndex;
        }
        else {
            lunarArmorModIndex = isNewMoon() ? -1 : 0;
        }
        if (currentLunarArmorIndex != lunarArmorModIndex) {
            // Update clients
            for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
                boolean playSound = false;

                for (EquipmentSlot slot : MobEquipmentHandler.ARMOR_SLOTS) {
                    ItemStack armorStack = serverPlayer.getItemBySlot(slot);

                    if (armorStack.getItem() instanceof LunarArmorItem) {
                        playSound = true;
                        LunarArmorItem.writeIndexToNBT(armorStack, serverPlayer.level());
                    }
                }
                if (playSound) {
                    serverPlayer.level().playSound(
                            null,
                            serverPlayer.blockPosition(),
                            ApocalypseSounds.LUNAR_ARMOR_REACT.get(),
                            SoundSource.PLAYERS,
                            0.9F,
                            serverPlayer.getRandom().nextFloat() * 0.1F + 1.0F);
                }
            }
            currentLunarArmorIndex = lunarArmorModIndex;
        }
    }

    /**
     * Updates the player's difficulty.
     */
    private void updatePlayerDifficulty(ServerPlayer player) {
        final long maxDifficulty = CapabilityHelper.getMaxPlayerDifficulty(player);
        long currentDifficulty = CapabilityHelper.getPlayerDifficulty(player);
        double difficultyMultiplier = 1.0D;
        boolean maxDifficultyReached = maxDifficulty >= 0 && currentDifficulty >= maxDifficulty;

        if (!maxDifficultyReached && !player.isCreative() && !player.isSpectator()) {
            final int playerCount = server.getPlayerCount();

            // Apply multiplayer difficulty multiplier, if enabled.
            if (playerCount > 1 && ApocalypseConfig.DIFFICULTY.GENERAL.multiplayerMultiplier.get() > 1.0D) {
                difficultyMultiplier = ApocalypseConfig.DIFFICULTY.GENERAL.multiplayerMultiplier.get();
            }

            // Apply dimension difficulty rate penalty if any player is in a dimension with a penalty multiplier
            Double dimensionPenalty = ApocalypseConfig.DIFFICULTY.GENERAL.dimensionPenaltyList.get(player.level());

            if (dimensionPenalty != null && dimensionPenalty > 1.0D) {
                if (!player.isSpectator()) {
                    difficultyMultiplier += (dimensionPenalty - 1.0D);
                }
            }
            currentDifficulty += (long) (TICKS_PER_UPDATE * difficultyMultiplier);
        }

        // Update difficulty stuff on clients
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
    public void updatePlayerEvent(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        Map<EventType<?>, AbstractEvent> events = playerEvents.get(player.getUUID());

        final double scaledDifficulty = (double) (CapabilityHelper.getPlayerDifficulty(player) / References.DAY_LENGTH);

        // Loop through running events and
        // stop any events that should no longer run.
        events.values().removeIf((abstractEvent) -> {
            if (!abstractEvent.shouldContinueRunning(level, player, scaledDifficulty, this)) {
                abstractEvent.onEnd(server, player);
                return true;
            }
            return false;
        });

        // Tick current events
        for (AbstractEvent abstractEvent : events.values()) {
            abstractEvent.update(level, player, this);
        }

        // Check for events to start
        for (EventType<?> type : EventRegistry.EVENTS.values()) {
            if (!events.keySet().contains(type)) {
                IEventPredicate startPredicate = type.getStartPredicate();

                if (startPredicate != null && startPredicate.test(level, player, scaledDifficulty, this)) {
                    startEvent(player, type);
                }
            }
        }
    }

    /** Starts an event for the given player, if possible.
     *
     * @param player The player to start the event for.
     * @param eventType The event type for the event to start.
     */
    public void startEvent(ServerPlayer player, @Nonnull EventType<?> eventType) {
        AbstractEvent newEvent = eventType.createEvent();
        newEvent.onStart(server, player);
        playerEvents.get(player.getUUID()).put(eventType, newEvent);

        if (eventType.getEventStartMessage() != null && ApocalypseConfig.MISC.EVENTS.displayStartMessage.get()) {
            player.displayClientMessage(Component.translatable(eventType.getEventStartMessage()), true);
        }
    }

    @Nullable
    public Set<EventType<?>> getEventTypes(ServerPlayer player) {
        UUID uuid = player.getUUID();
        return playerEvents.containsKey(uuid) ? ImmutableSet.copyOf(playerEvents.get(uuid).keySet()) : null;
    }

    // SHOULD not return null, but who knows
    @Nullable
    public AbstractEvent getEvent(ServerPlayer player, EventType<?> eventType) {
        UUID uuid = player.getUUID();

        if (!playerEvents.containsKey(uuid)) return null;

        return playerEvents.get(uuid).getOrDefault(eventType, null);
    }

    /** Cleans up the references to things in a server when the server stops. */
    public void cleanup() {
        server = null;
        timeUpdate = 0;
        timeSave = 0;
        timeAdvCheck = 0;
        playerEvents.clear();
        worldInfo.clear();
    }

    /** Loads the given player's event data. */
    public void loadEventData(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();

        if (persistentData.contains(EVENT_DATA_LIST_KEY, Tag.TAG_LIST)) {
            ListTag listTag = persistentData.getList(EVENT_DATA_LIST_KEY, Tag.TAG_COMPOUND);

            for (Tag tag : listTag) {
                try {
                    CompoundTag compoundTag = (CompoundTag) tag;

                    if (compoundTag.contains("EventId", Tag.TAG_INT)) {
                        EventType<?> eventType = EventRegistry.getFromId(compoundTag.getInt("EventId"));

                        if (eventType != null) {
                            AbstractEvent event = eventType.createEvent();
                            event.read(compoundTag, player, player.serverLevel());
                            playerEvents.get(player.getUUID()).put(eventType, event);
                        }
                    }
                } catch (Exception e) {
                    Apocalypse.LOGGER.error("Failed to load mod event data for player with UUID {}.", player.getUUID());
                    e.printStackTrace();
                }
            }
        }
    }

    /** Saves the data of the player's current event. */
    public void saveEventData(ServerPlayer player) {
        if (!playerEvents.containsKey(player.getUUID())) return;

        try {
            CompoundTag persistentData = player.getPersistentData();
            ListTag listTag = new ListTag();

            for (AbstractEvent abstractEvent : playerEvents.get(player.getUUID()).values()) {
                CompoundTag tag = new CompoundTag();
                abstractEvent.write(tag);
                listTag.add(tag);
            }
            persistentData.put(EVENT_DATA_LIST_KEY, listTag);
        }
        catch (Exception e) {
            Apocalypse.LOGGER.info("Failed to save player event data for player with UUID {}", player.getUUID());
            e.printStackTrace();
        }
    }

    public int getLunarArmorModIndex() {
        return currentLunarArmorIndex;
    }

    /** Contains miscellaneous info about a world. */
    public static class WorldInfo {

        protected static final String saveDataId = Apocalypse.resourceLoc("world_info").toString();

        private final ServerLevel level;
        private final WorldInfoSavedData savedData;

        private boolean justStartedRaining;
        private boolean isRainingAcid;


        private WorldInfo(ServerLevel level) {
            this.level = level;
            this.savedData = level.getDataStorage().computeIfAbsent(this::load, this::create, saveDataId);
        }

        protected void setRainingAcid(boolean value) {
            isRainingAcid = value;

            savedData.setDirty();

            for (ServerPlayer player : level.players()) {
                NetworkHelper.sendSimpleClientTaskRequest(player, value ? S2CSimpleClientTask.SET_ACID_RAIN : S2CSimpleClientTask.REMOVE_ACID_RAIN);
            }
        }

        public boolean isRainingAcid() {
            return isRainingAcid;
        }

        public boolean justStartedRaining() {
            return justStartedRaining;
        }

        public void setJustStartedRaining(boolean value, RandomSource random) {
            justStartedRaining = value;

            if (value && random.nextDouble() <= ApocalypseConfig.ACID_RAIN.GENERAL.acidRainChance.get())
                setRainingAcid(true);
        }

        protected WorldInfoSavedData load(CompoundTag compoundNBT) {
            WorldInfoSavedData savedData = new WorldInfoSavedData(this);

            if (compoundNBT.contains("RainingAcid", Tag.TAG_BYTE)) {
                isRainingAcid = compoundNBT.getBoolean("RainingAcid");
            }
            return savedData;
        }

        protected WorldInfoSavedData create() {
            return new WorldInfoSavedData(this);
        }

        protected static class WorldInfoSavedData extends SavedData {

            private final WorldInfo worldInfo;

            public WorldInfoSavedData(WorldInfo worldInfo) {
                this.worldInfo = worldInfo;
            }

            @Override
            public CompoundTag save(CompoundTag compoundNBT) {
                compoundNBT.putBoolean("RainingAcid", worldInfo.isRainingAcid);
                return compoundNBT;
            }
        }
    }
}
