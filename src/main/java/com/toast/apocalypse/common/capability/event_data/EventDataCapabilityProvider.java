package com.toast.apocalypse.common.capability.event_data;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("all")
public class EventDataCapabilityProvider implements ICapabilitySerializable<CompoundNBT> {

    private final IEventDataCapability INSTANCE = ApocalypseCapabilities.EVENT_DATA_CAPABILITY.getDefaultInstance();
    private final LazyOptional<IEventDataCapability> optional = LazyOptional.of(() -> INSTANCE);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ApocalypseCapabilities.EVENT_DATA_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) ApocalypseCapabilities.EVENT_DATA_CAPABILITY.getStorage().writeNBT(ApocalypseCapabilities.EVENT_DATA_CAPABILITY, INSTANCE, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        ApocalypseCapabilities.EVENT_DATA_CAPABILITY.getStorage().readNBT(ApocalypseCapabilities.EVENT_DATA_CAPABILITY, INSTANCE, null, nbt);
    }
}