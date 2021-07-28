package com.toast.apocalypse.common.capability.entity_marker;

import com.toast.apocalypse.common.capability.rain_tick.IRainTickCapability;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class EntityMarkerCapabilityStorage implements Capability.IStorage<IEntityMarkerCapability> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IEntityMarkerCapability> capability, IEntityMarkerCapability instance, Direction side) {
        return ByteNBT.valueOf(instance.getMarked());
    }

    @Override
    public void readNBT(Capability<IEntityMarkerCapability> capability, IEntityMarkerCapability instance, Direction side, INBT nbt) {
        if (nbt.getType() != ByteNBT.TYPE) {
            Apocalypse.LOGGER.error("Failed to read entity marker capability data! The parsed data must be of type ByteNBT");
            return;
        }
        instance.setMarked(((ByteNBT)nbt).getAsByte() == 1);
    }
}
