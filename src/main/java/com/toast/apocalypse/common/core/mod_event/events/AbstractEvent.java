package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.mod_event.EventType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public abstract class AbstractEvent {

    protected final EventType<?> type;
    /** Increments by 1 every time the player dies. */
    protected int eventGeneration = 0;

    public AbstractEvent(EventType<?> type) {
        this.type = type;
    }

    public final EventType<?> getType() {
        return this.type;
    }

    public int getEventGeneration() {
        return eventGeneration;
    }

    public void setEventGeneration(int generation) {
        eventGeneration = generation;
    }

    /** Called when the event starts.
     * Variables should all be set to default values here.
     */
    public abstract void onStart(MinecraftServer server, ServerPlayerEntity player);

    /** Called every 5 ticks for each player to update the event.
     *
     * @param player The player to update this event for.
     */
    public abstract void update(ServerWorld world, ServerPlayerEntity player, PlayerDifficultyManager difficultyManager);

    /** Called when the event ends. */
    public abstract void onEnd(MinecraftServer server, ServerPlayerEntity player);

    /**
     *  Called when the player disconnects
     *  before the event should be over.
     */
    public abstract void stop(ServerWorld world);

    /**
     * Called from {@link com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager#onPlayerDeath(LivingDeathEvent)}
     */
    public void onPlayerDeath(ServerPlayerEntity player, ServerWorld world) {
        // Just recount from 0 if the player dies 20 times, no need to go on forever.
        if (++eventGeneration >= 100)
            eventGeneration = 0;
    }

    /**
     * Saves this event.
     *
     * @param data The tag to write to.
     */
    public final void write(CompoundNBT data) {
        data.putInt("EventId", this.getType().getId());
        data.putInt("EventGeneration", getEventGeneration());

        this.writeAdditional(data);
    }

    public abstract void writeAdditional(CompoundNBT data);

    /**
     * Loads this event.
     *
     * @param data the tag to read from.
     */
    public void read(CompoundNBT data, ServerPlayerEntity player, ServerWorld world) {
        if (data.contains("EventGeneration", Constants.NBT.TAG_ANY_NUMERIC)) {
            eventGeneration = data.getInt("EventGeneration");
        }
    }
}
