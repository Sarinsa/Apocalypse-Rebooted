package com.toast.apocalypse.common.capability.rain_tick;

public interface IRainTickCapability {

    /** Returns the player's current amount of rain ticks **/
    int getRainTicks();

    /** Adds 1 to the rain tick count **/
    void addTick();

    /** Sets the total amount of rain ticks **/
    void setTicks(int ticks);

    /** Clears the player's rain ticks **/
    void clearTicks();
}
