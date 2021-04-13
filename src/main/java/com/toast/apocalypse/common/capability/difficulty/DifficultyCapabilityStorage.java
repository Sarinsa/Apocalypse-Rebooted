package com.toast.apocalypse.common.capability.difficulty;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.util.References;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class DifficultyCapabilityStorage implements Capability.IStorage<IDifficultyCapability> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IDifficultyCapability> capability, IDifficultyCapability instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();

        tag.putLong("Difficulty", instance.getDifficulty());
        tag.putLong("MaxDifficulty", instance.getMaxDifficulty());

        return tag;
    }

    @Override
    public void readNBT(Capability<IDifficultyCapability> capability, IDifficultyCapability instance, Direction side, INBT nbt) {
        if (nbt.getType() != CompoundNBT.TYPE) {
            Apocalypse.LOGGER.error("Failed to read difficulty capability data! The parsed data must be of type CompoundNBT");
            return;
        }
        CompoundNBT tag = (CompoundNBT) nbt;

        instance.setDifficulty(tag.getLong("Difficulty"));
        instance.setMaxDifficulty(tag.getLong("MaxDifficulty"));
    }
}
