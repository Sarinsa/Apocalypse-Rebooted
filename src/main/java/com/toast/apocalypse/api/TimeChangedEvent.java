package com.toast.apocalypse.api;

import net.minecraftforge.eventbus.api.Event;

/**
 * This event can be fired to notify Apocalypse of
 * a change in world time in order to update difficulty
 * accordingly. Intended usage is for in-game events,
 * such as when players sleep (the time set command is excluded)
 */
public final class TimeChangedEvent extends Event {

    private long timeSkipped;

    public TimeChangedEvent(long timeSkipped) {
        this.timeSkipped = timeSkipped;
    }

    /**
     * @param timeSkipped The amount of time skipped.
     */
    public void setTimeSkipped(long timeSkipped) {
        this.timeSkipped = timeSkipped;
    }

    public long getTimeSkipped() {
        return this.timeSkipped;
    }
}
