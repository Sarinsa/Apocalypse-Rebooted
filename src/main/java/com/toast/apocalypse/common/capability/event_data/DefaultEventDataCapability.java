package com.toast.apocalypse.common.capability.event_data;

import net.minecraft.nbt.CompoundNBT;

public class DefaultEventDataCapability implements IEventDataCapability {

    private CompoundNBT dataTag;

    @Override
    public CompoundNBT getEventData() {
        return this.dataTag;
    }

    @Override
    public void setEventData(CompoundNBT dataTag) {
        this.dataTag = dataTag;
    }
}
