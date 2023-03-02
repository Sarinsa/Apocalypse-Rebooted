package com.toast.apocalypse.common.capability.difficulty;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class DifficultyProvider implements ICapabilitySerializable<CompoundTag> {

    public static final NonNullSupplier<IDifficultyCapability> SUPPLIER = DifficultyCapability::new;
    private final LazyOptional<IDifficultyCapability> optional = LazyOptional.of(SUPPLIER);


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ApocalypseCapabilities.DIFFICULTY_CAPABILITY.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        return ApocalypseCapabilities.DIFFICULTY_CAPABILITY.orEmpty(ApocalypseCapabilities.DIFFICULTY_CAPABILITY, optional).orElse(SUPPLIER.get()).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ApocalypseCapabilities.DIFFICULTY_CAPABILITY.orEmpty(ApocalypseCapabilities.DIFFICULTY_CAPABILITY, optional).orElse(SUPPLIER.get()).deserializeNBT(nbt);
    }
}
