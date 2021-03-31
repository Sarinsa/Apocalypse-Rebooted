package com.toast.apocalypse.common.util;

import net.minecraft.world.IWorld;

/**
 * The major backbone of Apocalypse, this class manages everything to do with the world difficulty - increases it over time,
 * saves and loads it to and from the disk, and notifies clients of changes to it.<br>
 * In addition, it houses many helper methods related to world difficulty and save data.
 */
public class WorldDifficultyManager {

    public static boolean isFullMoon(IWorld world) {
        return world.getMoonBrightness() == 1.0F;
    }
}
