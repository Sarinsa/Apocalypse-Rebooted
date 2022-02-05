package com.toast.apocalypse.common.core.mod_event;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

/**
 * Provides the conditions for a specified Apocalypse event to start.
 */
@FunctionalInterface
public interface IStartupPredicate {

    boolean canStart(ServerWorld serverWorld, EventType<?> currentEventType, ServerPlayerEntity player, boolean isFullMoonNight);
}
