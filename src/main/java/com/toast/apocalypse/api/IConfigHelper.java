package com.toast.apocalypse.api;

import java.util.List;

/**
 * An easy way for modders to
 * check some of Apocalypse's
 * config settings.
 */
public interface IConfigHelper {

    /**
     * @return True if rain damage is
     *         enabled in the mod config.
     */
    boolean rainDamageEnabled();

    /**
     * @return A list of Strings representing the registry
     *         names of the worlds that are configured to
     *         give an increased difficulty multiplier whenever
     *         a player is in any of these dimensions.
     */
    List<? extends String> penaltyDimensions();
}
