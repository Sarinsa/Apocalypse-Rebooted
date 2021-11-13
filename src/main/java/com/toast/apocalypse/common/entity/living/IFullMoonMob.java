package com.toast.apocalypse.common.entity.living;

import javax.annotation.Nullable;
import java.util.UUID;

/** Represents a mob type that spawns during full moons */
public interface IFullMoonMob {

    String PLAYER_UUID_KEY = "PlayerTargetUUID";

    @Nullable
    UUID getPlayerTargetUUID();

    void setPlayerTargetUUID(UUID playerTargetUUID);
}
