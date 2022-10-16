package com.toast.apocalypse.api.impl;

import com.toast.apocalypse.api.plugin.IDifficultyProvider;
import com.toast.apocalypse.common.util.CapabilityHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class DifficultyProvider implements IDifficultyProvider {

    @Override
    public double getDifficultyRate(PlayerEntity player) {
        return CapabilityHelper.getPlayerDifficultyMult(player);
    }

    @Override
    public long getPlayerDifficulty(PlayerEntity player) {
        return CapabilityHelper.getPlayerDifficulty(player);
    }

    @Override
    public long getMaxPlayerDifficulty(PlayerEntity player) {
        return CapabilityHelper.getMaxPlayerDifficulty(player);
    }

    @Override
    public int currentEventId(ServerPlayerEntity player) {
        return CapabilityHelper.getEventId(player);
    }
}
