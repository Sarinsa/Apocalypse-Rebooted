package com.toast.apocalypse.common.core.mod_event;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractEvent {

    /** The ID for this event, must be unique */
    private final int id;

    public AbstractEvent(int id) {
        this.id = id;
    }

    /**
     * @return A ResourceLocation representing
     *         this event's unique ID.
     */
    public final int getId() {
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
    public abstract void onStart(MinecraftServer server);

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
     * @param data The tag to write to.
     */
    public abstract CompoundNBT write(CompoundNBT data);

    /**
     * Loads this event.
     *
     * @param data the tag to read from.
     */
    public abstract void read(CompoundNBT data) throws JsonIOException;
}
