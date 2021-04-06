package com.toast.apocalypse.common.capability.difficulty;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class DifficultyCapabilityStorage implements Capability.IStorage<IDifficultyCapability> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IDifficultyCapability> capability, IDifficultyCapability instance, Direction side) {
        return LongNBT.valueOf(instance.getDifficulty());
    }

    @Override
    public void readNBT(Capability<IDifficultyCapability> capability, IDifficultyCapability instance, Direction side, INBT nbt) {
        if (nbt.getType() != LongNBT.TYPE) {
            Apocalypse.LOGGER.error("Failed to read difficulty capability data! The parsed data must be of type LongNBT");
            return;
        }
        instance.setDifficulty(((LongNBT)nbt).getAsLong());
    }
}
