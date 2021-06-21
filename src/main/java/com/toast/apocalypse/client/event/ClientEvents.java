package com.toast.apocalypse.client.event;

import com.toast.apocalypse.client.screen.ApocalypseWorldCreateConfigScreen;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.util.References;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ConfigTracker;

public class ClientEvents {

    /** The location of the Apocalypse world config button icon */
    private static final ResourceLocation GRUMP_ICON = Apocalypse.resourceLoc("textures/screen/button/ghostly.png");
    /** The Minecraft client instance. **/
    private final Minecraft minecraftClient;

    public ClientEvents(Minecraft minecraft) {
        this.minecraftClient = minecraft;
    }

    /**
     * Renders the difficulty seen in-game
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void afterRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        DifficultyRenderHandler.renderDifficulty(event, this.minecraftClient);
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
            event.addWidget(new ImageButton(screen.width/ 2 + 165, 100, 20, 20, 0, 0, 20, GRUMP_ICON, 32, 64, (button) -> {
                this.minecraftClient.setScreen(new ApocalypseWorldCreateConfigScreen(screen));
            }));
        }
    }
}
