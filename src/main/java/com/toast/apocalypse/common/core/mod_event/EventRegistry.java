package com.toast.apocalypse.common.core.mod_event;

import com.toast.apocalypse.common.core.mod_event.events.*;
import com.toast.apocalypse.common.util.References;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Objects;

public class EventRegistry {

    public static final HashMap<Integer, EventType<?>> EVENTS = new HashMap<>();


    public static final EventType<?> NONE = register(-1, EmptyEvent::new, null, true,
            (serverWorld, currentEvent, player, difficultyManager) -> false,
            (serverWorld, currentEvent, player, difficultyManager) -> true);

    public static final EventType<?> FULL_MOON = register(0, FullMoonEvent::new, References.FULL_MOON, false,
            (serverWorld, currentEvent, player, difficultyManager) -> difficultyManager.isFullMoonNight(),
            (serverWorld, currentEvent, player, difficultyManager) -> difficultyManager.isFullMoonNight());

    public static final EventType<?> THUNDERSTORM = register(1, ThunderstormEvent::new, null, true,
            (serverWorld, currentEvent, player, difficultyManager) -> serverWorld.isThundering(),
            (serverWorld, currentEvent, player, difficultyManager) -> serverWorld.isThundering());

    public static final EventType<?> ACID_RAIN = register(2, AcidRainEvent::new, References.ACID_RAIN, true,
            (serverWorld, currentEvent, player, difficultyManager) -> difficultyManager.isRainingAcid(serverWorld),
            (serverWorld, currentEvent, player, difficultyManager) -> difficultyManager.isRainingAcid(serverWorld));



    @Nonnull
    public static EventType<?> getFromId(int id) {
        if (!EVENTS.containsKey(id)) {
            return NONE;
        }
        return EVENTS.get(id);
    }

    private static <T extends AbstractEvent> EventType<T> register(int id, EventType.IEventFactory<T> factory, String startMessage , boolean canBeInterrupted, IEventPredicate startupPredicate, IEventPredicate persistPredicate) {
        Objects.requireNonNull(factory);

        if (EVENTS.containsKey(id)) {
            throw new IllegalArgumentException("An internal Apocalypse event was registered with duplicate ID \"" + id + "\". This is really bad, and should never happen. Please contact the Apocalypse devs to let them know they messed up!");
        }
        EventType<T> eventType = new EventType<>(id, factory, startMessage, canBeInterrupted, startupPredicate, persistPredicate);
        EVENTS.put(id, eventType);
        return eventType;
    }

    // Class loading epic moment
    public static void init() {}
}
