package com.toast.apocalypse.api.impl;

/**
 * Stores information about full moon mobs
 * like their spawn frequency and if they should
 * be forced to not despawn during full moons.
 */
public final class FullMoonMobInfo {

    private final boolean persistent;
    private int spawnChance;

    public FullMoonMobInfo(int spawnChance, boolean persistent) {
        this.spawnChance = spawnChance;
        this.persistent = persistent;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    public int getSpawnChance() {
        return this.spawnChance;
    }

    public void setSpawnChance(int chance) {
        this.spawnChance = chance;
    }
}
