package com.toast.apocalypse.common.capability.rain_tick;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class RainTickCapabilityStorage implements Capability.IStorage<IRainTickCapability> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<IRainTickCapability> capability, IRainTickCapability instance, Direction side) {
        return IntNBT.valueOf(instance.getRainTicks());
    }

    @Override
    public void readNBT(Capability<IRainTickCapability> capability, IRainTickCapability instance, Direction side, INBT nbt) {
        if (nbt.getType() != IntNBT.TYPE) {
            Apocalypse.LOGGER.error("Failed to read rain tick capability data! The parsed data must be of type IntNBT");
            return;
        }
        instance.setTicks(((IntNBT)nbt).getAsInt());
    }
}
