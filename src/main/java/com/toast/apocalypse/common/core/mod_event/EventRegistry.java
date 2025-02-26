package com.toast.apocalypse.common.core.mod_event;

import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.config.COTSConfig;
import com.toast.apocalypse.common.core.mod_event.events.*;
import com.toast.apocalypse.common.event.RainDamageTickHandler;
import com.toast.apocalypse.common.util.References;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.lighting.SkyLightEngine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;

/**
 * The registry for Apocalypse's event types.<br>
 * Intended for internal use. Other mods can create their own events if desired, but it is not really supported.
 */
public class EventRegistry {

    public static final HashMap<Integer, EventType<?>> EVENTS = new HashMap<>();
    private static int idIndex = 0;


    public static final EventType<?> FULL_MOON = register("full_moon_siege", FullMoonEvent::new, References.FULL_MOON,
            (serverLevel, player, difficulty, difficultyManager) ->
                    ApocalypseConfig.LUNAR_SIEGE.GENERAL.enableLunarSieges.get() && difficulty > 0 && difficultyManager.isFullMoonNight());

    public static final EventType<?> THUNDERSTORM = register("thunderstorm", ThunderstormEvent::new, References.THUNDERSTORM,
            (serverLevel, player, difficulty, difficultyManager) -> ApocalypseConfig.THUNDERSTORM.GENERAL.enabled.get() && serverLevel.isThundering());

    public static final EventType<?> ACID_RAIN = register("acid_rain", AcidRainEvent::new, References.ACID_RAIN,
            (serverLevel, player, difficulty, difficultyManager) -> difficultyManager.isRainingAcid(serverLevel));

    public static final EventType<?> CALL_OF_THE_SHADOWS = register("call_of_the_shadows", DarknessEvent::new, null,
            (serverLevel, player, difficulty, difficultyManager) -> {
                if (player.isCreative() || player.isSpectator() || !ApocalypseConfig.CALL_OF_THE_SHADOWS.GENERAL.enabled.get()) return false;

                BlockPos pos = player.blockPosition();
                return serverLevel.getBrightness(LightLayer.SKY, pos) <= ApocalypseConfig.CALL_OF_THE_SHADOWS.GENERAL.skyLightLevel.get()
                        && serverLevel.getBrightness(LightLayer.BLOCK, pos) <= ApocalypseConfig.CALL_OF_THE_SHADOWS.GENERAL.blockLightLevel.get();
    });



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
     * @param startupPredicate The predicate used for testing if this event can start.
     */
    public static <T extends AbstractEvent> EventType<T> register(String name,  @Nonnull EventType.IEventFactory<T> factory,
                                                                   @Nullable String startMessage, @Nonnull IEventPredicate startupPredicate) {
        Objects.requireNonNull(factory);

        EventType<T> eventType = new EventType<>(idIndex++, name, factory, startMessage, startupPredicate);
        EVENTS.put(idIndex, eventType);
        return eventType;
    }

    // Class loading epic moment
    public static void init() {
    }
}
