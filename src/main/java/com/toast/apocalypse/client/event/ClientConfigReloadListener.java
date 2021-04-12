package com.toast.apocalypse.client.event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseClientConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import static com.toast.apocalypse.common.core.config.ApocalypseClientConfig.DifficultyRenderPosHeight;
import static com.toast.apocalypse.common.core.config.ApocalypseClientConfig.DifficultyRenderPosWidth;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientConfigReloadListener {

    @SubscribeEvent
    public static void onConfigReload(ModConfig.Reloading event) {
        ModConfig.Type type = event.getConfig().getType();

        if (type == ModConfig.Type.CLIENT) {
            updateDifficultyRenderInfo();
        }
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfig.Loading event) {
        ModConfig.Type type = event.getConfig().getType();

        if (type == ModConfig.Type.CLIENT) {
            updateDifficultyRenderInfo();
        }
    }

    private static void updateDifficultyRenderInfo() {
        DifficultyRenderPosWidth renderPosWidth = ApocalypseClientConfig.CLIENT.getDifficultyRenderPosWidth();
        DifficultyRenderPosHeight renderPosHeight = ApocalypseClientConfig.CLIENT.getDifficultyRenderPosHeight();
        int xOffset = ApocalypseClientConfig.CLIENT.getDifficultyRenderXOffset();
        int yOffset = ApocalypseClientConfig.CLIENT.getDifficultyRenderYOffset();

        ClientEvents.updateInfo(renderPosWidth, renderPosHeight, xOffset, yOffset);
    }
}
