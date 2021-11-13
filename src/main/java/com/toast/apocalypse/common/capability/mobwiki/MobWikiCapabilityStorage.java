package com.toast.apocalypse.common.capability.mobwiki;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class MobWikiCapabilityStorage implements Capability.IStorage<IMobWikiCapability> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IMobWikiCapability> capability, IMobWikiCapability instance, Direction side) {
        return instance.getEntries() == null ? new IntArrayNBT(new int[]{}) : new IntArrayNBT(instance.getEntries());
    }

    @Override
    public void readNBT(Capability<IMobWikiCapability> capability, IMobWikiCapability instance, Direction side, INBT nbt) {
        if (nbt.getType() != IntArrayNBT.TYPE) {
            Apocalypse.LOGGER.error("Failed to read mob wiki capability data! The parsed data must be of type IntArrayNBT");
            return;
        }
        instance.setEntries(((IntArrayNBT) nbt).getAsIntArray());
    }
}
