package com.toast.apocalypse.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * The base Forge event class for all Apocalypse events, such as lunar sieges,
 * acid rain, thunderstorms etc.
 * <br><br>
 * All sub-events are posted on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * and are {@link Cancelable}.
 */
@Cancelable
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
    
    // TODO fire on client
    
    /**
     * On the <strong>server</strong>, this event is fired when an Apocalypse event is about to start.
     * Canceling this event will prevent the Apocalypse event from starting.
     * <br><br>
     * On the <strong>client</strong>, this event is fired AFTER an Apocalypse event has started,
     * and provides no control over the event itself, as Apocalypse events only exist on the server.
     * Canceling this event on the client will in other words do nothing other than stop
     * other listeners from receiving it.
     */
    public static final class Start extends ApocalypseEvent {
        
        public Start( Player player, int eventId ) {
            super( player, eventId );
        }
    }
    
    // TODO fire on client
    
    /**
     * On the <strong>server</strong>, this event is fired when an Apocalypse event is about to end.
     * Canceling this event will prevent the Apocalypse event from ending.
     * <br><br>
     * On the <strong>client</strong>, this event is fired AFTER an Apocalypse event has ended,
     * and provides no control over the event itself, as Apocalypse events only exist on the server.
     * Canceling this event on the client will in other words do nothing other than stop
     * other listeners from receiving it.
     */
    public static final class Stop extends ApocalypseEvent {
        
        public Stop( Player player, int eventId ) {
            super( player, eventId );
        }
    }
}
