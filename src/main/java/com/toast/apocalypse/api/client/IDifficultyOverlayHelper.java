package com.toast.apocalypse.api.client;

import com.toast.apocalypse.api.IApocalypseApi;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/** Provides read access to properties and components used by Apocalypse's difficulty GUI overlay. */
public interface IDifficultyOverlayHelper {
    
    /** The difficulty GUI overlay's ID. */
    ResourceLocation OVERLAY_ID = ResourceLocation.fromNamespaceAndPath( IApocalypseApi.MOD_ID, "difficulty" );
    
    
    /** @return True if the difficulty overlay is active and drawing difficulty text on the screen this frame. */
    boolean isRendering();
    
    /**
     * Fetches the difficulty level of the specified player and turns it into
     * a translated string formatted the same way it would be displayed by the difficulty overlay.
     *
     * @param player The player to get the difficulty level for.
     * @return A formatted and translated difficulty level string.
     */
    String getFormattedDifficulty( Player player );
    
    /**
     * Fetches the difficulty multiplier of the specified player and turns it into
     * a translated string formatted the same way it would be displayed by the difficulty overlay.
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
    
    /** @return The x-position on the screen where the difficulty overlay is currently drawing the difficulty text. */
    int getPosX();
    
    /** @return The y-position on the screen where the difficulty overlay is currently drawing the difficulty text. */
    int getPosY();
}
