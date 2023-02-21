package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.mod_event.EventType;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class EmptyEvent extends AbstractEvent {

    public EmptyEvent(EventType<?> type) {
        super(type);
    }

    @Override
    public void onStart(MinecraftServer server, ServerPlayer player) {

    }

    @Override
    public void update(ServerLevel level, ServerPlayer player, PlayerDifficultyManager difficultyManager) {

    }

    @Override
    public void onEnd(MinecraftServer server, ServerPlayer player) {

    }

    @Override
    public void stop(ServerLevel level) {

    }

    @Override
    public void writeAdditional(CompoundTag data) {

    }
}
