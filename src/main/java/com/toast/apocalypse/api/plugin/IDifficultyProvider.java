package com.toast.apocalypse.api.plugin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * A simple interface that lets modders check
 * Apocalypse's current difficulty rate and
 * active event.
 */
public interface IDifficultyProvider {

    /**
     * @return The current world difficulty rate.
     */
    double getDifficultyRate(PlayerEntity player);

    /**
     * @return The current difficulty of the specified player.
     */
    long getPlayerDifficulty(PlayerEntity player);

    /**
     * @return The current max difficulty of the specified player.
     */
    long getMaxPlayerDifficulty(PlayerEntity player);

    /**
     * @return The ID of the given player's
     *         currently running event.
     *
     * No event: -1
     *
     * Full moon: 0
     *
     * Thunderstorm: 1
     */
    int currentEventId(ServerPlayerEntity player);
}
