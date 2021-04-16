package com.toast.apocalypse.api.plugin;

/**
 * This is the interface that your
 * plugin class must implement in
 * order to access the API.
 */
public interface IApocalypsePlugin {

    /**
     * Called by Apocalypse during {@link net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent}
     *
     * @param api The IApocalypseApi instance
     *            parsed by Apocalypse.
     */
    void load(final ApocalypseApi api);

    /**
     * @return A String representing the ID / name of this
     *         plugin. Mainly used for debug and logging.
     */
    String getPluginId();
}
