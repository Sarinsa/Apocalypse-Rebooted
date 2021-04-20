package com.toast.apocalypse.api.plugin;

import com.toast.apocalypse.api.TimeChangedEvent;
import net.minecraftforge.common.MinecraftForge;

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

    /**
     * Since Apocalypse increases difficulty over time,
     * any form of time skip is punished with additional
     * difficulty. This applies to sleeping in a vanilla setting.
     * If your mod skips time by any means other than sleeping
     * and you want it to affect Apocalypse's difficulty you can
     * fire this event which Apocalypse will listen for and dish out
     * a difficulty penalty relative to the amount of time skipped.
     *
     * @param newTime The amount of time skipped.
     */
    public static void onTimeChanged(long newTime) {
        MinecraftForge.EVENT_BUS.post(new TimeChangedEvent(newTime));
    }
}
