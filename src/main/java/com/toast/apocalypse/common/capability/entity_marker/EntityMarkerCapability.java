package com.toast.apocalypse.common.capability.entity_marker;

import net.minecraft.nbt.ByteTag;

public class EntityMarkerCapability implements IEntityMarkerCapability {

    private boolean isMarked = false;

    @Override
    public void setMarked(boolean marked) {
        this.isMarked = marked;
    }

    @Override
    public boolean getMarked() {
        return this.isMarked;
    }

    @Override
    public ByteTag serializeNBT() {
        return ByteTag.valueOf(isMarked);
    }

    @Override
    public void deserializeNBT(ByteTag nbt) {
        isMarked = nbt.getAsByte() > 0;
    }
}
