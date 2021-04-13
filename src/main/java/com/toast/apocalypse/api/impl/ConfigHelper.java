package com.toast.apocalypse.api.impl;

import com.toast.apocalypse.api.IConfigHelper;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.EnumMap;
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
    public List<? extends String> penaltyDimensions() {
        return ApocalypseCommonConfig.COMMON.getDifficultyPenaltyDimensions();
    }
}
