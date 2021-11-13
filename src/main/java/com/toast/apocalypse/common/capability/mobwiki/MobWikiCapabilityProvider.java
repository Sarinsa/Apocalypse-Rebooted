package com.toast.apocalypse.common.capability.mobwiki;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import com.toast.apocalypse.common.capability.event_data.IEventDataCapability;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MobWikiCapabilityProvider implements ICapabilitySerializable<IntArrayNBT> {

    private final IMobWikiCapability INSTANCE = ApocalypseCapabilities.MOB_WIKI_CAPABILITY.getDefaultInstance();
    private final LazyOptional<IMobWikiCapability> optional = LazyOptional.of(() -> INSTANCE);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ApocalypseCapabilities.MOB_WIKI_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public IntArrayNBT serializeNBT() {
        return (IntArrayNBT) ApocalypseCapabilities.MOB_WIKI_CAPABILITY.getStorage().writeNBT(ApocalypseCapabilities.MOB_WIKI_CAPABILITY, INSTANCE, null);
    }

    @Override
    public void deserializeNBT(IntArrayNBT nbt) {
        ApocalypseCapabilities.MOB_WIKI_CAPABILITY.getStorage().readNBT(ApocalypseCapabilities.MOB_WIKI_CAPABILITY, INSTANCE, null, nbt);
    }
}
