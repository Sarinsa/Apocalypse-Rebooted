package com.toast.apocalypse.common.capability.event_data;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EventDataProvider implements ICapabilitySerializable<CompoundTag> {

    public static final IEventDataCapability INSTANCE = new EventDataCapability();
    private final LazyOptional<IEventDataCapability> optional = LazyOptional.of(() -> INSTANCE);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ApocalypseCapabilities.EVENT_DATA_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return ApocalypseCapabilities.EVENT_DATA_CAPABILITY.orEmpty(ApocalypseCapabilities.EVENT_DATA_CAPABILITY, optional).orElse(INSTANCE).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ApocalypseCapabilities.EVENT_DATA_CAPABILITY.orEmpty(ApocalypseCapabilities.EVENT_DATA_CAPABILITY, optional).orElse(INSTANCE).deserializeNBT(nbt);
    }
}