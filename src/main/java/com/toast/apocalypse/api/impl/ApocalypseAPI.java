package com.toast.apocalypse.api.impl;

import com.toast.apocalypse.api.plugin.ApocalypseApi;
import com.toast.apocalypse.api.plugin.IConfigHelper;
import com.toast.apocalypse.api.plugin.IDifficultyProvider;
import com.toast.apocalypse.api.plugin.IRegistryHelper;
import com.toast.apocalypse.common.core.Apocalypse;

public final class ApocalypseAPI extends ApocalypseApi {

    @Override
    public IRegistryHelper getRegistryHelper() {
        return Apocalypse.INSTANCE.getRegistryHelper();
    }

    @Override
    public IDifficultyProvider getDifficultyProvider() {
        return Apocalypse.INSTANCE.getDifficultyManager();
    }

    @Override
    public IConfigHelper getConfigHelper() {
        return Apocalypse.INSTANCE.getConfigHelper();
    }
}
