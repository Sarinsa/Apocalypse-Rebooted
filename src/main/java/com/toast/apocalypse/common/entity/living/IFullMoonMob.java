package com.toast.apocalypse.common.entity.living;

import javax.annotation.Nullable;
import java.util.UUID;

/** Represents a mob type that spawns during full moons */
public interface IFullMoonMob {

    /** Key used for storing the full moon mob's player target UUID to NBT. */
    String PLAYER_UUID_KEY = "PlayerTargetUUID";

    /**
     * @return The UUID of this full moon mob's set
     *         player target. Full moon mobs spawned
     *         from commands or spawn eggs will normally
     *         not have a target UUID, and may return null.
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
    void setPlayerTargetUUID(@Nullable UUID playerTargetUUID);
}
