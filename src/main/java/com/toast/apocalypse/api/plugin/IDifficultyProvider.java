package com.toast.apocalypse.api.plugin;

/**
 * A simple interface that lets modders check
 * Apocalypse's current difficulty rate and
 * active event.
 */
public interface IDifficultyProvider {

    /**
     * @return The current world difficulty rate.
     */
    double getDifficultyRate();

    /**
     * @return The ID of the currently running
     *         event, if any.
     *
     * No event: -1
     *
     * Full moon: 0
     *
     * Thunderstorm: 1
     */
    int currentEventId();
}
