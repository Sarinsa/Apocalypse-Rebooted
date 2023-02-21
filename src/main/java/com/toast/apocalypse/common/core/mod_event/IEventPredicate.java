package com.toast.apocalypse.common.core.mod_event;

import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Provides the conditions for a specified Apocalypse event to start.
 */
@FunctionalInterface
public interface IEventPredicate {

    boolean test(ServerLevel serverWorld, EventType<?> currentEventType, ServerPlayer player, PlayerDifficultyManager difficultyManager);
}
