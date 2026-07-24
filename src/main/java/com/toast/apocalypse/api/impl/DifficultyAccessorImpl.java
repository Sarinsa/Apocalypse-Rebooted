package com.toast.apocalypse.api.impl;

import com.google.common.collect.ImmutableList;
import com.toast.apocalypse.api.IDifficultyAccessor;
import com.toast.apocalypse.common.capability.CapabilityHelper;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.mod_event.EventType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/** Provides access to player Apocalypse difficulty data. */
public final class DifficultyAccessorImpl implements IDifficultyAccessor {
    
    private static final List<Integer> EMPTY_ID_LIST = ImmutableList.of();
    
    /** @return The player's current difficulty rate/multiplier. */
    @Override
    public double getDifficultyRate( Player player ) {
        return CapabilityHelper.getPlayerDifficultyMult( player );
    }
    
    /** @return The current difficulty of the specified player. */
    @Override
    public long getPlayerDifficulty( Player player ) {
        return CapabilityHelper.getPlayerDifficulty( player );
    }
    
    /** @return The current max difficulty of the specified player. */
    @Override
    public long getMaxPlayerDifficulty( Player player ) {
        return CapabilityHelper.getMaxPlayerDifficulty( player );
    }
    
    /**
     * @return An immutable view of the numerical IDs of the given player's
     * currently running Apocalypse events.
     * <strong>(Server side only)</strong>
     * @see com.toast.apocalypse.api.event.ApocalypseEvent.EventIds Overview of valid Apocalypse event IDs.
     */
    @Override
    public List<Integer> getEventIds( ServerPlayer player ) {
        Iterable<EventType<?>> eventTypes = Apocalypse.INSTANCE.getDifficultyManager().getEventTypes( player );
        
        if( eventTypes == null ) return EMPTY_ID_LIST;
        
        List<Integer> eventIds = new ArrayList<>();
        
        for( EventType<?> eventType : eventTypes ) {
            eventIds.add( eventType.getId() );
        }
        return ImmutableList.copyOf( eventIds );
    }
}
