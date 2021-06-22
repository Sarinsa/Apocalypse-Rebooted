package com.toast.apocalypse.common.core.mod_event;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

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
    public abstract void read(CompoundNBT data);
}
