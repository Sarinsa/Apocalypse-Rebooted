package com.toast.apocalypse.client.event;

import com.toast.apocalypse.client.screen.ApocalypseWorldCreateConfigScreen;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseClientConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import static com.toast.apocalypse.common.core.config.ApocalypseClientConfig.PositionWidthAnchor;
import static com.toast.apocalypse.common.core.config.ApocalypseClientConfig.PositionHeightAnchor;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientConfigReloadListener {

    @SubscribeEvent
    public static void onConfigReload(ModConfig.Reloading event) {
        ModConfig.Type type = event.getConfig().getType();

        if (type == ModConfig.Type.CLIENT) {
            updateClientConfigInfo();
        }
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfig.Loading event) {
        ModConfig.Type type = event.getConfig().getType();

        if (type == ModConfig.Type.CLIENT) {
            updateClientConfigInfo();
        }
    }

    private static void updateClientConfigInfo() {
        DifficultyRenderHandler.RENDER_IN_CREATIVE = ApocalypseClientConfig.CLIENT.getRenderDifficultyInCreative();
        DifficultyRenderHandler.KEYBIND_ONLY = ApocalypseClientConfig.CLIENT.getKeybindOnly();
        PositionWidthAnchor renderPosWidth = ApocalypseClientConfig.CLIENT.getDifficultyRenderPosWidth();
        PositionHeightAnchor renderPosHeight = ApocalypseClientConfig.CLIENT.getDifficultyRenderPosHeight();
        int xOffset = ApocalypseClientConfig.CLIENT.getDifficultyRenderXOffset();
        int yOffset = ApocalypseClientConfig.CLIENT.getDifficultyRenderYOffset();

        DifficultyRenderHandler.updateRenderPos(renderPosWidth, renderPosHeight, xOffset, yOffset);

        ClientEvents.WIDTH = ApocalypseClientConfig.CLIENT.getWorldConfigButtonPosWidth();
        ClientEvents.HEIGHT = ApocalypseClientConfig.CLIENT.getWorldConfigButtonPosHeight();
        ClientEvents.X_OFFSET = ApocalypseClientConfig.CLIENT.getWorldConfigButtonXOffset();
        ClientEvents.Y_OFFSET = ApocalypseClientConfig.CLIENT.getWorldConfigButtonYOffset();
    }
}
