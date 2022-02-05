package com.toast.apocalypse.api.impl;

import com.toast.apocalypse.api.plugin.IConfigHelper;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseClientConfig;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.core.config.ApocalypseServerConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.config.ConfigTracker;

import java.util.List;

/**
 * Helper class for interacting with the mod config
 * and setting values through code.
 */
public final class ConfigHelper implements IConfigHelper {

    public ConfigHelper() {

    }

    @Override
    public <T> T getValue(ConfigType configType, String key) {
        ForgeConfigSpec configSpec;

        if (configType == null || key == null)
            return null;

        switch (configType) {
            default:
            case COMMON:
                try {
                    configSpec = ApocalypseCommonConfig.COMMON_SPEC;
                }
                catch (Exception e) {
                    Apocalypse.LOGGER.error("Failed to fetch config spec for common config.");
                    return null;
                }
                break;
            case CLIENT:
                try {
                    configSpec = ApocalypseClientConfig.CLIENT_SPEC;
                }
                catch (Exception e) {
                    Apocalypse.LOGGER.error("Failed to fetch config spec for client config.");
                    return null;
                }
                break;
            case SERVER:
                try {
                    configSpec = ApocalypseServerConfig.SERVER_SPEC;
                }
                catch (Exception e) {
                    Apocalypse.LOGGER.error("Failed to fetch config spec for server config.");
                    return null;
                }
                break;
        }
        try {
            if (configSpec.contains(key)) {
                return configSpec.get(key);
            }
            else {
                Apocalypse.LOGGER.error("Mod config for \"{}\" does not contain the key \"{}\"", configType.name(), key);
                return null;
            }
        }
        catch (Exception e) {
            Apocalypse.LOGGER.error("Failed to fetch config value for key \"{}\" in \"{}\" config. Could the expected value be different from what was given?", key, configType.name());
            return null;
        }
    }
}
