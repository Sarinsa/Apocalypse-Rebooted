package com.toast.apocalypse.api.plugin;

import javax.annotation.Nullable;

/**
 * Allows other mods to read
 * Apocalypse's config settings.
 */
public interface IConfigHelper {

    enum ConfigType {
        COMMON,
        CLIENT,
        SERVER
    }

    /**
     * Helper method for accessing config values in
     * Apocalypse's configs. <br>
     * <br>
     *
     * @param configType The type of the config to read from.
     * @param key The config entry key of the value to fetch.<br>
     * <br>
     * @param <T> The value type of the value to fetch.<br>
     * <br>
     * @return The value of the specified config key in the specified
     *         config if it exists and null if not. If the inferred value type does not match
     *         that of the key's value, this will also return null.<br>
     * <br>
     */
    @Nullable
    <T> T getValue(ConfigType configType, String key);
}
