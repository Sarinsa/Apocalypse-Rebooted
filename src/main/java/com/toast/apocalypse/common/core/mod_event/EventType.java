package com.toast.apocalypse.common.core.mod_event;

import com.toast.apocalypse.common.core.mod_event.events.AbstractEvent;

public class EventType<T extends AbstractEvent> {

    private final IEventFactory<T> factory;
    private final int id;
    private final String name;
    private final String startMessage;
    private final int priority;
    private final IEventPredicate startPredicate;
    private final IEventPredicate continuePredicate;

    public EventType(int id, String name, IEventFactory<T> factory, String startMessage, int priority, IEventPredicate startPredicate, IEventPredicate continuePredicate) {
        this.factory = factory;
        this.name = name;
        this.id = id;
        this.startMessage = startMessage;
        this.priority = priority;
        this.startPredicate = startPredicate;
        this.continuePredicate = continuePredicate;
    }

    public final T createEvent() {
        return factory.create(this);
    }

    public final int getId() {
        return id;
    }

    /**
     * @return The translation key of the message
     *         that is sent to players when this
     *         event starts, which will later be
     *         parsed to a TranslationTextComponent
     */
    public final String getEventStartMessage() {
        return startMessage;
    }

    /**
     * Whether this event can be
     * interrupted by another event.
     *
     * @return True if this event can be interrupted.
     */
    public final int getPriority() {
        return priority;
    }

    public IEventPredicate getStartPredicate() {
        return startPredicate;
    }

    public IEventPredicate getPersistPredicate() {
        return continuePredicate;
    }

    public String getName() {
        return name;
    }

    public interface IEventFactory<T> {
        T create(EventType<?> type);
    }
}
