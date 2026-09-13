package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.mod_event.EventType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

// TODO - Currently does nothing other than displaying an event message.
//        Perhaps other mechanics or mobs can be introduced in the future.
public final class AcidRainEvent extends AbstractEvent {
    
    public AcidRainEvent( EventType<?> type ) {
        super( type );
    }
    
    /** @return True if this event can start. */
    @SuppressWarnings( "unused" )
    public static boolean canStart( ServerLevel serverLevel, ServerPlayer player, double scaledDifficulty, PlayerDifficultyManager difficultyManager ) {
        return difficultyManager.isRainingAcid( serverLevel );
    }
    
    /** Called when this event starts. */
    @Override
    public void onStart( MinecraftServer server, ServerPlayer player ) { }
    
    /**
     * Called every 5 ticks on the server to tick this event.
     *
     * @param player The player to update this event for.
     */
    @Override
    public void update( ServerLevel level, ServerPlayer player, PlayerDifficultyManager difficultyManager ) { }
    
    /** Called before each update to check if this event should keep running. */
    @Override
    public boolean shouldContinueRunning( ServerLevel level, ServerPlayer player, double scaledDifficulty, PlayerDifficultyManager difficultyManager ) {
        return difficultyManager.isRainingAcid( level );
    }
    
    /** Called when the event ends naturally. */
    @Override
    public void onEnd( MinecraftServer server, ServerPlayer player ) { }
    
    /** Called when the player disconnects before the event can end naturally. */
    @Override
    public void stop( ServerLevel level, ServerPlayer player ) { }
}
