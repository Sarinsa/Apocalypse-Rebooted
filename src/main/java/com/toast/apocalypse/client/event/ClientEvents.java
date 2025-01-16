package com.toast.apocalypse.client.event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEvents {

    public static ApocalypseClientConfig.PositionWidthAnchor WIDTH;
    public static ApocalypseClientConfig.PositionHeightAnchor HEIGHT;
    public static int X_OFFSET;
    public static int Y_OFFSET;

    /** The location of the Apocalypse world config button icon */
    private static final ResourceLocation GHOSTLY_ICON = Apocalypse.resourceLoc("textures/gui/button/ghostly.png");
    /** The Minecraft client instance. **/
    private final Minecraft minecraft;


    public ClientEvents() {
        minecraft = Minecraft.getInstance();
    }

    /**
     * Renders the difficulty seen in-game
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void afterRenderGameOverlay(RenderGuiOverlayEvent.Post event) {
        DifficultyRenderHandler.renderDifficulty(event, this.minecraft);
    }

    @SubscribeEvent
    public void onScreenOpened(ScreenEvent.Init.Post event) {

    }
}
