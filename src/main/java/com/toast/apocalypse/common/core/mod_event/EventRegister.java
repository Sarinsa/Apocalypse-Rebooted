package com.toast.apocalypse.common.core.mod_event;

import com.toast.apocalypse.common.core.Apocalypse;

import java.util.HashMap;
import java.util.Objects;

public class EventRegister {

    public static final HashMap<Integer, AbstractEvent> EVENTS = new HashMap<>();

    public static final AbstractEvent FULL_MOON = registerEvent(new FullMoonEvent(0));
    public static final AbstractEvent THUNDER_STORM = registerEvent(new ThunderstormEvent(1));

    private static <T extends AbstractEvent> T registerEvent(T event) {
        Objects.requireNonNull(event);
        int id = event.getId();

        if (id == -1) {
            throw new IllegalArgumentException("It appears that Sarinsa has been a total idiot and tried to register a mod event with an ID of -1. (I know I am a doofus, but this couldn't possibly happen, right?)");
        }

        if (EVENTS.containsKey(id)) {
            Apocalypse.LOGGER.warn("[{}] Attempted to register an event with duplicate ID: {}", EventRegister.class.getSimpleName(), id);
        }
        else {
            // He put himself in the EVENTS map, funniest shit I've ever seen
            EVENTS.put(id, event);
        }
        return event;
    }

    // Class loading :D
    public static void init() {}
}
