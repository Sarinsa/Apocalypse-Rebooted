package com.toast.apocalypse.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.util.References;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.toast.apocalypse.common.core.config.ApocalypseClientConfig.DifficultyRenderPosHeight;
import static com.toast.apocalypse.common.core.config.ApocalypseClientConfig.DifficultyRenderPosWidth;

public class ClientEvents {

    /** The color sequence. */
    public static final int[] COLORS = {
            0xFFFFFF, 0x88FFFF, 0x88FF88, 0xFFFF88, 0xFFBB88, 0xFF8888
    };

    // Rendering properties for quick access.
    public static long DIFFICULTY = 0;
    public static double DIFFICULTY_RATE = 0.0D;
    public static long COLOR_CHANGE;
    public static int POSITION_X;
    public static int POSITION_Y;
    public static int OFFSET_X;
    public static int OFFSET_Y;

    /** The Minecraft client instance. **/
    private final Minecraft minecraftClient;

    public ClientEvents(Minecraft minecraft) {
        this.minecraftClient = minecraft;
    }

    /**
     * Updates the render info when rendering the world difficulty in-game.
     * Called from {@link ClientConfigReloadListener} when the client config is loaded/reloaded */
    public static void updateInfo(DifficultyRenderPosWidth widthPos, DifficultyRenderPosHeight heightPos, int xOffset, int yOffset, long colorChange) {
        COLOR_CHANGE = colorChange * References.DAY_LENGTH;

        switch (widthPos) {
            default:
            case LEFT:
                POSITION_X = 0;
                break;
            case RIGHT:
                POSITION_X = 1;
                break;
            case MIDDLE:
                POSITION_X = 2;
                break;
        }

        switch (heightPos) {
            default:
            case TOP:
                POSITION_Y = 0;
                break;
            case BOTTOM:
                POSITION_Y = 1;
                break;
            case MIDDLE:
                POSITION_Y = 2;
                break;
        }
        OFFSET_X = xOffset * (POSITION_X == 1 ? -1 : 1);
        OFFSET_Y = yOffset * (POSITION_Y == 1 ? -1 : 1);
    }

    /**
     * Called by ForgeIngameGui.render().
     * float partialTicks = time since the last game tick.
     * ScaledResolution resolution = the game resolution.
     * int mouseX = the x position of the mouse.
     * int mouseY = the y position of the mouse.
     * RenderGameOverlayEvent.ElementType type = the type of overlay being rendered
     *
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void afterRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.BOSSHEALTH || OFFSET_X < 0 || OFFSET_Y < 0)
            return;

        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();

        FontRenderer fontRenderer = this.minecraftClient.font;

        // Calculate difficulty level in days with 1 decimal point
        int color = COLORS[0];
        long difficulty = DIFFICULTY;
        int partialDifficulty = (int) (difficulty % 24000L / 2400);

        if (COLOR_CHANGE >= 0L && difficulty >= 0L) {
            if (difficulty >= COLOR_CHANGE) {
                color = COLORS[COLORS.length - 1];
            }
            else {
                color = COLORS[(int) (difficulty / (double) COLOR_CHANGE * COLORS.length)];
            }
        }
        difficulty /= 24000L;

        String difficultyInfo = "Difficulty: " + difficulty + "." + partialDifficulty;

        // Calculate % of increase in difficulty rate
        double difficultyRate = DIFFICULTY_RATE;
        if (difficultyRate != 1.0) {
            difficultyInfo = difficultyInfo + " Rate: " + (int)(difficultyRate * 100.0) + "%";
        }

        int x, y;
        switch (POSITION_X) {
            case 0:
                x = 2;
                break;
            case 1:
                x = width - fontRenderer.width(difficultyInfo) - 2;
                break;
            case 2:
                x = (width >> 1) - (fontRenderer.width(difficultyInfo) >> 1);
                break;
            default:
                return;
        }
        switch (POSITION_Y) {
            case 0:
                y = 2;
                break;
            case 1:
                y = height - 10;
                break;
            case 2:
                y = (height >> 1) - 4;
                break;
            default:
                return;
        }
        x += OFFSET_X;
        y += OFFSET_Y;

        fontRenderer.drawShadow(event.getMatrixStack(), difficultyInfo, x, y, color);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
