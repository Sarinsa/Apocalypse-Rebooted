package com.toast.apocalypse.common.entity.living;

import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

/** Represents a mob type that spawns during full moons */
public interface IFullMoonMob {

    @Nullable
    PlayerEntity getPlayerTarget();

    void setPlayerTarget(PlayerEntity playerTarget);
}
