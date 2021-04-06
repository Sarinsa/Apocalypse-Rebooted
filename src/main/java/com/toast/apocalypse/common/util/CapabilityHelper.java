package com.toast.apocalypse.common.util;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.entity.player.PlayerEntity;
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
    public static void setWorldDifficulty(@Nonnull World world, long difficulty) {
        if (!world.dimension().equals(World.OVERWORLD)) {
            throw new IllegalArgumentException("Cannot save world difficulty to other worlds than the overworld");
        }
        world.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).orElse(ApocalypseCapabilities.DIFFICULTY_CAPABILITY.getDefaultInstance()).setDifficulty(difficulty);
    }

    public static long getWorldDifficulty(@Nonnull World world) {
        if (!world.dimension().equals(World.OVERWORLD)) {
            throw new IllegalArgumentException("Cannot fetch world difficulty from other worlds than the overworld");
        }
        long diff = world.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).orElse(ApocalypseCapabilities.DIFFICULTY_CAPABILITY.getDefaultInstance()).getDifficulty();
        Apocalypse.LOGGER.error("Read world difficulty capability: " + diff);
        return diff;
    }
}
