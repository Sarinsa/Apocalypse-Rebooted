package com.toast.apocalypse.common.capability.event_data;

import net.minecraft.nbt.CompoundNBT;

public interface IEventDataCapability {

    CompoundNBT getEventData();

    void setEventData(CompoundNBT dataTag);
}
