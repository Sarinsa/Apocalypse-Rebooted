package com.toast.apocalypse.common.capability.mobwiki;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IMobWikiCapability extends INBTSerializable<CompoundTag> {

    void addEntry(int entry);

    void setEntries(int[] entries);

    int[] getEntries();
}
