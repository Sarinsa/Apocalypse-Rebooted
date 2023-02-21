package com.toast.apocalypse.common.capability.entity_marker;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ByteTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityMarkerProvider implements ICapabilitySerializable<ByteTag> {

    public static final IEntityMarkerCapability INSTANCE = new EntityMarkerCapability();
    private final LazyOptional<IEntityMarkerCapability> optional = LazyOptional.of(() -> INSTANCE);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ApocalypseCapabilities.ENTITY_MARKER_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public ByteTag serializeNBT() {
        return ApocalypseCapabilities.ENTITY_MARKER_CAPABILITY.orEmpty(ApocalypseCapabilities.ENTITY_MARKER_CAPABILITY, optional).orElse(INSTANCE).serializeNBT();
    }

    @Override
    public void deserializeNBT(ByteTag nbt) {
        ApocalypseCapabilities.ENTITY_MARKER_CAPABILITY.orEmpty(ApocalypseCapabilities.ENTITY_MARKER_CAPABILITY, optional).orElse(INSTANCE).deserializeNBT(nbt);
    }
}