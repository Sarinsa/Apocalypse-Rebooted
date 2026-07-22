package com.toast.apocalypse.api.plugin;

/**
 * This is the main interface of the Apocalypse API,
 * containing various utility methods, getters and accessors.
 */
public interface IApocalypseApi {
    
    /** @return The {@link IDifficultyAccessor} instance provided by Apocalypse. */
    IDifficultyAccessor getDifficultyProvider();
}
