package com.toast.apocalypse.common.core.difficulty.spawner;

import com.toast.apocalypse.common.core.difficulty.WorldDifficultyManager;
import net.minecraft.world.server.ServerWorld;

public class MoonSiegeSpawner extends AbstractEventSpawner {

    @Override
    public boolean shouldUpdate(ServerWorld serverWorld) {
        return WorldDifficultyManager.isFullMoon(serverWorld);
    }

    @Override
    public void tickSpawner(ServerWorld serverWorld) {

    }
}
