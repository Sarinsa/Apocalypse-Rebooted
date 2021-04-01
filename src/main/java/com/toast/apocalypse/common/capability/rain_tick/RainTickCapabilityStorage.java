package com.toast.apocalypse.common.capability.rain_tick;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class RainTickCapabilityStorage implements Capability.IStorage<IRainTickCapability> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IRainTickCapability> capability, IRainTickCapability instance, Direction side) {
        return null;
    }

    @Override
    public void readNBT(Capability<IRainTickCapability> capability, IRainTickCapability instance, Direction side, INBT nbt) {

    }
}
