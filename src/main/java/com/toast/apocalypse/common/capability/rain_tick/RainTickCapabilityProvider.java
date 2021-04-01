package com.toast.apocalypse.common.capability.rain_tick;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("all")
public class RainTickCapabilityProvider implements ICapabilitySerializable<IntNBT> {

    private final IRainTickCapability INSTANCE = ApocalypseCapabilities.RAIN_TICK_CAPABILITY.getDefaultInstance();
    private final LazyOptional<IRainTickCapability> optional = LazyOptional.of(() -> INSTANCE);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ApocalypseCapabilities.RAIN_TICK_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public IntNBT serializeNBT() {
        return (IntNBT) ApocalypseCapabilities.RAIN_TICK_CAPABILITY.getStorage().writeNBT(ApocalypseCapabilities.RAIN_TICK_CAPABILITY, INSTANCE, null);
    }

    @Override
    public void deserializeNBT(IntNBT nbt) {
        ApocalypseCapabilities.RAIN_TICK_CAPABILITY.getStorage().readNBT(ApocalypseCapabilities.RAIN_TICK_CAPABILITY, INSTANCE, null, nbt);
    }
}
