package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.core.mod_event.EventType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;

public final class EmptyEvent extends AbstractEvent {

    public EmptyEvent(EventType<?> type) {
        super(type);
    }

    @Override
    public void onStart(MinecraftServer server, ServerPlayerEntity player) {

    }

    @Override
    public void update(ServerWorld world, ServerPlayerEntity player) {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public void stop(ServerWorld world) {

    }

    @Override
    public CompoundNBT write(CompoundNBT data) {
        data.putInt("EventId", this.getType().getId());
        return data;
    }

    @Override
    public void read(CompoundNBT data, ServerWorld world)  {

    }
}