package com.toast.apocalypse.common.core.mod_event;

import com.toast.apocalypse.common.core.mod_event.events.AbstractEvent;

public class EventType<T extends AbstractEvent> {

    private final IEventFactory<T> factory;
    private final int id;
    private final String startMessage;
    private final boolean canBeInterrupted;
    private final IStartupPredicate predicate;

    public EventType(int id, IEventFactory<T> factory, String startMessage, boolean canBeInterrupted, IStartupPredicate predicate) {
        this.factory = factory;
        this.id = id;
        this.startMessage = startMessage;
        this.canBeInterrupted = canBeInterrupted;
        this.predicate = predicate;
    }

    public final T createEvent() {
        return this.factory.create(this);
    }

    public final int getId() {
        return this.id;
    }

    /**
     * @return The translation key of the message
     *         that is sent to players when this
     *         event starts, which will later be
     *         parsed to a TranslationTextComponent
     */
    public final String getEventStartMessage() {
        return this.startMessage;
    }

    /**
     * Whether or not this event can be
     * interrupted by another event.
     *
     * @return True if this event can be interrupted.
     */
    public final boolean canBeInterrupted() {
        return this.canBeInterrupted;
    }

    public IStartupPredicate getStartPredicate() {
        return this.predicate;
    }

    public interface IEventFactory<T> {
        T create(EventType<?> type);
    }
}
