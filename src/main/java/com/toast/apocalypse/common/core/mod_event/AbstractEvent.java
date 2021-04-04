package com.toast.apocalypse.common.core.mod_event;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public abstract class AbstractEvent {

    /** The ID for this event, must be unique */
    private final ResourceLocation id;

    public AbstractEvent(ResourceLocation id) {
        this.id = id;
    }

    /**
     * @return A ResourceLocation representing
     *         this event's unique ID.
     */
    @Nullable
    public final ResourceLocation getId() {
        return this.id;
    }

    /**
     * @return The translation key of the message
     *         that is sent to players when this
     *         event starts, which will later be
     *         parsed to a TranslationTextComponent
     */
    public abstract String getEventStartMessage();

    /** Whether the passed event can interrupt this one.
     *  All events should be able to be interrupted by the full moon event.
     *
     * @return True if this event can be interrupted.
     */
    public boolean canBeInterrupted(AbstractEvent event) {
        return event == EventRegister.FULL_MOON;
    }

    /** Called when the event starts.
     * Variables should all be set to default values here.
     */
    public abstract void onStart();

    /** Called every 5 ticks to update the event. */
    public abstract void update();

    /** Called every 5 ticks for each world to update the event.
     *
     * @param world The world to update for this event.
     */
    public abstract void update(ServerWorld world);

    /** Called every 5 ticks for each player to update the event.
     *
     * @param player The player to update for this event.
     */
    public abstract void update(PlayerEntity player);

    /** Called when the event ends. */
    public abstract void onEnd();

    /**
     * Saves this event.
     *
     * @param saveData The JsonObject to save to.
     * @throws JsonIOException If an exception occurs while trying to write.
     */
    public abstract JsonObject save(JsonObject saveData) throws JsonIOException;

    /**
     * Loads this event.
     *
     * @param loadData the JsonObject to read from.
     * @throws JsonIOException If an exception occurs while trying to read.
     */
    public abstract void load(JsonObject loadData) throws JsonIOException;
}
