package com.toast.apocalypse.api;

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
}
