package com.toast.apocalypse.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.toast.apocalypse.client.ApocalypseKeyBindings;
import com.toast.apocalypse.client.ClientRegister;
import com.toast.apocalypse.common.capability.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import fathertoast.crust.api.config.common.value.CrustAnchor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import static com.toast.apocalypse.client.ClientRegister.CLIENT_CONFIG;

public class DifficultyOverlayRenderHandler {
    
    /** Color sequence for the difficulty counter. */
    public static final int[] COLORS = {
            0xFFFFFF, 0x88FFFF, 0x88FF88, 0xFFFF88, 0xFFBB88, 0xFF8888
    };
    /** The amount of difficulty levels to pass before changing color. */
    public static long COLOR_THRESHOLD;
    
    
    /** Renders the client player's current Apocalypse difficulty level. */
    public static void renderDifficulty( ForgeGui gui, GuiGraphics guiGraphics, int width, int height ) {
        final LocalPlayer player = gui.getMinecraft().player;
        if( player == null ) return;
        
        // Check if we should render in creative mode
        if( player.isCreative() && !CLIENT_CONFIG.DIFFICULTY.renderDifficultyInCreative.get() )
            return;
        
        // Check if keybind only is enabled
        if( CLIENT_CONFIG.DIFFICULTY.keybindOnly.get() && !ApocalypseKeyBindings.TOGGLE_DIFFICULTY.isDown() )
            return;
        
        final long maxDifficulty = CapabilityHelper.getMaxPlayerDifficulty( player );
        
        // Don't bother rendering the difficulty
        // when it will constantly be at 0 or if
        // the player is dead
        if( maxDifficulty == 0L || player.isDeadOrDying() )
            return;
        
        // Format difficulty level text
        int color = COLORS[0];
        long difficulty = CapabilityHelper.getPlayerDifficulty( player );
        int partialDifficulty = difficulty <= 0 ? 0 : (int) (difficulty % References.DAY_LENGTH / 2400);
        
        // Determine what color to use for the text (scales with difficulty)
        if( COLOR_THRESHOLD >= 0L && difficulty >= 0L ) {
            int colorIndex = Math.min( (int) (difficulty / (double) COLOR_THRESHOLD * COLORS.length), COLORS.length - 1 );
            color = COLORS[colorIndex];
        }
        difficulty /= References.DAY_LENGTH;
        String parsedDifficulty = difficulty > 0L ? (difficulty + "." + partialDifficulty) : "0.0";
        String difficultyInfo = Component.translatable( References.DIFFICULTY, parsedDifficulty ).getString();
        
        // Calculate % of increase in difficulty rate
        double difficultyRate = CapabilityHelper.getPlayerDifficultyMult( player );
        
        if( difficultyRate != 1.0 ) {
            difficultyInfo = difficultyInfo + " " + Component.translatable( References.DIFFICULTY_RATE, (int) Math.ceil( difficultyRate * 100 ) + "%" ).getString();
        }
        final CrustAnchor xAnchor = CLIENT_CONFIG.DIFFICULTY.difficultyRenderXAnchor.get();
        final CrustAnchor yAnchor = CLIENT_CONFIG.DIFFICULTY.difficultyRenderYAnchor.get();
        final Font font = gui.getFont();
        
        int x = getXRenderPos(
                xAnchor,
                width,
                font.width( difficultyInfo ),
                CLIENT_CONFIG.DIFFICULTY.difficultyRenderXOffset.get() );
        int y = getYRenderPos(
                yAnchor,
                height,
                font.lineHeight,
                CLIENT_CONFIG.DIFFICULTY.difficultyRenderYOffset.get() );
        
        // Additional Y offset when boss bar is rendered, if enabled.
        if( !gui.getBossOverlay().events.isEmpty() && ClientRegister.CLIENT_CONFIG.DIFFICULTY.offsetForBossBar.get() ) {
            if( xAnchor == CrustAnchor.CENTER && yAnchor == CrustAnchor.TOP ) {
                y += 20;
            }
        }
        guiGraphics.drawString( font, difficultyInfo, x, y, color );
        RenderSystem.setShaderColor( 1.0F, 1.0F, 1.0F, 1.0F );
    }
    
    /** @return The GUI X-position to draw the difficulty level at. */
    private static int getXRenderPos( CrustAnchor anchor, int guiWidth, int stringWidth, int xOffset ) {
        if( anchor == null ) return 0;
        
        switch( anchor ) {
            case CENTER -> {
                return (guiWidth / 2) - (stringWidth / 2) + xOffset;
            }
            case RIGHT -> {
                return guiWidth - stringWidth + xOffset;
            }
            default -> {
                return xOffset;
            }
        }
    }
    
    /** @return The GUI Y-position to draw the difficulty level at. */
    private static int getYRenderPos( CrustAnchor anchor, int guiHeight, int stringHeight, int yOffset ) {
        if( anchor == null ) return 0;
        
        switch( anchor ) {
            case CENTER -> {
                return (guiHeight / 2) - (stringHeight / 2) + yOffset;
            }
            case BOTTOM -> {
                return guiHeight - stringHeight + yOffset;
            }
            default -> {
                return yOffset;
            }
        }
    }
}
