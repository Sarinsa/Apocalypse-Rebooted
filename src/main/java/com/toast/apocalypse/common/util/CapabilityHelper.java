package com.toast.apocalypse.common.util;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;

/**
 * Helper class for easily manipulating capability
 * data without writing a long line every time.
 */
@SuppressWarnings("all")
public class CapabilityHelper {

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
}
