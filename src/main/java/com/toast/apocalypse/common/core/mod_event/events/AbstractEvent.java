package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.core.mod_event.EventType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractEvent {

    protected final ServerPlayerEntity player;
    protected final EventType<?> type;

    public AbstractEvent(EventType<?> type, ServerPlayerEntity player) {
        this.type = type;
        this.player = player;
    }

    public final EventType<?> getType() {
        return this.type;
    }

    /** Called when the event starts.
     * Variables should all be set to default values here.
     */
    public abstract void onStart(MinecraftServer server);

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

    /** Called when the player disconnects
     *  before the event should be over.
     */
    public abstract void stop();

    /**
     * Saves this event.
     *
     * @param data The tag to write to.
     */
    public CompoundNBT write(CompoundNBT data) {
        data.putInt("EventId", this.getType().getId());
        return data;
    }

    /**
     * Loads this event.
     *
     * @param data the tag to read from.
     */
    public void read(CompoundNBT data) {

    }
}
