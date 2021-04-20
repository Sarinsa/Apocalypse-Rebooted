package com.toast.apocalypse.api.impl;

import com.google.common.collect.Lists;
import com.toast.apocalypse.api.plugin.IRegistryHelper;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Objects;

public final class RegistryHelper implements IRegistryHelper {

    /** Keeping this here to avoid writing the
     *  same annoying symbols over and over again.
     */
    private static final String PLUGIN_MESSAGE = "Mod plugin \"{}\" ";

    /** The ID of the plugin that is currently being loaded */
    private String currentPluginId = "no_plugin_id :(";


    /**
     * Called after all found mod
     * plugins have been loaded.
     */
    public void postSetup() {

    }

    /**
     * Set ever time a different plugin
     * is being loaded. Using this for
     * logging and debug.
     */
    public void setCurrentPluginId(String pluginId) {
        this.currentPluginId = pluginId;
    }

    /** Helper method for logging */
    private static void log(Level level, String message, Object... arguments) {
        Apocalypse.LOGGER.log(level,"[{}] " + message, Lists.asList(RegistryHelper.class.getSimpleName(), arguments));
    }
}
