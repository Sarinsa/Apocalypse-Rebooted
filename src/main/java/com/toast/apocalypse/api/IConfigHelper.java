package com.toast.apocalypse.api;

/**
 * An easy way for modders to
 * check Apocalypse's config
 * settings.
 */
public interface IConfigHelper {

    /**
     * @return True if rain damage is
     *         enabled in the mod config.
     */
    boolean rainDamageEnabled();
}
