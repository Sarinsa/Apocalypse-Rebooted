package com.toast.apocalypse.common.capability.event_data;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IEventDataCapability extends INBTSerializable<CompoundTag> {

    CompoundTag getEventData();

    void setEventData(CompoundTag dataTag);
}
