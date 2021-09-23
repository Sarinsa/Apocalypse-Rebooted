package com.toast.apocalypse.api.plugin;

/**
 * This is the main interface containing all
 * the methods for retrieving various helpers.
 */
public abstract class ApocalypseApi {

    protected ApocalypseApi() {

    }

    /**
     * @return The IRegistryHelper instance
     *         parsed by Apocalypse
     */
    public abstract IRegistryHelper getRegistryHelper();

    /**
     * @return The IDifficultyProvider instance
     *         parsed by Apocalypse.
     */
    public abstract IDifficultyProvider getDifficultyProvider();

    /**
     * @return The IConfigHelper instance
     *         parsed by Apocalypse.
     */
    public abstract IConfigHelper getConfigHelper();
}
