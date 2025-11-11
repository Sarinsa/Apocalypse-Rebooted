package com.toast.apocalypse.api.plugin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * A simple interface for reading various
 * difficulty related data from players.
 */
public interface DifficultyProvider {
    
    /**
     * @return The player's current difficulty rate/multiplier.
     */
    <T extends Player> double getDifficultyRate( T player );
    
    /**
     * @return The current difficulty of the specified player.
     */
    <T extends Player> long getPlayerDifficulty( T player );
    
    /**
     * @return The current max difficulty of the specified player.
     */
    <T extends Player> long getMaxPlayerDifficulty( T player );
    
    /**
     * @return A list containing the numerical IDs of the given player's
     * currently running apocalypse events.
     * <strong>(Server side only)</strong><br><br>
     * <p>
     * 0: Lunar Siege
     * <br><br>
     * 1: Thunderstorm
     * <br><br>
     * 2: Acid rain
     * <br><br>
     * 3: Call of The Shadows
     */
    <T extends ServerPlayer> List<Integer> getEventIds( T player );
}
