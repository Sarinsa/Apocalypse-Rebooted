package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.WorldDifficultyManager;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonConfigReloadListener {

    @SubscribeEvent
    public static void onLoad(ModConfig.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            updateInfo();
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfig.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            updateInfo();
        }
    }

    public static void updateInfo() {
        WorldDifficultyManager.MULTIPLAYER_DIFFICULTY_SCALING = ApocalypseCommonConfig.COMMON.multiplayerDifficultyScaling();
        WorldDifficultyManager.DIFFICULTY_MULTIPLIER = ApocalypseCommonConfig.COMMON.getDifficultyRateMultiplier();
        WorldDifficultyManager.SLEEP_PENALTY = ApocalypseCommonConfig.COMMON.getSleepPenalty();

        List<ResourceLocation> list = new ArrayList<>();
        ApocalypseCommonConfig.COMMON.getDifficultyPenaltyDimensions().forEach((s -> list.add(new ResourceLocation(s))));
        WorldDifficultyManager.DIMENSION_PENALTY_LIST = list;

        WorldDifficultyManager.DIMENSION_PENALTY = ApocalypseCommonConfig.COMMON.getDimensionPenalty();
    }
}
