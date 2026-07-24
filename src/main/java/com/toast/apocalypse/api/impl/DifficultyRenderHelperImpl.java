package com.toast.apocalypse.api.impl;

import com.toast.apocalypse.api.client.IDifficultyRenderHelper;
import com.toast.apocalypse.client.renderer.DifficultyOverlayRenderHandler;
import com.toast.apocalypse.common.capability.CapabilityHelper;
import net.minecraft.world.entity.player.Player;

/** Provides read access to properties and components used by Apocalypse's overlay difficulty renderer. */
public final class DifficultyRenderHelperImpl implements IDifficultyRenderHelper {
    
    /** @return True if the difficulty renderer is active and drawing difficulty text on the screen this frame. */
    @Override
    public boolean isRendering() {
        return DifficultyOverlayRenderHandler.isRendering();
    }
    
    /**
     * Fetches the difficulty level of the specified player and turns it into
     * a string formatted the same way it would be displayed by the difficulty renderer.
     *
     * @param player The player to get the difficulty level for.
     * @return A formatted and translated difficulty level string.
     */
    @Override
    public String getFormattedDifficulty( Player player ) {
        final long difficulty = CapabilityHelper.getPlayerDifficulty( player );
        return DifficultyOverlayRenderHandler.getFormattedDifficulty( difficulty );
    }
    
    /**
     * Fetches the difficulty multiplier of the specified player and turns it into
     * a string formatted the same way it would be displayed by the difficulty renderer.
     *
     * @param player The player to get the difficulty multiplier for.
     * @return A formatted and translated difficulty multiplier string.
     */
    @Override
    public String getFormattedMultiplier( Player player ) {
        final double multiplier = CapabilityHelper.getPlayerDifficultyMult( player );
        return DifficultyOverlayRenderHandler.getFormattedMultiplier( multiplier );
    }
    
    /** @return The currently used color for the difficulty text. */
    @Override
    public int getTextColor( Player player ) {
        final long difficulty = CapabilityHelper.getPlayerDifficulty( player );
        return DifficultyOverlayRenderHandler.getColorForDifficulty( difficulty );
    }
    
    /** @return The x-position on the screen where Apocalypse is currently drawing the difficulty text. */
    @Override
    public int getPosX() {
        return DifficultyOverlayRenderHandler.getPosX();
    }
    
    /** @return The y-position on the screen where Apocalypse is currently drawing the difficulty text. */
    @Override
    public int getPosY() {
        return DifficultyOverlayRenderHandler.getPosY();
    }
}
