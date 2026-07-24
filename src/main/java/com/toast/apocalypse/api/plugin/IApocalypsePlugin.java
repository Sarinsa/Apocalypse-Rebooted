package com.toast.apocalypse.api.plugin;

import com.toast.apocalypse.api.IApocalypseApi;

/**
 * This is the main interface that Apocalypse plugins must implement to access the API.
 */
public interface IApocalypsePlugin {
    
    /**
     * Called by Apocalypse during {@link net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent}.
     *
     * @param api The Apocalypse API instance.
     */
    void load( final IApocalypseApi api );
    
    /** @return This plugins ID. Mainly used for debug and logging purposes. */
    String getPluginId();
}
