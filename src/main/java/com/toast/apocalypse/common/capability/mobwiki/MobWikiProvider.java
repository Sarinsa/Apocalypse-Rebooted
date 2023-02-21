package com.toast.apocalypse.common.capability.mobwiki;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class MobWikiProvider implements ICapabilitySerializable<CompoundTag> {

    public static final IMobWikiCapability INSTANCE = new MobWikiCapability();
    private final LazyOptional<IMobWikiCapability> optional = LazyOptional.of(() -> INSTANCE);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ApocalypseCapabilities.MOB_WIKI_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return ApocalypseCapabilities.MOB_WIKI_CAPABILITY.orEmpty(ApocalypseCapabilities.MOB_WIKI_CAPABILITY, optional).orElse(INSTANCE).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ApocalypseCapabilities.MOB_WIKI_CAPABILITY.orEmpty(ApocalypseCapabilities.MOB_WIKI_CAPABILITY, optional).orElse(INSTANCE).deserializeNBT(nbt);
    }
}
