package com.toast.apocalypse.api.impl;

import com.toast.apocalypse.api.IApocalypseApi;
import com.toast.apocalypse.api.IDifficultyAccessor;
import com.toast.apocalypse.api.client.IDifficultyRenderHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * This is the main interface of the Apocalypse API,
 * containing various utility methods, getters and accessors.
 */
public final class ApocalypseApiImpl implements IApocalypseApi {
    
    // Common helpers and utilities
    private final IDifficultyAccessor difficultyProvider;
    
    // Client-only helpers and utilities
    private IDifficultyRenderHelper difficultyRenderHelper;
    
    
    public ApocalypseApiImpl() {
        this.difficultyProvider = new DifficultyAccessorImpl();
        
        // Only instantiate client helpers on the client
        if( FMLEnvironment.dist == Dist.CLIENT ) {
            difficultyRenderHelper = new DifficultyRenderHelperImpl();
        }
    }
    
    /**
     * @return The {@link IDifficultyAccessor} instance provided by Apocalypse.
     * Use this to access player difficulty data.
     */
    @Override
    public IDifficultyAccessor getDifficultyProvider() {
        return this.difficultyProvider;
    }
    
    /**
     * @return The {@link IDifficultyRenderHelper} instance provided by Apocalypse.
     * Use this to access the difficulty renderer's text formats and other properties.
     */
    @Override
    public IDifficultyRenderHelper getDifficultyRenderHelper() {
        if( !FMLEnvironment.dist.isClient() )
            throw new IllegalStateException( "Client-side helpers cannot be accessed in a dedicated server environment!" );
        return difficultyRenderHelper;
    }
}
