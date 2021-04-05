package com.toast.apocalypse.common.core.mod_event;

import com.toast.apocalypse.common.core.Apocalypse;

import java.util.HashMap;
import java.util.Objects;

public class EventRegister {

    public static final HashMap<Integer, AbstractEvent> EVENTS = new HashMap<>();

    public static final AbstractEvent FULL_MOON = new FullMoonEvent(0);

    public static void registerEvents() {
        registerEvent(FULL_MOON);
    }

    private static void registerEvent(AbstractEvent event) {
        Objects.requireNonNull(event);;

        int id = event.getId();

        if (EVENTS.containsKey(id)) {
            Apocalypse.LOGGER.warn("[{}] Attempted to register an event with duplicate ID: {}", EventRegister.class.getSimpleName(), id);
        }
        else {
            // He put himself in the EVENTS map, funniest shit I've ever seen
            EVENTS.put(id, event);
        }
    }
}
