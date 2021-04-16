package com.toast.apocalypse.api.plugin;

import com.toast.apocalypse.api.TimeChangedEvent;
import com.toast.apocalypse.common.core.Apocalypse;
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

    public static void onTimeChanged(long newTime) {
        MinecraftForge.EVENT_BUS.post(new TimeChangedEvent(newTime));
    }
}
