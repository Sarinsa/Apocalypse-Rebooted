package com.toast.apocalypse.common.capability;

import com.toast.apocalypse.api.IApocalypseApi;
import com.toast.apocalypse.common.capability.difficulty.DifficultyCapProvider;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

/**
 * Helper class for manipulating capability data.
 * <br><br>
 * This class is usually not subject to breaking changes, but
 * devs are encouraged to use the API instead to interact with
 * Apocalypse's capability data.
 *
 * @see IApocalypseApi
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
    public static void setDifficulty( ServerPlayer player, long difficulty ) {
        player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).ifPresent( ( capability ) ->
        {
            capability.setDifficulty( difficulty );
            NetworkHelper.sendUpdatePlayerDifficulty( player, difficulty );
        } );
    }
    
    /** @return The current difficulty level of the specified player. */
    public static long getDifficulty( Player player ) {
        return player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).orElse( DifficultyCapProvider.SUPPLIER.get() ).getDifficulty();
    }
    
    /**
     * @return The current difficulty level of the specified player divided by
     * {@link References#DAY_LENGTH}, which in vanilla is {@code 24,000} ticks.
     * <br><br>
     * This is the "true" difficulty level of the player, which is displayed in the overlay
     * and referenced in configs and most difficulty-related calculations.
     */
    public static double getScaledDifficulty( Player player ) {
        return fractalDivByDayLength( getDifficulty( player ) );
    }
    
    /**
     * @return The partial difficulty level of the specified player,
     * which is the first digit of the fractional remainder you get when dividing difficulty level by day length.
     */
    public static int getPartialScaledDifficulty( Player player ) {
        final long difficulty = getDifficulty( player );
        return difficulty <= 0 ? 0 : (int) (difficulty % References.DAY_LENGTH / 2400);
    }
    
    /**
     * @return The partial difficulty level of the specified difficulty level,
     * which is the first digit of the fractional remainder you get when dividing difficulty level by day length.
     */
    public static int getPartialScaledDifficulty( long difficulty ) {
        return difficulty <= 0 ? 0 : (int) (difficulty % References.DAY_LENGTH / 2400);
    }
    
    /**
     * Sets the maximum difficulty level for the specified player.
     *
     * @param player        The player to update data for.
     * @param maxDifficulty The new maximum difficulty level.
     */
    public static void setMaxDifficulty( ServerPlayer player, long maxDifficulty ) {
        player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).ifPresent( ( capability ) ->
        {
            capability.setMaxDifficulty( maxDifficulty );
            NetworkHelper.sendUpdatePlayerMaxDifficulty( player, maxDifficulty );
        } );
    }
    
    /** @return The current maximum difficulty level for the specified player. */
    public static long getMaxDifficulty( Player player ) {
        return player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).orElse( DifficultyCapProvider.SUPPLIER.get() ).getMaxDifficulty();
    }
    
    /**
     * @return The current maximum difficulty level for the specified player divided by
     * {@link References#DAY_LENGTH}, which in vanilla is {@code 24,000} ticks.
     */
    public static long getScaledMaxDifficulty( Player player ) {
        final long maxDifficulty = getMaxDifficulty( player );
        return maxDifficulty / References.DAY_LENGTH;
    }
    
    /**
     * Sets the current difficulty increment multiplier for the specified player.
     *
     * @param player     The player to update data for.
     * @param multiplier The new difficulty multiplier.
     */
    public static void setDifficultyMult( ServerPlayer player, double multiplier ) {
        player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).ifPresent( ( capability ) ->
        {
            capability.setMultiplier( multiplier );
            NetworkHelper.sendUpdatePlayerDifficultyMult( player, multiplier );
        } );
    }
    
    /** @return The current difficulty increment multiplier for the specified player. */
    public static double getDifficultyMult( Player player ) {
        return player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).orElse( DifficultyCapProvider.SUPPLIER.get() ).getMultiplier();
    }
    
    /**
     * Sets the current partial difficulty (sub-tick) for the specified player.
     *
     * @param player            The player to update data for.
     * @param partialDifficulty The new difficulty multiplier.
     */
    public static void setPartialDifficulty( ServerPlayer player, double partialDifficulty ) {
        player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).ifPresent( ( capability ) ->
        {
            capability.setPartialDifficulty( partialDifficulty );
            // Don't think clients really care about this
            //NetworkHelper.sendUpdatePlayerPartialDifficulty( player, partialDifficulty );
        } );
    }
    
    /** @return The current partial difficulty (sub-tick) for the specified player. */
    public static double getPartialDifficulty( Player player ) {
        return player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).orElse( DifficultyCapProvider.SUPPLIER.get() ).getPartialDifficulty();
    }
    
    /**
     * @return The specified long value divided by {@link References#DAY_LENGTH}, which in vanilla is {@code 24,000} ticks.
     * <br><br>
     * This is the "true" difficulty level of the player, which is displayed in the overlay
     * and referenced in configs and most difficulty-related calculations.
     */
    public static long divByDayLength( long value ) {
        return value / References.DAY_LENGTH;
    }
    
    /**
     * @return The specified long value divided by {@link References#DAY_LENGTH}, which in vanilla is {@code 24,000} ticks.
     * Cast as a double value to include any fractals resulting from division.
     * <br><br>
     * This is the "true" difficulty level of the player, which is displayed in the overlay
     * and referenced in configs and most difficulty-related calculations.
     */
    public static double fractalDivByDayLength( long value ) {
        return value / (double) References.DAY_LENGTH;
    }
    
    /**
     * @return The specified long value multiplied by {@link References#DAY_LENGTH}, which in vanilla is {@code 24,000} ticks.
     * <br><br>
     * This is the "true" difficulty level of the player, which is displayed in the overlay
     * and referenced in configs and most difficulty-related calculations.
     */
    public static long mulByDayLength( long value ) {
        return value * References.DAY_LENGTH;
    }
    
    /**
     * @return The specified double value multiplied by {@link References#DAY_LENGTH}, which in vanilla is {@code 24,000} ticks.
     */
    public static long mulByDayLength( double value ) {
        // Split it up since double may not have enough precision to handle direct multiplication accurately
        long integerPotion = (long) value;
        double decimalPortion = value - integerPotion;
        return (long) (decimalPortion * References.DAY_LENGTH) + integerPotion * References.DAY_LENGTH;
    }
}