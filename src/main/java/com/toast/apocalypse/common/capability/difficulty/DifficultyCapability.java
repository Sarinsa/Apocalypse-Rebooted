package com.toast.apocalypse.common.capability.difficulty;

import com.toast.apocalypse.common.core.config.ApocalypseServerConfig;
import com.toast.apocalypse.common.util.References;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.checkerframework.checker.units.qual.C;

public class DifficultyCapability implements IDifficultyCapability {

    private long difficulty =- (long) ApocalypseServerConfig.SERVER.getPlayerGracePeriod() * References.DAY_LENGTH;
    private long maxDifficulty = (long) ApocalypseServerConfig.SERVER.getDefaultPlayerMaxDifficulty() * References.DAY_LENGTH;

    private double difficultyMult = 0.0D;

    @Override
    public void setDifficulty(long difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public long getDifficulty() {
        return this.difficulty;
    }

    @Override
    public void setMaxDifficulty(long maxDifficulty) {
        this.maxDifficulty = maxDifficulty;
    }

    @Override
    public long getMaxDifficulty() {
        return this.maxDifficulty;
    }

    @Override
    public void setDifficultyMult(double multiplier) {
        this.difficultyMult = multiplier;
    }

    @Override
    public double getDifficultyMult() {
        return this.difficultyMult;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putLong("Difficulty", difficulty);
        tag.putLong("MaxDifficulty", maxDifficulty);
        tag.putDouble("DifficultyMul", difficultyMult);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        if (compoundTag.contains("Difficulty", Tag.TAG_LONG))
            difficulty = compoundTag.getLong("Difficulty");

        if (compoundTag.contains("MaxDifficulty", Tag.TAG_LONG))
            maxDifficulty = compoundTag.getLong("MaxDifficulty");

        if (compoundTag.contains("DifficultyMul", Tag.TAG_DOUBLE))
            difficultyMult = compoundTag.getDouble("DifficultyMul");
    }
}
