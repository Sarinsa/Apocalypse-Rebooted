package com.toast.apocalypse.common.capability.entity_marker;

public class DefaultEntityMarkerCapability implements IEntityMarkerCapability {

    private boolean isMarked = false;

    @Override
    public void setMarked(boolean marked) {
        this.isMarked = marked;
    }

    @Override
    public boolean getMarked() {
        return this.isMarked;
    }
}
