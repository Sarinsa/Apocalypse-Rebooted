package com.toast.apocalypse.client.event;

import com.toast.apocalypse.client.screen.ApocalypseWorldCreateConfigScreen;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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


    public ClientEvents(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    /**
     * Renders the difficulty seen in-game
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void afterRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        DifficultyRenderHandler.renderDifficulty(event, this.minecraft);
    }

    /**
     * Here we add our own button to the world creation screen
     * for setting the world's default max player difficulty
     * and grace period.
     */
    @SubscribeEvent
    public void onScreenOpened(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof CreateWorldScreen) {
            Screen screen = event.getGui();

            int x;
            int y;

            switch (WIDTH) {
                default:
                case LEFT:
                    x = 0;
                    break;
                case MIDDLE:
                    x = (screen.width / 2) - 10;
                    break;
                case RIGHT:
                    x = screen.width - 20;
                    break;
            }

            switch (HEIGHT) {
                default:
                case TOP:
                    y = 0;
                    break;
                case MIDDLE:
                    y = (screen.height / 2) - 10;
                    break;
                case BOTTOM:
                    y = screen.height - 20;
                    break;
            }
            x += X_OFFSET;
            y += Y_OFFSET;

            event.addWidget(new ImageButton(x, y, 20, 20, 0, 0, 20, GHOSTLY_ICON, 32, 64,
                    (button) -> this.minecraft.setScreen(new ApocalypseWorldCreateConfigScreen(screen))));
        }
    }
}
