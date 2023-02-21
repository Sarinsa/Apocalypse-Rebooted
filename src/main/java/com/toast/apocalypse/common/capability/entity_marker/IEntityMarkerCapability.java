package com.toast.apocalypse.common.capability.entity_marker;

import net.minecraft.nbt.ByteTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IEntityMarkerCapability extends INBTSerializable<ByteTag> {

    void setMarked(boolean marked);

    boolean getMarked();
}
