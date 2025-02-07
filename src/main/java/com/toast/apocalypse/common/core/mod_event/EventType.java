package com.toast.apocalypse.common.core.mod_event;

import com.toast.apocalypse.common.core.mod_event.events.AbstractEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;

public class EventType<T extends AbstractEvent> {

    private final IEventFactory<T> factory;
    private final int id;
    private final String name;
    private final String startMessage;
    private final int priority;
    private final IEventPredicate startPredicate;
    @Nullable
    private final IEventPredicate persistPredicate;

    public EventType(int id, String name, IEventFactory<T> factory, String startMessage, int priority,
                     IEventPredicate startPredicate, @Nullable IEventPredicate persistPredicate) {
        this.factory = factory;
        this.name = name;
        this.id = id;
        this.startMessage = startMessage;
        this.priority = priority;
        this.startPredicate = startPredicate;
        this.persistPredicate = persistPredicate;
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
     * @return This event type's start predicate with the conditions that must be met
     *         for the event type to start the event.
     */
    public final IEventPredicate getStartPredicate() {
        return startPredicate;
    }

    /**
     * @return This event type's persist predicate with the conditions that must be met
     *         for the event to keep running after it has started.<br>
     *         If persistPredicate is null, the start predicate is returned instead.
     */
    public final IEventPredicate getPersistPredicate() {
        return persistPredicate == null ? startPredicate : persistPredicate;
    }

    /**
     * @return The name of this event. Should follow a format like this:<br>
     *         "the_event_name"
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getId() + "," + getName();
    }

    public interface IEventFactory<T> {
        T create(EventType<?> type);
    }
}
