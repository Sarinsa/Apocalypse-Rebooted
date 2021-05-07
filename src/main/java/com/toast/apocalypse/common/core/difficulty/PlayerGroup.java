package com.toast.apocalypse.common.core.difficulty;

import com.toast.apocalypse.common.util.CapabilityHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerGroup {

    private final List<ServerPlayerEntity> players;
    private long effectiveDifficulty;

    public PlayerGroup(@Nullable ServerPlayerEntity... playerEntities) {
        this.players = new ArrayList<>();

        if (playerEntities != null) {
            this.players.addAll(Arrays.asList(playerEntities));
        }
    }

    public void tick() {
        recalculateDifficulty();
    }

    public List<ServerPlayerEntity> getPlayers() {
        return this.players;
    }

    public long getEffectiveDifficulty() {
        return this.effectiveDifficulty;
    }

    public void recalculateDifficulty() {
        long difficulty = 0;

        for (PlayerEntity player : this.players) {
            difficulty += CapabilityHelper.getPlayerDifficulty(player);
        }
        this.effectiveDifficulty = difficulty / this.players.size();
    }
}
