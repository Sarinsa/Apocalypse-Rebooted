package com.toast.apocalypse.api.impl;

import com.google.common.collect.Lists;
import com.toast.apocalypse.api.register.IRegistryHelper;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Objects;

public final class RegistryHelper implements IRegistryHelper {

    /** A map containing info about full moon mobs */
    private static final HashMap<EntityType<? extends LivingEntity>, FullMoonMobInfo> FULL_MOON_MOB_INFO = new HashMap<>();

    /** Keeping this here to avoid writing the
     *  same annoying symbols over and over again.
     */
    private static final String PLUGIN_MESSAGE = "Mod plugin \"{}\" ";

    /** The ID of the plugin that is currently being loaded */
    private String currentPluginId = "no_plugin_id :(";

    public RegistryHelper() {

    }

    /**
     * Registering our own stuff here.
     * Called during {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}
     * from our mod class.
     */
    public void registerInternal() {

    }

    /**
     * Called after all found mod
     * plugins have been loaded.
     */
    public void postSetup() {

    }

    @Override
    public <T extends LivingEntity> void registerFullMoonMob(EntityType<T> entityType, int baseSpawnChance, boolean persistent) {
        Objects.requireNonNull(entityType);

        if (baseSpawnChance < 0) {
            log(Level.WARN, PLUGIN_MESSAGE + "attempted to register a full moon mob with negative spawn chance. It will not be registered.", this.currentPluginId);
            return;
        }

        if (FULL_MOON_MOB_INFO.containsKey(entityType)) {
            String registryName = entityType.getRegistryName() == null ? "missingno" : entityType.getRegistryName().toString();
            log(Level.WARN, PLUGIN_MESSAGE + "attempted to register a full moon mob with an entity type that has already been registered! Type: {}", this.currentPluginId, registryName);
        }
        else {
            FULL_MOON_MOB_INFO.put(entityType, new FullMoonMobInfo(baseSpawnChance, persistent));
        }
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
    private static void log(Level loggingLevel, String message, Object... arguments) {
        Apocalypse.LOGGER.log(loggingLevel,"[{}] " + message, Lists.asList(RegistryHelper.class.getSimpleName(), arguments));
    }
}
