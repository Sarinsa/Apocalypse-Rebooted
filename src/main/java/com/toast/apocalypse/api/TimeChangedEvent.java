package com.toast.apocalypse.api;

import net.minecraftforge.eventbus.api.Event;

public final class TimeChangedEvent extends Event {

    private long newTime;

    public TimeChangedEvent(long newTime) {
        this.newTime = newTime;
    }

    public void setNewTime(long newTime) {
        this.newTime = newTime;
    }

    public long getNewTime() {
        return this.newTime;
    }
}
