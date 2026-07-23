package com.toast.apocalypse.common.core.mod_event.events;

import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.mod_event.EventType;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public abstract class AbstractEvent {
    
    /** The NBT key for storing event ID. */
    public static final String KEY_EVENT_ID = "EventId";
    /** The NBT key for storing the player death count. */
    public static final String KEY_DEATH_COUNT = "PlayerDeathCount";
    
    /** The event's event type. */
    protected final EventType<?> type;
    
    /** Increments by 1 every time the player dies. */
    protected int deathCount = 0;
    
    
    public AbstractEvent( EventType<?> type ) {
        this.type = type;
    }
    
    
    /** @return The {@link EventType} of this event. */
    public final EventType<?> getType() {
        return type;
    }
    
    /** @return The amount of times the player has died since this event started. */
    public int getPlayerDeathCount() {
        return deathCount;
    }
    
    /** Sets the current amount of times the player has died since this event started. */
    public void setDeathCount( int deathCount ) {
        this.deathCount = deathCount;
    }
    
    /** Called when this event starts. */
    public abstract void onStart( MinecraftServer server, ServerPlayer player );
    
    /**
     * Called every 5 ticks on the server to tick this event.
     *
     * @param player The player to update this event for.
     */
    public abstract void update( ServerLevel level, ServerPlayer player, PlayerDifficultyManager difficultyManager );
    
    /** Called before each update to check if this event should keep running. */
    public abstract boolean shouldContinueRunning( ServerLevel level, ServerPlayer player, double scaledDifficulty, PlayerDifficultyManager difficultyManager );
    
    /** Called when the event ends. */
    public abstract void onEnd( MinecraftServer server, ServerPlayer player );
    
    /** Called when the player disconnects before the event can end naturally. */
    public abstract void stop( ServerLevel level, ServerPlayer player );
    
    /**
     * Called from {@link PlayerDifficultyManager#onPlayerDeath(LivingDeathEvent)}.
     */
    public void onPlayerDeath( ServerPlayer player, ServerLevel world ) {
        if( ++deathCount >= 100 ) deathCount = 0;
    }
    
    /**
     * Saves this event's data to NBT.
     *
     * @param data The tag to write to.
     */
    public final void write( CompoundTag data ) {
        data.putInt( KEY_EVENT_ID, getType().getId() );
        data.putInt( KEY_DEATH_COUNT, getPlayerDeathCount() );
        writeAdditional( data );
    }
    
    public abstract void writeAdditional( CompoundTag data );
    
    /**
     * Loads this event from the given NBT.
     *
     * @param data the tag to read from.
     */
    public void read( CompoundTag data, ServerPlayer player, ServerLevel level ) {
        if( NBTHelper.containsNumber( data, KEY_DEATH_COUNT ) ) {
            deathCount = data.getInt( KEY_DEATH_COUNT );
        }
    }
}
