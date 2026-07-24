package com.toast.apocalypse.api;

import com.toast.apocalypse.api.client.IDifficultyRenderHelper;

/**
 * This is the main interface of the Apocalypse API,
 * containing various utility methods, getters and accessors.
 */
public interface IApocalypseApi {
    
    /**
     * @return The {@link IDifficultyAccessor} instance provided by Apocalypse.
     * Use this to access player difficulty data.
     */
    IDifficultyAccessor getDifficultyProvider();
    
    /**
     * @return The {@link IDifficultyRenderHelper} instance provided by Apocalypse.
     * Use this to access the difficulty renderer's text formats and other properties.
     */
    IDifficultyRenderHelper getDifficultyRenderHelper();
}
