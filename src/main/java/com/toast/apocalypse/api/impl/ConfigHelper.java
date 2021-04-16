package com.toast.apocalypse.api.impl;

import com.toast.apocalypse.api.plugin.IConfigHelper;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;

import java.util.List;

/**
 * Helper class for interacting with the mod config
 * and setting values through code.
 */
public final class ConfigHelper implements IConfigHelper {

    public ConfigHelper() {

    }

    @Override
    public boolean rainDamageEnabled() {
        return ApocalypseCommonConfig.COMMON.rainDamageEnabled();
    }

    @Override
    public float rainDamageAmount() {
        return ApocalypseCommonConfig.COMMON.getRainDamage();
    }

    @Override
    public List<? extends String> penaltyDimensions() {
        return ApocalypseCommonConfig.COMMON.getDifficultyPenaltyDimensions();
    }
}
