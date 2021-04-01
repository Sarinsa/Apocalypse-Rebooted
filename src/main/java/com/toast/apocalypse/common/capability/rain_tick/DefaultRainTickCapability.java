package com.toast.apocalypse.common.capability.rain_tick;

public class DefaultRainTickCapability implements IRainTickCapability {

    private int ticks;

    @Override
    public int getRainTicks() {
        return this.ticks;
    }

    @Override
    public void addTick() {
        ++this.ticks;
    }

    @Override
    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public void clearTicks() {
        this.ticks = 0;
    }
}
