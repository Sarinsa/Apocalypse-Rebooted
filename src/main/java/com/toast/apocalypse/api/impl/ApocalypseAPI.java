package com.toast.apocalypse.api.impl;

import com.toast.apocalypse.api.IApocalypseApi;
import com.toast.apocalypse.api.IConfigHelper;
import com.toast.apocalypse.api.IDifficultyProvider;
import com.toast.apocalypse.api.register.IRegistryHelper;
import com.toast.apocalypse.common.core.Apocalypse;

public final class ApocalypseAPI implements IApocalypseApi {

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
