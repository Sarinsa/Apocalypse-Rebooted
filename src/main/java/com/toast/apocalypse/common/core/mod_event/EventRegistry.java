package com.toast.apocalypse.common.core.mod_event;

import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.mod_event.events.*;
import com.toast.apocalypse.common.util.References;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;

/**
 * The registry for Apocalypse's event types.<br>
 * Intended for internal use (other mods creating their own types is not really supported).
 */
public class EventRegistry {

    public static final HashMap<Integer, EventType<?>> EVENTS = new HashMap<>();
    private static int idIndex = -1;


    public static final EventType<?> NONE = register("none", EmptyEvent::new, null, 0,
            (serverWorld, player, difficulty, difficultyManager) -> false,
            (serverWorld, player, difficulty, difficultyManager) -> false);

    public static final EventType<?> FULL_MOON = register("full_moon_siege", FullMoonEvent::new, References.FULL_MOON, 100,
            (serverWorld, player, difficulty, difficultyManager) ->
                    ApocalypseConfig.LUNAR_SIEGE.GENERAL.enableLunarSieges.get() && difficulty > 0 && difficultyManager.isFullMoonNight(),
            null);

    public static final EventType<?> THUNDERSTORM = register("thunderstorm", ThunderstormEvent::new, References.THUNDERSTORM, 2,
            (serverWorld, player, difficulty, difficultyManager) -> serverWorld.isThundering(),
            null);

    public static final EventType<?> ACID_RAIN = register("acid_rain", AcidRainEvent::new, References.ACID_RAIN, 1,
            (serverWorld, player, difficulty, difficultyManager) -> difficultyManager.isRainingAcid(serverWorld),
            null);


    /**
     * Looks through the event registry for an entry that
     * has the given numerical ID and returns it.<br>
     * If one isn't found, this returns null.
     */
    @Nullable
    public static EventType<?> getFromId(int id) {
        if (!EVENTS.containsKey(id)) {
            return null;
        }
        return EVENTS.get(id);
    }

    /**
     * Registers a new event type to the event registry and returns it.
     *
     * @param name The named id of this event type.
     * @param factory The event factory of this event type. Used to instantiate a new event.
     * @param startMessage The translation key for the message that should be displayed to the player when the event starts.
     * @param priority The tick priority of this event type.
     * @param startupPredicate The predicate used for testing if this event can start.
     * @param persistPredicate The predicate used for testing if this event can continue running after it has started.
     *                         If this is null, the startup predicate will be used instead.
     */
    private static <T extends AbstractEvent> EventType<T> register(String name,  @Nonnull EventType.IEventFactory<T> factory,
                                                                   String startMessage, int priority, @Nonnull IEventPredicate startupPredicate,
                                                                   @Nullable IEventPredicate persistPredicate) {
        Objects.requireNonNull(factory);
        Objects.requireNonNull(startupPredicate);

        EventType<T> eventType = new EventType<>(idIndex++, name, factory, startMessage, priority, startupPredicate, persistPredicate);
        EVENTS.put(idIndex, eventType);
        return eventType;
    }

    // Class loading epic moment
    public static void init() {

    }
}
