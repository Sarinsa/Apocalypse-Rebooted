package com.toast.apocalypse.common.core.mod_event;

import com.toast.apocalypse.common.core.mod_event.events.AbstractEvent;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class EventType<T extends AbstractEvent> {
    
    private final IEventFactory<T> factory;
    private final int id;
    private final String name;
    @Nullable
    private final Component startMessage;
    @Nullable
    private final IEventPredicate startPredicate;
    
    public EventType( int id, String name, IEventFactory<T> factory, @Nullable Component startMessage,
                      @Nullable IEventPredicate startPredicate ) {
        this.factory = factory;
        this.name = name;
        this.id = id;
        this.startMessage = startMessage;
        this.startPredicate = startPredicate;
    }
    
    /** @return A new event instance using this event type's factory. */
    public final T createEvent() {
        return factory.create( this );
    }
    
    /** The ID of this event type. */
    public final int getId() {
        return id;
    }
    
    /** @return The component to display to the player when the event starts. */
    @Nullable
    public final Component getEventStartMessage() {
        return startMessage;
    }
    
    /**
     * @return This event type's start predicate with the conditions that must be met
     * for the event type to start the event.<br>
     * If this is null, assume the event is meant to be started manually and
     * not by {@link com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager}
     */
    @Nullable
    public final IEventPredicate getStartPredicate() {
        return startPredicate;
    }
    
    /**
     * @return The name of this event. Should follow a format like this:<br>
     * "the_event_name"
     */
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return getId() + "," + getName();
    }
    
    public interface IEventFactory<T> {
        T create( EventType<?> type );
    }
}
