package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.mod_event.EventType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
    public abstract void onStart(MinecraftServer server, ServerPlayer player);

    /** Called every 5 ticks for each player to update the event.
     *
     * @param player The player to update this event for.
     */
    public abstract void update(ServerLevel level, ServerPlayer player, PlayerDifficultyManager difficultyManager);

    /** Called when the event ends. */
    public abstract void onEnd(MinecraftServer server, ServerPlayer player);

    /**
     *  Called when the player disconnects
     *  before the event should be over.
     */
    public abstract void stop(ServerLevel level);

    /**
     * Called from {@link PlayerDifficultyManager#onPlayerDeath(LivingDeathEvent)}
     */
    public void onPlayerDeath(ServerPlayer player, ServerLevel world) {
        // Just recount from 0 if the player dies 20 times, no need to go on forever.
        if (++eventGeneration >= 100)
            eventGeneration = 0;
    }

    /**
     * Saves this event.
     *
     * @param data The tag to write to.
     */
    public final void write(CompoundTag data) {
        data.putInt("EventId", this.getType().getId());
        data.putInt("EventGeneration", getEventGeneration());

        this.writeAdditional(data);
    }

    public abstract void writeAdditional(CompoundTag data);

    /**
     * Loads this event.
     *
     * @param data the tag to read from.
     */
    public void read(CompoundTag data, ServerPlayer player, ServerLevel level) {
        if (data.contains("EventGeneration", Tag.TAG_ANY_NUMERIC)) {
            eventGeneration = data.getInt("EventGeneration");
        }
    }
}
