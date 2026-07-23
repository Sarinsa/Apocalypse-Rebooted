package com.toast.apocalypse.common.core.difficulty;

import com.google.common.collect.ImmutableSet;
import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.capability.CapabilityHelper;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.config.util.ServerConfigHelper;
import com.toast.apocalypse.common.core.mod_event.EventRegistry;
import com.toast.apocalypse.common.core.mod_event.EventType;
import com.toast.apocalypse.common.core.mod_event.IEventPredicate;
import com.toast.apocalypse.common.core.mod_event.events.AbstractEvent;
import com.toast.apocalypse.common.event.ApocalypseEventFactory;
import com.toast.apocalypse.common.item.LunarArmorItem;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.network.message.S2CSimpleClientTask;
import com.toast.apocalypse.common.triggers.ApocalypseTriggers;
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
    
    /** The NBT key for Apocalypse event data. */
    private static final String KEY_EVENT_DATA_LIST = "ApocalypseRBOOTDEventData";
    
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
    
    
    public PlayerDifficultyManager() { }
    
    
    /** @return The current time of day in the given level. */
    public static long queryDayTime( Level level ) {
        return level.getDayTime() % References.DAY_LENGTH;
    }
    
    /**
     * Used to find the difficulty of the nearest player when
     * calculating mob attribute bonuses and equipment etc.<br>
     * <br>
     *
     * @param level        The World :)
     * @param livingEntity The entity to use as reference point.<br>
     *                     <br>
     * @return The unscaled, raw difficulty of the nearest player.
     * Defaults to 0 if no player can be found.
     */
    public static long getNearestPlayerDifficulty( LevelAccessor level, LivingEntity livingEntity ) {
        Player player = level.getNearestPlayer( livingEntity, Double.MAX_VALUE );
        
        if( player != null ) {
            return CapabilityHelper.getPlayerDifficulty( player );
        }
        return 0;
    }
    
    public static long getNearestPlayerDifficulty( LevelAccessor level, BlockPos pos ) {
        Player player = level.getNearestPlayer( pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, Double.MAX_VALUE, false );
        
        if( player != null ) {
            return CapabilityHelper.getPlayerDifficulty( player );
        }
        return 0;
    }
    
    /**
     * @return True if the current moon phase in the overworld is "new moon".<br>
     * Always returns false if {@link PlayerDifficultyManager#server} is null.
     */
    public boolean isNewMoon() {
        if( server == null ) return false;
        
        ServerLevel world = server.overworld();
        return world.dimensionType().moonPhase( world.getDayTime() ) == 4;
    }
    
    /**
     * @return True if the current moon phase in the overworld is "full moon".<br>
     * Always returns false if {@link PlayerDifficultyManager#server} is null.
     */
    public boolean isFullMoon() {
        if( server == null ) return false;
        
        ServerLevel world = server.overworld();
        return world.dimensionType().moonPhase( world.getDayTime() ) == 0;
    }
    
    /**
     * @return True if the current moon phase in the overworld is "new moon" and it is nighttime.<br>
     * Always returns false if {@link PlayerDifficultyManager#server} is null.
     */
    public boolean isFullMoonNight() {
        if( server == null ) return false;
        long dayTime = queryDayTime( server.overworld() );
        return isFullMoon() && dayTime > 13000L && dayTime < 23500L;
    }
    
    /** @return True if it is currently raining acid in the given level. */
    public boolean isRainingAcid( ServerLevel world ) {
        return worldInfo.get( world ).isRainingAcid();
    }
    
    /** Called when the server is about to start. */
    @SubscribeEvent
    public void onServerAboutToStart( ServerAboutToStartEvent event ) {
        server = event.getServer();
        if( !server.isDedicatedServer() ) {
            // Update integrated server mod server config
            ServerConfigHelper.updateModServerConfig();
        }
    }
    
    /** Called when the server has finished loading and is ready for playing. */
    @SubscribeEvent
    public void onServerStarted( ServerStartedEvent event ) {
        serverStopped = false;
        event.getServer().getAllLevels().forEach( ( world ) -> worldInfo.put( world, new WorldInfo( world ) ) );
    }
    
    /** Called when the server is stopping. */
    @SubscribeEvent
    public void onServerStopping( ServerStoppingEvent event ) {
        // Clean up references and save player event data
        for( ServerPlayer player : server.getPlayerList().getPlayers() ) {
            saveEventData( player );
        }
        cleanup();
        serverStopped = true;
    }
    
    /** Called when a player logs into the server. */
    @SubscribeEvent( priority = EventPriority.HIGH )
    public void onPlayerLoggedIn( PlayerEvent.PlayerLoggedInEvent event ) {
        if( event.getEntity() instanceof ServerPlayer player ) {
            ServerLevel playerLevel = player.serverLevel();
            
            // Load event data
            playerEvents.put( player.getUUID(), new HashMap<>() );
            loadEventData( player );
            
            // Update equipped lunar armor
            for( EquipmentSlot slot : MobEquipmentHandler.ARMOR_SLOTS ) {
                ItemStack armorStack = player.getItemBySlot( slot );
                
                if( armorStack.getItem() instanceof LunarArmorItem ) {
                    LunarArmorItem.writeIndexToNBT( armorStack, playerLevel );
                }
            }
        }
    }
    
    /** Called when a player has logged out of the server. */
    @SubscribeEvent( priority = EventPriority.HIGH )
    public void onPlayerLoggedOut( PlayerEvent.PlayerLoggedOutEvent event ) {
        // Don't bother saving event data
        // if the server has already been stopped
        // as it will have been taken care of already.
        if( serverStopped ) return;
        
        if( event.getEntity() instanceof ServerPlayer player ) {
            saveEventData( player );
            
            for( AbstractEvent abstractEvent : playerEvents.get( player.getUUID() ).values() ) {
                abstractEvent.stop( player.serverLevel(), player );
            }
            playerEvents.remove( player.getUUID() );
        }
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onPlayerDeath( LivingDeathEvent event ) {
        if( event.getEntity() instanceof ServerPlayer serverPlayer ) {
            for( AbstractEvent abstractEvent : playerEvents.get( serverPlayer.getUUID() ).values() ) {
                abstractEvent.onPlayerDeath( serverPlayer, serverPlayer.serverLevel() );
            }
            long difficulty = CapabilityHelper.getPlayerDifficulty( serverPlayer );
            
            // Reduce difficulty if we should
            if( difficulty > 0 ) {
                ReductionType reductionType = ApocalypseConfig.DIFFICULTY.GENERAL.reductionType.get();
                
                switch( reductionType ) {
                    case RESET -> CapabilityHelper.setPlayerDifficulty( serverPlayer, 0 );
                    case LEVEL -> {
                        long newDifficulty = difficulty - (ApocalypseConfig.DIFFICULTY.GENERAL.reductionLevel.get() * References.DAY_LENGTH);
                        CapabilityHelper.setPlayerDifficulty( serverPlayer, Math.max( 0, newDifficulty ) );
                    }
                    case PERCENTAGE -> {
                        double multiplier = 1.0 - ApocalypseConfig.DIFFICULTY.GENERAL.reductionPercentage.get();
                        CapabilityHelper.setPlayerDifficulty( serverPlayer, (long) (difficulty * multiplier) );
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
    @SubscribeEvent( priority = EventPriority.HIGH )
    public void onServerTick( TickEvent.ServerTickEvent event ) {
        if( event.phase == TickEvent.Phase.END ) {
            final MinecraftServer server = this.server;
            final ServerLevel overworld = server.overworld();
            
            // Update lunar armor modifier index.
            calculateLunarArmorIndex( server );
            
            if( ++timeUpdate >= TICKS_PER_UPDATE ) {
                timeUpdate = 0;
                
                if( server.overworld().getGameTime() > 0L ) {
                    // Update player difficulty and event
                    for( ServerPlayer player : server.getPlayerList().getPlayers() ) {
                        // noinspection resource
                        if( player.level().isLoaded( BlockPos.containing( player.position() ) ) ) {
                            updatePlayerDifficulty( player );
                            
                            if( playerEvents.containsKey( player.getUUID() ) ) {
                                updatePlayerEvent( player );
                            }
                        }
                    }
                    
                    // Update world info
                    if( !worldInfo.isEmpty() ) {
                        final WorldInfo overworldInfo = worldInfo.get( overworld );
                        
                        if( overworldInfo != null ) {
                            if( overworld.isRaining() ) {
                                if( !overworldInfo.justStartedRaining() ) {
                                    overworldInfo.setJustStartedRaining( true, overworld.random );
                                }
                            }
                            else {
                                overworldInfo.setJustStartedRaining( false, overworld.random );
                                overworldInfo.setRainingAcid( false );
                            }
                        }
                    }
                }
            }
            // Save event data
            if( ++timeSave >= TICKS_PER_SAVE ) {
                timeSave = 0;
                
                for( ServerPlayer player : server.getPlayerList().getPlayers() ) {
                    saveEventData( player );
                }
            }
            // Check if players have passed their grace
            // period and grant the base achievement if so.
            if( ++timeAdvCheck >= TICKS_PER_ADV_CHECK ) {
                timeAdvCheck = 0;
                
                for( ServerPlayer player : server.getPlayerList().getPlayers() ) {
                    ApocalypseTriggers.PASSED_GRACE_PERIOD.trigger( player, CapabilityHelper.getPlayerDifficulty( player ) );
                }
            }
        }
    }
    
    /**
     * Calculates the Lunar Armor attribute modifier index and also writes it
     * to the NBT of any Lunar Armor pieces the players have equipped.
     * <br><br>
     * Used in {@link LunarArmorItem} to decide which armor attribute modifiers to use.
     */
    private void calculateLunarArmorIndex( MinecraftServer server ) {
        if( ++sporadicLunarIndexTime >= (ApocalypseConfig.MISC.OTHER.lunarEquipmentUpdateTime.get() * 20) ) {
            sporadicLunarIndexTime = 0;
            sporadicLunarArmorIndex = server.overworld().random.nextInt( LunarArmorItem.MAX_INDEX ) + 1;
        }
        int lunarArmorModIndex;
        
        if( isFullMoonNight() ) {
            lunarArmorModIndex = sporadicLunarArmorIndex;
        }
        else {
            lunarArmorModIndex = isNewMoon() ? -1 : 0;
        }
        if( currentLunarArmorIndex != lunarArmorModIndex ) {
            // Update clients
            for( ServerPlayer serverPlayer : server.getPlayerList().getPlayers() ) {
                boolean playSound = false;
                
                for( EquipmentSlot slot : MobEquipmentHandler.ARMOR_SLOTS ) {
                    ItemStack armorStack = serverPlayer.getItemBySlot( slot );
                    
                    if( armorStack.getItem() instanceof LunarArmorItem ) {
                        playSound = true;
                        LunarArmorItem.writeIndexToNBT( armorStack, serverPlayer.level() );
                    }
                }
                if( playSound ) {
                    // noinspection resource
                    serverPlayer.level().playSound(
                            null,
                            serverPlayer.blockPosition(),
                            ApocalypseObjects.SoundEvents.LUNAR_ARMOR_REACT.get(),
                            SoundSource.PLAYERS,
                            0.9F,
                            serverPlayer.getRandom().nextFloat() * 0.1F + 1.0F );
                }
            }
            currentLunarArmorIndex = lunarArmorModIndex;
        }
    }
    
    /**
     * Updates the player's difficulty.
     */
    private void updatePlayerDifficulty( ServerPlayer player ) {
        final long maxDifficulty = CapabilityHelper.getMaxPlayerDifficulty( player );
        long currentDifficulty = CapabilityHelper.getPlayerDifficulty( player );
        double difficultyMultiplier = 1.0D;
        boolean maxDifficultyReached = maxDifficulty >= 0 && currentDifficulty >= maxDifficulty;
        
        if( !maxDifficultyReached && !player.isCreative() && !player.isSpectator() ) {
            final int playerCount = server.getPlayerCount();
            
            // Apply multiplayer difficulty multiplier, if enabled.
            if( playerCount > 1 && ApocalypseConfig.DIFFICULTY.GENERAL.multiplayerMultiplier.get() > 1.0D ) {
                difficultyMultiplier = ApocalypseConfig.DIFFICULTY.GENERAL.multiplayerMultiplier.get();
            }
            
            // Apply dimension difficulty rate penalty if any player is in a dimension with a penalty multiplier
            Double dimensionPenalty = ApocalypseConfig.DIFFICULTY.GENERAL.dimensionPenaltyList.get( player.level() );
            
            if( dimensionPenalty != null && dimensionPenalty > 1.0D ) {
                if( !player.isSpectator() ) {
                    difficultyMultiplier += (dimensionPenalty - 1.0D);
                }
            }
            currentDifficulty += (long) (TICKS_PER_UPDATE * difficultyMultiplier);
        }
        
        // Update difficulty stuff on clients
        CapabilityHelper.setPlayerDifficulty( player, currentDifficulty );
        CapabilityHelper.setPlayerDifficultyMult( player, difficultyMultiplier );
    }
    
    /**
     * Updates the given player's current event
     * and checks what event should be active.
     *
     * @param player The player to update event for.
     */
    @SuppressWarnings( "ConstantConditions" )
    public void updatePlayerEvent( ServerPlayer player ) {
        ServerLevel level = player.serverLevel();
        
        Map<EventType<?>, AbstractEvent> events = playerEvents.get( player.getUUID() );
        
        final double scaledDifficulty = (double) (CapabilityHelper.getPlayerDifficulty( player ) / References.DAY_LENGTH);
        
        // Loop through running events and
        // stop any events that should no longer run.
        events.values().removeIf( ( abstractEvent ) -> {
            if( !abstractEvent.shouldContinueRunning( level, player, scaledDifficulty, this ) ) {
                return stopEvent( player, abstractEvent );
            }
            return false;
        } );
        
        // Tick current events
        for( AbstractEvent abstractEvent : events.values() ) {
            abstractEvent.update( level, player, this );
        }
        
        // Check for events to start
        for( EventType<?> type : EventRegistry.EVENTS.values() ) {
            if( !events.containsKey( type ) ) {
                IEventPredicate startPredicate = type.getStartPredicate();
                
                if( startPredicate != null && startPredicate.test( level, player, scaledDifficulty, this ) ) {
                    startEvent( player, type );
                }
            }
        }
    }
    
    /**
     * Starts the specified Apocalypse event for the given player, if possible.
     *
     * @param player    The player to start the event for.
     * @param eventType The event type of the event to start.
     */
    private void startEvent( ServerPlayer player, EventType<?> eventType ) {
        final int eventId = eventType.getId();
        // Allow listeners to prevent the event from starting.
        if( ApocalypseEventFactory.fireApocalypseEventStarting( player, eventId ) ) return;
        
        final AbstractEvent newEvent = eventType.createEvent();
        
        newEvent.onStart( server, player );
        playerEvents.get( player.getUUID() ).put( eventType, newEvent );
        
        if( eventType.getEventStartMessage() != null && ApocalypseConfig.MISC.EVENTS.displayStartMessage.get() ) {
            player.displayClientMessage( Component.translatable( eventType.getEventStartMessage() ), true );
        }
        // Notify listeners that this event has started.
        ApocalypseEventFactory.fireApocalypseEventStarted( true, player, eventId );
    }
    
    /**
     * Stops the specified Apocalypse event for the given player, if possible.
     *
     * @param player The player to stop the event for.
     * @param event  The event to stop.
     * @return True if the event stopped.
     */
    private boolean stopEvent( ServerPlayer player, AbstractEvent event ) {
        final boolean ended;
        final int eventId = event.getType().getId();
        
        // Allow listeners to prevent the event from ending
        if( ApocalypseEventFactory.fireApocalypseEventEnding( player, eventId ) ) {
            ended = false;
        }
        else {
            event.onEnd( server, player );
            ended = true;
        }
        // Notify listeners that this event has ended.
        ApocalypseEventFactory.fireApocalypseEventEnded( true, player, eventId );
        return ended;
    }
    
    /**
     * @return A set containing the event types for every running Apocalypse event for the specified player.
     * Returns null if the internal event map does not contain a key of the given player's UUID.
     */
    @Nullable
    public Set<EventType<?>> getEventTypes( ServerPlayer player ) {
        final UUID uuid = player.getUUID();
        return playerEvents.containsKey( uuid ) ? ImmutableSet.copyOf( playerEvents.get( uuid ).keySet() ) : null;
    }
    
    /**
     * @return The specified player's currently running event
     * associated with the given event type. Returns null if the event is not running.
     */
    @Nullable
    public AbstractEvent getEvent( ServerPlayer player, EventType<?> eventType ) {
        UUID uuid = player.getUUID();
        if( !playerEvents.containsKey( uuid ) ) return null;
        return playerEvents.get( uuid ).getOrDefault( eventType, null );
    }
    
    /** Resets timers and misc temporary data. */
    private void cleanup() {
        server = null;
        timeUpdate = 0;
        timeSave = 0;
        timeAdvCheck = 0;
        playerEvents.clear();
        worldInfo.clear();
    }
    
    /**
     * Loads all Apocalypse event data for the specified player.
     *
     * @see PlayerDifficultyManager#onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent)
     */
    public void loadEventData( ServerPlayer player ) {
        final CompoundTag persistentData = player.getPersistentData();
        
        if( persistentData.contains( KEY_EVENT_DATA_LIST, Tag.TAG_LIST ) ) {
            ListTag listTag = persistentData.getList( KEY_EVENT_DATA_LIST, Tag.TAG_COMPOUND );
            
            for( Tag tag : listTag ) {
                try {
                    CompoundTag compoundTag = (CompoundTag) tag;
                    
                    if( compoundTag.contains( "EventId", Tag.TAG_INT ) ) {
                        EventType<?> eventType = EventRegistry.getFromId( compoundTag.getInt( "EventId" ) );
                        
                        if( eventType != null ) {
                            AbstractEvent event = eventType.createEvent();
                            event.read( compoundTag, player, player.serverLevel() );
                            playerEvents.get( player.getUUID() ).put( eventType, event );
                        }
                    }
                }
                catch( Exception e ) {
                    Apocalypse.LOGGER.error( "Failed to load mod event data for player with UUID {}.", player.getUUID() );
                    // noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Saves all Apocalypse event data for the specified player.
     *
     * @see PlayerDifficultyManager#onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent)
     * @see PlayerDifficultyManager#onServerStopping(ServerStoppingEvent)
     * @see PlayerDifficultyManager#onServerTick(TickEvent.ServerTickEvent)
     */
    public void saveEventData( ServerPlayer player ) {
        if( !playerEvents.containsKey( player.getUUID() ) ) return;
        
        try {
            CompoundTag persistentData = player.getPersistentData();
            ListTag listTag = new ListTag();
            
            for( AbstractEvent abstractEvent : playerEvents.get( player.getUUID() ).values() ) {
                CompoundTag tag = new CompoundTag();
                abstractEvent.write( tag );
                listTag.add( tag );
            }
            persistentData.put( KEY_EVENT_DATA_LIST, listTag );
        }
        catch( Exception e ) {
            Apocalypse.LOGGER.info( "Failed to save player event data for player with UUID {}", player.getUUID() );
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
    
    /**
     * @return The current lunar armor attribute modifier index.
     * @see LunarArmorItem#writeIndexToNBT(ItemStack, Level)
     */
    public int getLunarArmorModIndex() {
        return currentLunarArmorIndex;
    }
    
    
    /** Contains various level information Apocalypse uses for events and whatnot. */
    public static class WorldInfo {
        
        /** The save data ID to use when writing {@link #savedData} to the level. */
        protected static final String SAVE_DATA_ID = Apocalypse.rl( "world_info" ).toString();
        
        /** The level that this world info is storing data for. */
        private final ServerLevel level;
        /** The level save data instance for this world info. */
        private final WorldInfoSavedData savedData;
        
        /** True if it just started raining this tick. */
        private boolean justStartedRaining;
        /** True if it is currently raining acid in the level associated with this world info. */
        private boolean isRainingAcid;
        
        
        /** Creates a new world info instance with the specified level. */
        private WorldInfo( ServerLevel level ) {
            this.level = level;
            this.savedData = level.getDataStorage().computeIfAbsent( this::load, () -> new WorldInfoSavedData( this ), SAVE_DATA_ID );
        }
        
        /** Updates the {@link #isRainingAcid} field for this world info and notifies clients. */
        protected void setRainingAcid( boolean value ) {
            isRainingAcid = value;
            
            savedData.setDirty();
            
            for( ServerPlayer player : level.players() ) {
                NetworkHelper.sendSimpleClientTaskRequest( player, value ? S2CSimpleClientTask.SET_ACID_RAIN : S2CSimpleClientTask.REMOVE_ACID_RAIN );
            }
        }
        
        /** @return True if it is currently raining acid in the level associated with this world info. */
        public boolean isRainingAcid() {
            return isRainingAcid;
        }
        
        /**
         * Updates the {@link #justStartedRaining} field for this world info. If {@code value}
         * is true, the configured acid rain event chance is rolled, maybe starting the event.
         */
        public void setJustStartedRaining( boolean value, RandomSource random ) {
            justStartedRaining = value;
            
            if( value && random.nextDouble() <= ApocalypseConfig.ACID_RAIN.GENERAL.acidRainChance.get() )
                setRainingAcid( true );
        }
        
        /** @return True if it just started raining in the level associated with this world info (this tick). */
        public boolean justStartedRaining() {
            return justStartedRaining;
        }
        
        /** Used by this world info's {@link #savedData} instance to load from NBT. */
        protected WorldInfoSavedData load( CompoundTag compoundNBT ) {
            WorldInfoSavedData savedData = new WorldInfoSavedData( this );
            
            if( compoundNBT.contains( "RainingAcid", Tag.TAG_BYTE ) ) {
                isRainingAcid = compoundNBT.getBoolean( "RainingAcid" );
            }
            return savedData;
        }
        
        /**
         * A level saved data implementation used to write
         * world info data that needs to persist to disk.
         */
        protected static class WorldInfoSavedData extends SavedData {
            
            /** The {@link WorldInfo} instance associated with this saved data. */
            private final WorldInfo worldInfo;
            
            
            /** Creates a new instance for the specified world info. */
            public WorldInfoSavedData( WorldInfo info ) {
                worldInfo = info;
            }
            
            /** Saves this saved data instance to NBT. */
            @Override
            public CompoundTag save( CompoundTag compoundNBT ) {
                compoundNBT.putBoolean( "RainingAcid", worldInfo.isRainingAcid );
                return compoundNBT;
            }
        }
    }
}
