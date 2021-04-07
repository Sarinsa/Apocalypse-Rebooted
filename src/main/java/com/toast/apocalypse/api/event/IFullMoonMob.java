package com.toast.apocalypse.api.event;

/**
 * This represents a type of monster that will spawn as part of a full moon event.
 */
public interface IFullMoonMob {

    /**
     * Determines whether or not a full moon
     * monster should be prevented from
     * despawning during full moons.
     *
     * @return True if this entity should not despawn.
     */
    default boolean persistentDuringFullMoon() {
        return true;
    }

    /**
     * The default chance for this
     * full moon mob to spawn during
     * a full moon.
     *
     * This value will later be
     * inserted into the mod's config
     * where it can later be tweaked
     * by the user if needed be.
     */
    int defaultSpawnChance();
}
