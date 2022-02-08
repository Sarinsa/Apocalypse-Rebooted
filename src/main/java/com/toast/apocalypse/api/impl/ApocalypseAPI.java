package com.toast.apocalypse.api.impl;

import com.toast.apocalypse.api.plugin.IApocalypseApi;
import com.toast.apocalypse.api.plugin.IDifficultyProvider;
import com.toast.apocalypse.api.plugin.IRegistryHelper;
import com.toast.apocalypse.common.core.Apocalypse;

public final class ApocalypseAPI implements IApocalypseApi {

    private final IDifficultyProvider difficultyProvider;

    public ApocalypseAPI() {
        this.difficultyProvider = new DifficultyProvider();
    }

    @Override
    public IRegistryHelper getRegistryHelper() {
        return Apocalypse.INSTANCE.getRegistryHelper();
    }

    @Override
    public IDifficultyProvider getDifficultyProvider() {
        return this.difficultyProvider;
    }
}
