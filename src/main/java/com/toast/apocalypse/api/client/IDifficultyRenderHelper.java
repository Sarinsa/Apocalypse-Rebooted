package com.toast.apocalypse.api.client;

import net.minecraft.world.entity.player.Player;

/** Provides read access to properties and components used by Apocalypse's overlay difficulty renderer. */
public interface IDifficultyRenderHelper {
    
    /** @return True if the difficulty renderer is active and drawing difficulty text on the screen this frame. */
    boolean isRendering();
    
    /**
     * Fetches the difficulty level of the specified player and turns it into
     * a string formatted the same way it would be displayed by the difficulty renderer.
     *
     * @param player The player to get the difficulty level for.
     * @return A formatted and translated difficulty level string.
     */
    String getFormattedDifficulty( Player player );
    
    /**
     * Fetches the difficulty multiplier of the specified player and turns it into
     * a string formatted the same way it would be displayed by the difficulty renderer.
     *
     * @param player The player to get the difficulty multiplier for.
     * @return A formatted and translated difficulty multiplier string.
     */
    String getFormattedMultiplier( Player player );
    
    /**
     * @return The text color to use when drawing the difficulty text,
     * based on the specified player's current difficulty level.
     */
    int getTextColor( Player player );
    
    /** @return The x-position on the screen where Apocalypse is currently drawing the difficulty text. */
    int getPosX();
    
    /** @return The y-position on the screen where Apocalypse is currently drawing the difficulty text. */
    int getPosY();
}
