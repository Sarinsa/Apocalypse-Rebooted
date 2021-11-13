package com.toast.apocalypse.api.plugin;

/**
 * This is the main interface containing all
 * the methods for retrieving various helpers.
 */
public interface IApocalypseApi {

    /**
     * @return The IRegistryHelper instance
     *         parsed by Apocalypse
     */
    IRegistryHelper getRegistryHelper();

    /**
     * @return The IDifficultyProvider instance
     *         parsed by Apocalypse.
     */
    IDifficultyProvider getDifficultyProvider();

    /**
     * @return The IConfigHelper instance
     *         parsed by Apocalypse.
     */
    IConfigHelper getConfigHelper();
}
