package com.toast.apocalypse.client.event;

import com.toast.apocalypse.client.screen.ApocalypseWorldCreateConfigScreen;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
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

    /**
     * Here we add our own button to the world creation screen
     * for setting the world's default max player difficulty
     * and grace period.
     */
    @SubscribeEvent
    public void onScreenOpened(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof CreateWorldScreen) {
            Screen screen = event.getScreen();

            int x;
            int y;

            x = switch (WIDTH) {
                case LEFT -> 0;
                case MIDDLE -> (screen.width / 2) - 10;
                case RIGHT -> screen.width - 20;
            };

            y = switch (HEIGHT) {
                case TOP -> 0;
                case MIDDLE -> (screen.height / 2) - 10;
                case BOTTOM -> screen.height - 20;
            };
            x += X_OFFSET;
            y += Y_OFFSET;

            event.addListener(new ImageButton(x, y, 20, 20, 0, 0, 20, GHOSTLY_ICON, 32, 64,
                    (button) -> this.minecraft.setScreen(new ApocalypseWorldCreateConfigScreen(screen))));
        }
    }
}
