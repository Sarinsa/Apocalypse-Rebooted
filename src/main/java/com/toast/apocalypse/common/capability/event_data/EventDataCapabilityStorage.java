package com.toast.apocalypse.common.capability.event_data;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class EventDataCapabilityStorage implements Capability.IStorage<IEventDataCapability> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IEventDataCapability> capability, IEventDataCapability instance, Direction side) {
        return instance.getEventData() == null ? new CompoundNBT() : instance.getEventData();
    }

    @Override
    public void readNBT(Capability<IEventDataCapability> capability, IEventDataCapability instance, Direction side, INBT nbt) {
        if (nbt.getType() != CompoundNBT.TYPE) {
            Apocalypse.LOGGER.error("Failed to read event capability data! The parsed data must be of type CompoundNBT");
            return;
        }
        instance.setEventData((CompoundNBT) nbt);
    }
}
