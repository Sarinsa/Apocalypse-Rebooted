package com.toast.apocalypse.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * The base Forge event class for all Apocalypse events, such as lunar sieges,
 * acid rain, thunderstorms etc.
 */
public class ApocalypseEvent extends Event {
    
    /** Contains all valid and registered Apocalypse event IDs. */
    public interface EventIds {
        int LUNAR_SIEGE = 0;
        int THUNDERSTORM = 1;
        int ACID_RAIN = 2;
        int CALL_OF_THE_SHADOWS = 3;
    }
    
    /** The Apocalypse event owner. */
    private final Player player;
    /**
     * The Apocalypse event type ID.
     * See the fields above for an overview of recognized event IDs.
     */
    private final int eventId;
    
    
    public ApocalypseEvent( Player player, int eventId ) {
        this.player = player;
        this.eventId = eventId;
    }
    
    
    /** @return The Apocalypse event owner. */
    public Player getPlayer() {
        return player;
    }
    
    /** @return The Apocalypse event ID. */
    public int getEventId() {
        return eventId;
    }
    
    
    /**
     * Fired when an Apocalypse event is about to start.
     * Canceling this event will prevent the Apocalypse event from starting.
     * <br><br>
     * This event is only fired on the server.
     * <br><br>
     * This event is {@link Cancelable}.
     */
    @Cancelable
    public static final class Starting extends ApocalypseEvent {
        
        public Starting( Player player, int eventId ) {
            super( player, eventId );
        }
    }
    
    /**
     * Fired when an Apocalypse event has just started.
     * Listen to {@link Starting} instead if you wish to prevent
     * an Apocalypse event from starting.
     * <br><br>
     * This event is fired on both server and client.
     * <br><br>
     * This event is NOT {@link Cancelable}.
     */
    public static final class Started extends ApocalypseEvent {
        
        public Started( Player player, int eventId ) {
            super( player, eventId );
        }
    }
    
    /**
     * Fired when an Apocalypse event is about to end.
     * Canceling this event will prevent the Apocalypse event from ending,
     * allowing it to keep running. This event is only fired on the server.
     * <br><br>
     * This event is only fired on the server.
     * <br><br>
     * This event is {@link Cancelable}.
     */
    @Cancelable
    public static final class Ending extends ApocalypseEvent {
        
        public Ending( Player player, int eventId ) {
            super( player, eventId );
        }
    }
    
    /**
     * Fired when an Apocalypse event has ended.
     * Listen to {@link Ending} instead if you wish to prevent
     * an Apocalypse event from ending.
     * <br><br>
     * This event is fired on both server and client.
     * <br><br>
     * This event is NOT {@link Cancelable}.
     */
    public static final class Ended extends ApocalypseEvent {
        
        public Ended( Player player, int eventId ) {
            super( player, eventId );
        }
    }
}
