package com.toast.apocalypse.common.core.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.lang.reflect.Field;
import java.util.EnumMap;

public class ServerConfigHelper {

    /** Updated on client only as these
     *  values are only required for
     *  integrated servers.
     */
    public static double DESIRED_DEFAULT_MAX_DIFFICULTY = 200.0D;
    public static double DESIRED_DEFAULT_GRACE_PERIOD = 1.0D;

    /**
     * Writes the current Apocalypse world settings
     * to the integrated server's mod server config.
     */
    public static void updateModServerConfig() {
        String modid = Apocalypse.MODID;
        String configName = ConfigTracker.INSTANCE.getConfigFileName(modid, ModConfig.Type.SERVER);

        if (configName != null && !configName.isEmpty()) {
            ModContainer modContainer = ModList.get().getModContainerById(modid).orElseThrow(() -> new IllegalStateException("Failed to fetch ModContainer instance for modid " + modid));
            Field field = ObfuscationReflectionHelper.findField(ModContainer.class, "configs");

            try {
                EnumMap<ModConfig.Type, ModConfig> configMap;
                configMap = (EnumMap<ModConfig.Type, ModConfig>) field.get(modContainer);
                ModConfig config = configMap.getOrDefault(ModConfig.Type.SERVER, null);

                if (config != null) {
                    CommentedConfig commentedConfig = config.getConfigData();
                    commentedConfig.set("difficulty.defaultPlayerMaxDifficulty", DESIRED_DEFAULT_MAX_DIFFICULTY);
                    commentedConfig.set("difficulty.defaultPlayerGracePeriod", DESIRED_DEFAULT_GRACE_PERIOD);
                    config.save();
                }
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        // Reset values
        DESIRED_DEFAULT_MAX_DIFFICULTY = 200.0D;
        DESIRED_DEFAULT_GRACE_PERIOD = 1.0D;
    }

    /**
     * Called from {@link com.toast.apocalypse.client.screen.ApocalypseWorldCreateConfigScreen}
     */
    public static void updateModServerConfigValues(double maxDifficulty, double gracePeriod) {
        DESIRED_DEFAULT_MAX_DIFFICULTY = maxDifficulty;
        DESIRED_DEFAULT_GRACE_PERIOD = gracePeriod;
    }
}
