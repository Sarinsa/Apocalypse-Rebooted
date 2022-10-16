package com.toast.apocalypse.common.core.mod_event;

import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

/**
 * Provides the conditions for a specified Apocalypse event to start.
 */
@FunctionalInterface
public interface IEventPredicate {

    boolean test(ServerWorld serverWorld, EventType<?> currentEventType, ServerPlayerEntity player, PlayerDifficultyManager difficultyManager);
}
