package com.toast.apocalypse.common.capability.difficulty;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("all")
public class DifficultyCapabilityProvider implements ICapabilitySerializable<LongNBT> {

    private final IDifficultyCapability INSTANCE = ApocalypseCapabilities.DIFFICULTY_CAPABILITY.getDefaultInstance();
    private final LazyOptional<IDifficultyCapability> optional = LazyOptional.of(() -> INSTANCE);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ApocalypseCapabilities.DIFFICULTY_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public LongNBT serializeNBT() {
        return (LongNBT) ApocalypseCapabilities.DIFFICULTY_CAPABILITY.getStorage().writeNBT(ApocalypseCapabilities.DIFFICULTY_CAPABILITY, INSTANCE, null);
    }

    @Override
    public void deserializeNBT(LongNBT nbt) {
        ApocalypseCapabilities.DIFFICULTY_CAPABILITY.getStorage().readNBT(ApocalypseCapabilities.DIFFICULTY_CAPABILITY, INSTANCE, null, nbt);
    }
}
