package com.toast.apocalypse.common.entity.living;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

/** Represents a mob type that spawns during full moons */
public interface IFullMoonMob {

    @Nullable
    PlayerEntity getPlayerTarget();

    void setPlayerTarget(PlayerEntity playerTarget);
}
