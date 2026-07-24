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
    
    /** True if the overlay is active and rendering this frame. */
    private static boolean IS_RENDERING;
    
    // TODO - Make this a configurable list of colors
    /** Color sequence for the difficulty counter. */
    private static final int[] COLORS = {
            0xFFFFFF, 0x88FFFF, 0x88FF88, 0xFFFF88, 0xFFBB88, 0xFF8888
    };
    /** The amount of difficulty levels to pass before changing color. */
    public static long COLOR_THRESHOLD;
    
    /** The current GUI x-position the difficulty text is being drawn at. */
    private static int X_POS;
    /** The current GUI y-position the difficulty text is being drawn at. */
    private static int Y_POS;
    
    
    /** Renders the client player's current Apocalypse difficulty level. */
    public static void renderDifficulty( ForgeGui gui, GuiGraphics guiGraphics, int width, int height ) {
        // Reset flag
        IS_RENDERING = false;
        
        final LocalPlayer player = gui.getMinecraft().player;
        if( player == null )
            return;
        
        // Check if we should render in creative mode
        if( player.isCreative() && !CLIENT_CONFIG.DIFFICULTY.renderDifficultyInCreative.get() )
            return;
        
        // Check if keybind only is enabled
        if( CLIENT_CONFIG.DIFFICULTY.keybindOnly.get() && !ApocalypseKeyBindings.TOGGLE_DIFFICULTY.isDown() )
            return;
        
        final long maxDifficulty = CapabilityHelper.getMaxPlayerDifficulty( player );
        
        // Don't bother rendering the difficulty when it will
        // constantly be at 0 or if the player is dead.
        if( maxDifficulty == 0L || player.isDeadOrDying() )
            return;
        
        // Passed all checks, flag as active
        IS_RENDERING = true;
        
        // Build the difficulty text
        final long difficulty = CapabilityHelper.getPlayerDifficulty( player );
        final double multiplier = CapabilityHelper.getPlayerDifficultyMult( player );
        final StringBuilder builder = new StringBuilder();
        
        builder.append( getFormattedDifficulty( difficulty ) );
        
        // Append multiplier, if it is significant
        if( multiplier != 1.0 ) {
            builder.append( " " );
            builder.append( getFormattedMultiplier( multiplier ) );
        }
        final Font font = gui.getFont();
        final CrustAnchor xAnchor = CLIENT_CONFIG.DIFFICULTY.difficultyRenderXAnchor.get();
        final CrustAnchor yAnchor = CLIENT_CONFIG.DIFFICULTY.difficultyRenderYAnchor.get();
        final int xOffset = CLIENT_CONFIG.DIFFICULTY.difficultyRenderXOffset.get();
        final int yOffset = CLIENT_CONFIG.DIFFICULTY.difficultyRenderYOffset.get();
        
        final String difficultyText = builder.toString();
        
        X_POS = calcXRenderPos( xAnchor, width, font.width( difficultyText ), xOffset );
        Y_POS = calcYRenderPos( yAnchor, height, font.lineHeight, yOffset );
        
        // Additional Y offset when boss bar is rendered, if enabled.
        if( !gui.getBossOverlay().events.isEmpty() && ClientRegister.CLIENT_CONFIG.DIFFICULTY.offsetForBossBar.get() ) {
            if( xAnchor == CrustAnchor.CENTER && yAnchor == CrustAnchor.TOP ) {
                Y_POS += 20;
            }
        }
        guiGraphics.drawString( font, difficultyText, X_POS, Y_POS, getColorForDifficulty( difficulty ) );
        RenderSystem.setShaderColor( 1.0F, 1.0F, 1.0F, 1.0F );
    }
    
    /** @return True if the difficulty renderer is currently active and rendering difficulty text on the screen. */
    public static boolean isRendering() {
        return IS_RENDERING;
    }
    
    /** @return The given difficulty level as a formatted, translated string. */
    public static String getFormattedDifficulty( long difficulty ) {
        int partialDifficulty = difficulty <= 0 ? 0 : (int) (difficulty % References.DAY_LENGTH / 2400);
        difficulty /= References.DAY_LENGTH;
        String parsedDifficulty = difficulty > 0L ? (difficulty + "." + partialDifficulty) : "0.0";
        return Component.translatable( References.DIFFICULTY, parsedDifficulty ).getString();
    }
    
    /** @return The player's current difficulty multiplier as a formatted, translated string. */
    public static String getFormattedMultiplier( double multiplier ) {
        return Component.translatable( References.DIFFICULTY_RATE, (int) Math.ceil( multiplier * 100 ) + "%" ).getString();
    }
    
    /** @return The color to use when rendering the difficulty text. */
    public static int getColorForDifficulty( long difficulty ) {
        if( COLOR_THRESHOLD >= 0L && difficulty >= 0L ) {
            int colorIndex = Math.min( (int) (difficulty / (double) COLOR_THRESHOLD * COLORS.length), COLORS.length - 1 );
            return COLORS[colorIndex];
        }
        return COLORS[0];
    }
    
    /** @return The current X-position on the screen where difficulty text is being drawn. */
    public static int getPosX() {
        return X_POS;
    }
    
    /** @return The current Y-position on the screen where difficulty text is being drawn. */
    public static int getPosY() {
        return Y_POS;
    }
    
    /** @return Calculates and returns the GUI X-position to draw the difficulty level at. */
    private static int calcXRenderPos( CrustAnchor anchor, int guiWidth, int stringWidth, int xOffset ) {
        if( anchor == null ) return 0;
        
        return switch( anchor ) {
            case CENTER -> (guiWidth / 2) - (stringWidth / 2) + xOffset;
            case RIGHT -> guiWidth - stringWidth + xOffset;
            default -> xOffset;
        };
    }
    
    /** @return Calculates and returns the GUI Y-position to draw the difficulty level at. */
    private static int calcYRenderPos( CrustAnchor anchor, int guiHeight, int stringHeight, int yOffset ) {
        if( anchor == null ) return 0;
        
        return switch( anchor ) {
            case CENTER -> (guiHeight / 2) - (stringHeight / 2) + yOffset;
            case BOTTOM -> guiHeight - stringHeight + yOffset;
            default -> yOffset;
        };
    }
}
