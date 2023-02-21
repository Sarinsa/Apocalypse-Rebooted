package com.toast.apocalypse.api.impl;

import com.toast.apocalypse.api.plugin.IDifficultyProvider;
import com.toast.apocalypse.common.util.CapabilityHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class DifficultyProvider implements IDifficultyProvider {

    @Override
    public double getDifficultyRate(Player player) {
        return CapabilityHelper.getPlayerDifficultyMult(player);
    }

    @Override
    public long getPlayerDifficulty(Player player) {
        return CapabilityHelper.getPlayerDifficulty(player);
    }

    @Override
    public long getMaxPlayerDifficulty(Player player) {
        return CapabilityHelper.getMaxPlayerDifficulty(player);
    }

    @Override
    public int currentEventId(ServerPlayer player) {
        return CapabilityHelper.getEventId(player);
    }
}
