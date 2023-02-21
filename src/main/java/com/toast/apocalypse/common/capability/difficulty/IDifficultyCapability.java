package com.toast.apocalypse.common.capability.difficulty;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IDifficultyCapability extends INBTSerializable<CompoundTag> {

    void setDifficulty(long difficulty);

    long getDifficulty();

    void setMaxDifficulty(long maxDifficulty);

    long getMaxDifficulty();

    void setDifficultyMult(double multiplier);

    double getDifficultyMult();
}
