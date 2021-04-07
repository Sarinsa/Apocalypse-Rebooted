package com.toast.apocalypse.api;

/**
 * A simple interface that lets modders fetch
 * the world difficulty and difficulty rate.
 */
public interface IDifficultyProvider {

    /**
     * @return The current world difficulty.
     */
    long getDifficulty();

    /**
     * @return The current world difficulty rate.
     */
    double getDifficultyRate();
}
