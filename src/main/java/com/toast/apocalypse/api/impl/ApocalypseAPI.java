package com.toast.apocalypse.api.impl;

import com.toast.apocalypse.api.plugin.ApocalypseApi;
import com.toast.apocalypse.api.plugin.IConfigHelper;
import com.toast.apocalypse.api.plugin.IDifficultyProvider;
import com.toast.apocalypse.api.plugin.IRegistryHelper;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.entity.player.ServerPlayerEntity;

public final class ApocalypseAPI extends ApocalypseApi {

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

    @Override
    public IConfigHelper getConfigHelper() {
        return Apocalypse.INSTANCE.getConfigHelper();
    }
}
