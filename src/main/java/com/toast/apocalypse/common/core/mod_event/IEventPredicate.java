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
    boolean test( ServerLevel serverWorld, ServerPlayer player, double scaledDifficulty, PlayerDifficultyManager difficultyManager );
    
    /** @return The given event predicate as a {@link RisingEdge} predicate. */
    static IEventPredicate risingEdge( IEventPredicate eventPredicate ) {
        return new RisingEdge( eventPredicate );
    }
    
    /**
     * Wraps an event predicate with logic that only returns 'true' when
     * that predicate changes its result from 'false' to 'true'.
     */
    class RisingEdge implements IEventPredicate {
        private final IEventPredicate predicate;
        private Boolean lastTest = null;
        
        public RisingEdge( IEventPredicate startPredicate ) { predicate = startPredicate; }
        
        @Override
        public boolean test( ServerLevel serverWorld, ServerPlayer player, double scaledDifficulty, PlayerDifficultyManager difficultyManager ) {
            boolean newTest = predicate.test( serverWorld, player, scaledDifficulty, difficultyManager );
            if( lastTest != null && !lastTest && newTest ) {
                return lastTest = true;
            }
            lastTest = newTest;
            return false;
        }
    }
}