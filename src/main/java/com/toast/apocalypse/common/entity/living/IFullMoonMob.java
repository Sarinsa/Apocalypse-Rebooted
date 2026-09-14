package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import net.minecraft.server.level.ServerLevel;
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
    
    /**
     * @return The UUID of this full moon mob's set
     * player target. Full moon mobs spawned
     * from commands or spawn eggs will normally
     * not have a target UUID, and may return null.
     */
    @Nullable
    UUID getPlayerTargetUUID();
    
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
            return moonMob.level().getPlayerByUUID( moonMob.getPlayerTargetUUID() );
        }
        return null;
    }
    
    static void spawnSmoke( ServerLevel level, Mob mob ) {
        for( int i = 0; i < 8; i++ ) {
            level.sendParticles( ApocalypseObjects.ParticleTypes.LUNAR_DESPAWN_SMOKE.get(), mob.getX(), mob.getY(), mob.getZ(), 4, 0.1, 0.1, 0.1, 0.1 );
        }
    }
}