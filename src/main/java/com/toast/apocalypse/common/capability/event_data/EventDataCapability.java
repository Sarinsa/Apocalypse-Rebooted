package com.toast.apocalypse.common.capability.event_data;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class EventDataCapability implements IEventDataCapability {

    private CompoundTag dataTag;

    @Override
    public CompoundTag getEventData() {
        return this.dataTag;
    }

    @Override
    public void setEventData(CompoundTag dataTag) {
        this.dataTag = dataTag;
    }

    @Override
    public CompoundTag serializeNBT() {
        return dataTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        dataTag = nbt;
    }
}
