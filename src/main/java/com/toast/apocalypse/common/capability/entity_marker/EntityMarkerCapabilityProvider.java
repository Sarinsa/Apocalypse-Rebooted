package com.toast.apocalypse.common.capability.entity_marker;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("all")
public class EntityMarkerCapabilityProvider implements ICapabilitySerializable<ByteNBT> {

    private final IEntityMarkerCapability INSTANCE = ApocalypseCapabilities.ENTITY_MARKER_CAPABILITY.getDefaultInstance();
    private final LazyOptional<IEntityMarkerCapability> optional = LazyOptional.of(() -> INSTANCE);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ApocalypseCapabilities.ENTITY_MARKER_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public ByteNBT serializeNBT() {
        return (ByteNBT) ApocalypseCapabilities.ENTITY_MARKER_CAPABILITY.getStorage().writeNBT(ApocalypseCapabilities.ENTITY_MARKER_CAPABILITY, INSTANCE, null);
    }

    @Override
    public void deserializeNBT(ByteNBT nbt) {
        ApocalypseCapabilities.ENTITY_MARKER_CAPABILITY.getStorage().readNBT(ApocalypseCapabilities.ENTITY_MARKER_CAPABILITY, INSTANCE, null, nbt);
    }
}