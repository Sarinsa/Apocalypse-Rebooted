package com.toast.apocalypse.common.core.config.util;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.network.message.S2CSimpleClientTask;
import fathertoast.crust.api.config.common.field.BooleanField;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.function.Consumer;

/**
 * Weird and hacky helper for modifying server/world config
 * before world creation on clients.
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Apocalypse.MODID, value = Dist.DEDICATED_SERVER)
public class ServerConfigHelper {

    /**
     * Updated on client only as these
     * values are only required for
     * integrated servers.
     */
    public static double DESIRED_DEFAULT_MAX_DIFFICULTY;
    public static double DESIRED_DEFAULT_GRACE_PERIOD;

    static {
        resetValues();
    }

    private static void resetValues() {
        DESIRED_DEFAULT_MAX_DIFFICULTY = 150.0D;
        DESIRED_DEFAULT_GRACE_PERIOD = 1.0D;
    }

    /**
     * Writes the current Apocalypse world settings
     * to the integrated server's mod server config.
     */
    @SuppressWarnings("unchecked")
    public static void updateModServerConfig() {
        final String modid = Apocalypse.MODID;
        final String configName = ConfigTracker.INSTANCE.getConfigFileName(modid, ModConfig.Type.SERVER);

        if (configName != null && !configName.isEmpty()) {
            ModContainer modContainer = ModList.get().getModContainerById(modid).orElseThrow(() -> new IllegalStateException("Failed to fetch ModContainer instance for " + modid + ". The server config will not be updated."));
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
        resetValues();
    }

    /**
     * Called from {@link com.toast.apocalypse.client.screen.misc.ApocalypseWCTab}
     */
    public static void updateModServerConfigValues(double maxDifficulty, double gracePeriod) {
        DESIRED_DEFAULT_MAX_DIFFICULTY = maxDifficulty;
        DESIRED_DEFAULT_GRACE_PERIOD = gracePeriod;
    }
}
