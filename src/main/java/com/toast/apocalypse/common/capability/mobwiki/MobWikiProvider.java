package com.toast.apocalypse.common.capability.mobwiki;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import com.toast.apocalypse.common.capability.event_data.EventDataCapability;
import com.toast.apocalypse.common.capability.event_data.IEventDataCapability;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class MobWikiProvider implements ICapabilitySerializable<CompoundTag> {

    public static final NonNullSupplier<IMobWikiCapability> SUPPLIER = MobWikiCapability::new;
    private final LazyOptional<IMobWikiCapability> optional = LazyOptional.of(SUPPLIER);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ApocalypseCapabilities.MOB_WIKI_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return ApocalypseCapabilities.MOB_WIKI_CAPABILITY.orEmpty(ApocalypseCapabilities.MOB_WIKI_CAPABILITY, optional).orElse(SUPPLIER.get()).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ApocalypseCapabilities.MOB_WIKI_CAPABILITY.orEmpty(ApocalypseCapabilities.MOB_WIKI_CAPABILITY, optional).orElse(SUPPLIER.get()).deserializeNBT(nbt);
    }
}
