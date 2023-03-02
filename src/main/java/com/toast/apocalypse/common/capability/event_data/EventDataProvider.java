package com.toast.apocalypse.common.capability.event_data;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import com.toast.apocalypse.common.capability.difficulty.DifficultyCapability;
import com.toast.apocalypse.common.capability.difficulty.IDifficultyCapability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EventDataProvider implements ICapabilitySerializable<CompoundTag> {

    public static final NonNullSupplier<IEventDataCapability> SUPPLIER = EventDataCapability::new;
    private final LazyOptional<IEventDataCapability> optional = LazyOptional.of(SUPPLIER);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ApocalypseCapabilities.EVENT_DATA_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return ApocalypseCapabilities.EVENT_DATA_CAPABILITY.orEmpty(ApocalypseCapabilities.EVENT_DATA_CAPABILITY, optional).orElse(SUPPLIER.get()).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ApocalypseCapabilities.EVENT_DATA_CAPABILITY.orEmpty(ApocalypseCapabilities.EVENT_DATA_CAPABILITY, optional).orElse(SUPPLIER.get()).deserializeNBT(nbt);
    }
}