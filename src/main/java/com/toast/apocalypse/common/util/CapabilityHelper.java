package com.toast.apocalypse.common.util;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.network.NetworkHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Helper class for easily manipulating capability
 * data without writing a long line every time.
 */
@SuppressWarnings("all")
public class CapabilityHelper {

    //
    // RAIN DAMAGE
    //
    public static void setRainTick(@Nonnull PlayerEntity playerEntity, int ticks) {
        playerEntity.getCapability(ApocalypseCapabilities.RAIN_TICK_CAPABILITY).orElse(ApocalypseCapabilities.RAIN_TICK_CAPABILITY.getDefaultInstance()).setTicks(ticks);
    }

    public static void addRainTick(@Nonnull PlayerEntity playerEntity) {
        playerEntity.getCapability(ApocalypseCapabilities.RAIN_TICK_CAPABILITY).orElse(ApocalypseCapabilities.RAIN_TICK_CAPABILITY.getDefaultInstance()).addTick();
    }

    public static void clearRainTicks(@Nonnull PlayerEntity playerEntity) {
        playerEntity.getCapability(ApocalypseCapabilities.RAIN_TICK_CAPABILITY).orElse(ApocalypseCapabilities.RAIN_TICK_CAPABILITY.getDefaultInstance()).clearTicks();
    }

    public static int getRainTicks(@Nonnull PlayerEntity playerEntity) {
        return playerEntity.getCapability(ApocalypseCapabilities.RAIN_TICK_CAPABILITY).orElse(ApocalypseCapabilities.RAIN_TICK_CAPABILITY.getDefaultInstance()).getRainTicks();
    }

    //
    // DIFFICULTY
    //
    public static void setPlayerDifficulty(@Nonnull ServerPlayerEntity player, long difficulty) {
        player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).orElse(ApocalypseCapabilities.DIFFICULTY_CAPABILITY.getDefaultInstance()).setDifficulty(difficulty);
        NetworkHelper.sendUpdatePlayerDifficulty(player, difficulty);
    }

    public static long getPlayerDifficulty(@Nonnull PlayerEntity player) {
        return player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).orElse(ApocalypseCapabilities.DIFFICULTY_CAPABILITY.getDefaultInstance()).getDifficulty();
    }

    public static void setMaxPlayerDifficulty(@Nonnull ServerPlayerEntity player, long maxDifficulty) {
        player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).orElse(ApocalypseCapabilities.DIFFICULTY_CAPABILITY.getDefaultInstance()).setMaxDifficulty(maxDifficulty);
        NetworkHelper.sendUpdatePlayerMaxDifficulty(player, maxDifficulty);
    }

    public static long getMaxPlayerDifficulty(@Nonnull PlayerEntity player) {
        return player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).orElse(ApocalypseCapabilities.DIFFICULTY_CAPABILITY.getDefaultInstance()).getMaxDifficulty();
    }

    //
    // EVENT DATA
    //
    public static void setEventData(@Nonnull World world, CompoundNBT data) {
        if (!world.dimension().equals(World.OVERWORLD)) {
            throw new IllegalArgumentException("Cannot save event data to other worlds than the overworld");
        }
        world.getCapability(ApocalypseCapabilities.EVENT_DATA_CAPABILITY).orElse(ApocalypseCapabilities.EVENT_DATA_CAPABILITY.getDefaultInstance()).setEventData(data);
    }

    public static CompoundNBT getEventData(@Nonnull World world) {
        if (!world.dimension().equals(World.OVERWORLD)) {
            throw new IllegalArgumentException("Cannot fetch event data from other worlds than the overworld");
        }
        return world.getCapability(ApocalypseCapabilities.EVENT_DATA_CAPABILITY).orElse(ApocalypseCapabilities.EVENT_DATA_CAPABILITY.getDefaultInstance()).getEventData();
    }
}
