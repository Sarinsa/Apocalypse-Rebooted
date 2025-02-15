package com.toast.apocalypse.common.core.mod_event;

import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Provides the conditions for a specified Apocalypse event to start.
 */
@FunctionalInterface
public interface IEventPredicate {

    /**
     * NOTE: Checking scaled difficulty is not a good idea for persist predicates. May be 0 when the player is dead.
     */
    boolean test(ServerLevel serverWorld, ServerPlayer player, double scaledDifficulty, PlayerDifficultyManager difficultyManager);
}
