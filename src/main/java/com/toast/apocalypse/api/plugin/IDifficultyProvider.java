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
     * @return The player's current difficulty rate/multiplier.
     */
    <T extends PlayerEntity> double getDifficultyRate(T player);

    /**
     * @return The current difficulty of the specified player.
     */
    <T extends PlayerEntity> long getPlayerDifficulty(T player);

    /**
     * @return The current max difficulty of the specified player.
     */
    <T extends PlayerEntity> long getMaxPlayerDifficulty(T player);

    /**
     * @return The ID of the given player's
     *         currently running event.
     *         (Server side only)
     *
     * No event: -1
     *
     * Full Moon Siege: 0
     *
     * Thunderstorm: 1
     */
    <T extends ServerPlayerEntity> int currentEventId(T player);
}
