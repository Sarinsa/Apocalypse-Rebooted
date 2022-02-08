package com.toast.apocalypse.api.plugin;

/**
 * This is the main interface of the APi, containing all the<br>
 * methods for retrieving various helpers and whatnot.
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
}
