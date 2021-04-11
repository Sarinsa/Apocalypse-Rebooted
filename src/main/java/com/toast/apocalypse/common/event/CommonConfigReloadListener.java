package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.WorldDifficultyManager;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.util.RainDamageTickHelper;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Mod.EventBusSubscriber(modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfigReloadListener {

    @SubscribeEvent
    public static void onLoad(ModConfig.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            parseCommonModConfig();
            updateInfo();
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfig.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            updateInfo();
        }
    }

    /**
     * Updates static references when needed to
     * avoid accessing the config constantly
     * in the server tick loop and whatnot.
     */
    public static void updateInfo() {
        WorldDifficultyManager.MAX_DIFFICULTY = ApocalypseCommonConfig.COMMON.getMaxDifficulty();
        WorldDifficultyManager.MULTIPLAYER_DIFFICULTY_SCALING = ApocalypseCommonConfig.COMMON.multiplayerDifficultyScaling();
        WorldDifficultyManager.DIFFICULTY_MULTIPLIER = ApocalypseCommonConfig.COMMON.getDifficultyRateMultiplier();
        WorldDifficultyManager.SLEEP_PENALTY = ApocalypseCommonConfig.COMMON.getSleepPenalty();

        List<RegistryKey<World>> list = new ArrayList<>();
        ApocalypseCommonConfig.COMMON.getDifficultyPenaltyDimensions().forEach((s -> {
            list.add(RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(s)));
        }));
        WorldDifficultyManager.DIMENSION_PENALTY_LIST = list;

        WorldDifficultyManager.DIMENSION_PENALTY = ApocalypseCommonConfig.COMMON.getDimensionPenalty();

        RainDamageTickHelper.RAIN_TICK_RATE = ApocalypseCommonConfig.COMMON.getRainTickRate();
        RainDamageTickHelper.RAIN_DAMAGE = ApocalypseCommonConfig.COMMON.getRainDamage();
    }

    /**
     * Parses the common ModConfig for Apocalypse
     * to the Config Helper when the config is loaded.
     */
    @SuppressWarnings("all")
    private static void parseCommonModConfig() {
        String modid = Apocalypse.MODID;
        String configName = ConfigTracker.INSTANCE.getConfigFileName(modid, ModConfig.Type.COMMON);

        if (configName != null && !configName.isEmpty()) {
            ModContainer modContainer = ModList.get().getModContainerById(modid).orElseThrow(() -> new RuntimeException("Failed to fetch ModContainer instance for " + modid));
            Field field = ObfuscationReflectionHelper.findField(ModContainer.class, "configs");

            try {
                EnumMap<ModConfig.Type, ModConfig> configMap;
                configMap = (EnumMap<ModConfig.Type, ModConfig>) field.get(modContainer);
                Apocalypse.INSTANCE.getConfigHelper().setModConfig(configMap.getOrDefault(ModConfig.Type.COMMON, null));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Apocalypse.LOGGER.error("Failed to fetch ModConfig! The config helper will not work properly.");
        }
    }
}
