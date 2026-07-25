package com.toast.apocalypse.api;

import com.toast.apocalypse.api.client.IDifficultyOverlayHelper;

/**
 * This is the main interface of the Apocalypse API,
 * containing various utility methods, getters and accessors.
 */
public interface IApocalypseApi {
    
    /** Apocalypse's mod ID string. */
    String MOD_ID = "apocalypse";
    
    
    /**
     * @return The {@link IDifficultyAccessor} instance provided by Apocalypse.
     * Use this to access player difficulty data.
     */
    IDifficultyAccessor getDifficultyProvider();
    
    /**
     * @return The {@link IDifficultyOverlayHelper} instance provided by Apocalypse.
     * Use this to access the difficulty GUI overlay's text formats and other properties.
     * @throws IllegalStateException If called on a dedicated server.
     */
    IDifficultyOverlayHelper getDifficultyOverlayHelper();
}
