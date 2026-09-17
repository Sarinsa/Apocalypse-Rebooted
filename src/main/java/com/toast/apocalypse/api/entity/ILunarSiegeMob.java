package com.toast.apocalypse.api.entity;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Represents a mob specialized for lunar sieges.
 * <br>
 * If a mob class that implements this interface is spawned by a lunar siege event,
 * the event will be able to set the mob's target when it spawns.
 */
@Deprecated
// Siege targeting will be NBT-driven in the future, and utility methods
// will be moved elsewhere in the API, making this interface obsolete in future updates.
public interface ILunarSiegeMob {
    
    // Key used for storing a lunar siege mob's player target UUID to NBT
    String TAG_PLAYER_UUID = "PlayerTargetUUID";
    
    /**
     * @return The UUID of this lunar siege mob's siege player target.
     * Lunar siege mobs spawned from commands or spawn eggs will normally not have a target UUID,
     * in which case this is expected to return null.
     */
    @Nullable
    UUID getPlayerTargetUUID();
    
    /**
     * Sets this lunar siege mob's siege player target UUID.
     *
     * @param playerTargetUUID The UUID of the specified player to target.
     */
    void setPlayerTargetUUID( @Nullable UUID playerTargetUUID );
    
    /**
     * @return This lunar siege mob's siege player target, or null if target UUID is null
     * or the player does not exist in the same level as the mob.
     */
    @Nullable
    default <T extends LivingEntity & ILunarSiegeMob> Player getEventTarget( T siegeMob ) {
        if( siegeMob.getPlayerTargetUUID() != null ) {
            // noinspection resource
            return siegeMob.level().getPlayerByUUID( siegeMob.getPlayerTargetUUID() );
        }
        return null;
    }
    
    /** Helper method for sending despawn smoke particles to the server. */
    static void despawnSmokeEffect( ServerLevel level, Mob mob ) {
        for( int i = 0; i < 8; i++ ) {
            level.sendParticles( ApocalypseObjects.ParticleTypes.LUNAR_DESPAWN_SMOKE.get(),
                    mob.getX(), mob.getY(), mob.getZ(),
                    4, 0.1, 0.1, 0.1, 0.1
            );
        }
    }
}