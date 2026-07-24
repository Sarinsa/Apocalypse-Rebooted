package com.toast.apocalypse.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/** Provides access to player Apocalypse difficulty data. */
public interface IDifficultyAccessor {
    
    /** @return The player's current difficulty rate/multiplier. */
    double getDifficultyRate( Player player );
    
    /** @return The current difficulty of the specified player. */
    long getPlayerDifficulty( Player player );
    
    /** @return The current max difficulty of the specified player. */
    long getMaxPlayerDifficulty( Player player );
    
    /**
     * @return An immutable view of the numerical IDs of the given player's
     * currently running Apocalypse events.
     * <strong>(Server side only)</strong>
     * @see com.toast.apocalypse.api.event.ApocalypseEvent.EventIds Overview of valid Apocalypse event IDs.
     */
    List<Integer> getEventIds( ServerPlayer player );
}
