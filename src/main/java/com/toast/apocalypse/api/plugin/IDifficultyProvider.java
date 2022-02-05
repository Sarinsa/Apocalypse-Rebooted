package com.toast.apocalypse.api.plugin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * A simple interface for reading various
 * difficulty related data from players.
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
     *         <strong>(Server side only)</strong><br>
     *         <br>
     *
     * No event: -1<br>
     * <br>
     *
     * Full Moon Siege: 0<br>
     * <br>
     *
     * Thunderstorm: 1<br>
     * <br>
     */
    <T extends ServerPlayerEntity> int currentEventId(T player);
}
