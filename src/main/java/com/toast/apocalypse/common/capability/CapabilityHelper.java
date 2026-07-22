package com.toast.apocalypse.common.capability;

import com.toast.apocalypse.common.capability.difficulty.DifficultyCapProvider;
import com.toast.apocalypse.common.network.NetworkHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;

/**
 * Helper class for manipulating capability data.
 * <br><br>
 * This class is usually not subject to breaking changes, but
 * devs are encouraged to use the API instead to interact with
 * Apocalypse's capability data.
 *
 * @see com.toast.apocalypse.api.plugin.IApocalypseApi
 */
@ApiStatus.Internal
public class CapabilityHelper {
    
    //-----------------------------------------------------------
    //                      DIFFICULTY
    //-----------------------------------------------------------
    
    /**
     * Sets the difficulty level for the specified player.
     *
     * @param player     The player to update data for.
     * @param difficulty The new difficulty level.
     */
    public static void setPlayerDifficulty( @Nonnull ServerPlayer player, long difficulty ) {
        player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).ifPresent( ( capability ) ->
        {
            capability.setDifficulty( difficulty );
            NetworkHelper.sendUpdatePlayerDifficulty( player, difficulty );
        } );
    }
    
    /** @return The current difficulty level of the specified player. */
    public static long getPlayerDifficulty( @Nonnull Player player ) {
        return player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).orElse( DifficultyCapProvider.SUPPLIER.get() ).getDifficulty();
    }
    
    /**
     * Sets the maximum difficulty level for the specified player.
     *
     * @param player        The player to update data for.
     * @param maxDifficulty The new maximum difficulty level.
     */
    public static void setMaxPlayerDifficulty( @Nonnull ServerPlayer player, long maxDifficulty ) {
        player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).ifPresent( ( capability ) ->
        {
            capability.setMaxDifficulty( maxDifficulty );
            NetworkHelper.sendUpdatePlayerMaxDifficulty( player, maxDifficulty );
        } );
    }
    
    /** @return The current maximum difficulty level for the specified player. */
    public static long getMaxPlayerDifficulty( @Nonnull Player player ) {
        return player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).orElse( DifficultyCapProvider.SUPPLIER.get() ).getMaxDifficulty();
    }
    
    /**
     * Sets the current difficulty increment multiplier for the specified player.
     *
     * @param player     The player to update data for.
     * @param multiplier The new difficulty multiplier.
     */
    public static void setPlayerDifficultyMult( @Nonnull ServerPlayer player, double multiplier ) {
        player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).ifPresent( ( capability ) ->
        {
            capability.setDifficultyMult( multiplier );
            NetworkHelper.sendUpdatePlayerDifficultyMult( player, multiplier );
        } );
    }
    
    /** @return The current difficulty increment multiplier for the specified player. */
    public static double getPlayerDifficultyMult( @Nonnull Player player ) {
        return player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).orElse( DifficultyCapProvider.SUPPLIER.get() ).getDifficultyMult();
    }
}
