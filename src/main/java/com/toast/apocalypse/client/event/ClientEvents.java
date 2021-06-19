package com.toast.apocalypse.client.event;

import com.toast.apocalypse.client.screen.ApocalypseWorldCreateConfigScreen;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.util.References;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ConfigTracker;

public class ClientEvents {

    /** The location of the Apocalypse world config button icon */
    private static final ResourceLocation GRUMP_ICON = Apocalypse.resourceLoc("textures/screen/button/grump_icon.png");
    /** The Minecraft client instance. **/
    private final Minecraft minecraftClient;
    /** The desired max difficulty and grace period
     *  values to be used when creating a new world.
     *  These are refreshed when the world creation
     *  screen is opened.
     */
    private double desiredDefaultMaxDifficulty;
    private double desiredDefaultGracePeriod;

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
            this.desiredDefaultGracePeriod = 0;
            this.desiredDefaultMaxDifficulty = 0;

            CreateWorldScreen screen = (CreateWorldScreen) event.getGui();
            event.addWidget(new ImageButton(screen.width/ 2 + 165, 100, 20, 20, 0, 0, 1, GRUMP_ICON, 16, 16, (button) -> {
                this.minecraftClient.setScreen(new ApocalypseWorldCreateConfigScreen(screen));
            }));
        }
    }
}
