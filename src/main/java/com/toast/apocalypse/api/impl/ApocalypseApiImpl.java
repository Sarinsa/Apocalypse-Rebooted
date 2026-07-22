package com.toast.apocalypse.api.impl;

import com.toast.apocalypse.api.plugin.IApocalypseApi;
import com.toast.apocalypse.api.plugin.IDifficultyAccessor;

public final class ApocalypseApiImpl implements IApocalypseApi {
    
    private final IDifficultyAccessor difficultyProvider;
    
    public ApocalypseApiImpl() {
        this.difficultyProvider = new DifficultyAccessorImpl();
    }
    
    @Override
    public IDifficultyAccessor getDifficultyProvider() {
        return this.difficultyProvider;
    }
}
