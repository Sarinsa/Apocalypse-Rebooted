package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.mod_event.EventRegistry;
import com.toast.apocalypse.common.core.mod_event.events.AbstractEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

// TODO - Move this to API? And definitely rephrase some docs

/** Represents a specialized mob for full moon sieges. */
public interface IFullMoonMob {
    
    /** Key used for storing the full moon mob's player target UUID to NBT. */
    String KEY_PLAYER_UUID = "PlayerTargetUUID";
    /** Key used for storing the full moon mob's player death count to NBT. */
    String KEY_TARGET_DEATH_COUNT = "PlayerDeathCount";
    
    /**
     * @return The UUID of this full moon mob's set
     * player target. Full moon mobs spawned
     * from commands or spawn eggs will normally
     * not have a target UUID, and may return null.
     */
    @Nullable
    UUID getPlayerTargetUUID();
    
    /**
     * @return The amount of times this mob's target player has died
     * since this mob first spawned.
     * <br><br>
     * When the event starts, we are at 0.
     * When the player dies, the event's internal death count increments by 1,
     * and the full moon mobs that have already spawned will be despawned if it's
     * death count value is less than the current death count.
     */
    int getPlayerDeathCount();
    
    /**
     * Sets the death count of the player this mob is tracking.
     */
    void setPlayerDeathCount( int deathCount );
    
    /**
     * Sets this full moon mob's target UUID.<br>
     * The target UUID is the UUID of the player
     * this full moon mob was spawned for, if spawned
     * from a full moon siege event.<br>
     * <br>
     *
     * @param playerTargetUUID The UUID of the specified player to target.
     */
    void setPlayerTargetUUID( @Nullable UUID playerTargetUUID );
    
    @Nullable
    static <E extends LivingEntity & IFullMoonMob> Player getEventTarget( E moonMob ) {
        if( moonMob.getPlayerTargetUUID() != null ) {
            // noinspection resource
            return moonMob.level().getPlayerByUUID( moonMob.getPlayerTargetUUID() );
        }
        return null;
    }
    
    /**
     * This is a bit weird to explain, but here goes!<br>
     * <br>
     * When the player dies, their <strong>"event generation"</strong> increments. Full moon mobs
     * stores the value of what the event generation was when they spawned. This method
     * checks if the full moon mob's stored value is <strong>different</strong> from the player's current
     * event generation, in which case it should despawn.
     */
    static boolean shouldDisappear( @Nullable UUID playerTargetUUID, ServerLevel level, IFullMoonMob moonMob ) {
        if( !ApocalypseConfig.LUNAR_SIEGE.GENERAL.despawnMobsOnDeath.get() )
            return false;
        
        if( playerTargetUUID == null )
            return false;
        
        ServerPlayer player = level.getServer().getPlayerList().getPlayer( playerTargetUUID );
        
        // Player might be offline, do nothing
        if( player == null )
            return false;
        
        AbstractEvent event = Apocalypse.INSTANCE.getDifficultyManager().getEvent( player, EventRegistry.LUNAR_SIEGE );
        
        if( event != null ) {
            final int deathCount = event.getPlayerDeathCount();
            return moonMob.getPlayerDeathCount() != deathCount;
        }
        return false;
    }
    
    static void spawnSmoke( ServerLevel level, Mob mob ) {
        for( int i = 0; i < 8; i++ ) {
            level.sendParticles( ApocalypseObjects.ParticleTypes.LUNAR_DESPAWN_SMOKE.get(), mob.getX(), mob.getY(), mob.getZ(), 4, 0.1, 0.1, 0.1, 0.1 );
        }
    }
}
