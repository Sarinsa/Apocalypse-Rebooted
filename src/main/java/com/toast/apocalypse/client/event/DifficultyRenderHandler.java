package com.toast.apocalypse.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.toast.apocalypse.client.ApocalypseKeyBindings;
import com.toast.apocalypse.common.core.config.ApocalypseClientConfig;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

public class DifficultyRenderHandler {

    /** The color sequence. */
    public static final int[] COLORS = {
            0xFFFFFF, 0x88FFFF, 0x88FF88, 0xFFFF88, 0xFFBB88, 0xFF8888
    };

    // Rendering properties for quick access.
    public static long COLOR_CHANGE;
    public static int POSITION_X;
    public static int POSITION_Y;
    public static int OFFSET_X;
    public static int OFFSET_Y;
    public static boolean RENDER_IN_CREATIVE;
    public static boolean KEYBIND_ONLY;

    /**
     * Updates the render info when rendering the world difficulty in-game.
     * Called from {@link ClientConfigReloadListener} when the client config is loaded/reloaded */
    public static void updateRenderPos(ApocalypseClientConfig.PositionWidthAnchor widthPos, ApocalypseClientConfig.PositionHeightAnchor heightPos, int xOffset, int yOffset) {
        switch (widthPos) {
            case LEFT -> POSITION_X = 0;
            case RIGHT -> POSITION_X = 1;
            case MIDDLE -> POSITION_X = 2;
        }
        switch (heightPos) {
            case TOP -> POSITION_Y = 0;
            case BOTTOM -> POSITION_Y = 1;
            case MIDDLE -> POSITION_Y = 2;
        }
        OFFSET_X = xOffset * (POSITION_X == 1 ? -1 : 1);
        OFFSET_Y = yOffset * (POSITION_Y == 1 ? -1 : 1);
    }


    public static void renderDifficulty(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int width, int height) {
        LocalPlayer player = gui.getMinecraft().player;

        if (player.isCreative() && !RENDER_IN_CREATIVE)
            return;

        if (KEYBIND_ONLY && !ApocalypseKeyBindings.TOGGLE_DIFFICULTY.isDown()) {
            return;
        }

        final long maxDifficulty = CapabilityHelper.getMaxPlayerDifficulty(player);

        // Don't bother rendering the difficulty
        // when it will constantly be at 0 or if
        // the player is dead.
        if (maxDifficulty == 0L || player.isDeadOrDying())
            return;

        Font font = gui.getFont();

        // Calculate difficulty level in days with one decimal.
        int color = COLORS[0];
        long difficulty = CapabilityHelper.getPlayerDifficulty(player);
        int partialDifficulty = difficulty <= 0 ? 0 : (int) (difficulty % 24000L / 2400);

        if (COLOR_CHANGE >= 0L && difficulty >= 0L) {
            if (difficulty >= COLOR_CHANGE) {
                color = COLORS[COLORS.length - 1];
            }
            else {
                color = COLORS[(int) (difficulty / (double) COLOR_CHANGE * COLORS.length)];
            }
        }
        difficulty /= 24000L;
        String parsedDifficulty = difficulty > 0L ? (difficulty + "." + partialDifficulty) : "0.0";
        String difficultyInfo = Component.translatable(References.DIFFICULTY, parsedDifficulty).getString();

        // Calculate % of increase in difficulty rate
        double difficultyRate = CapabilityHelper.getPlayerDifficultyMult(player);

        if (difficultyRate != 1.0) {
            difficultyInfo = difficultyInfo + " " + Component.translatable(References.DIFFICULTY_RATE, (int)(difficultyRate * 100.0) + "%").getString();
        }
        int x;
        int y;

        switch (POSITION_X) {
            case 0:
                x = 2;
                break;
            case 1:
                x = width - font.width(difficultyInfo) - 2;
                break;
            case 2:
                x = (width >> 1) - (font.width(difficultyInfo) >> 1);
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

        guiGraphics.drawString(font, difficultyInfo, x, y, color);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
