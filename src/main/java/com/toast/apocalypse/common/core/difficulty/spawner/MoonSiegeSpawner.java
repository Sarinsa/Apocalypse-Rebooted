package com.toast.apocalypse.common.core.difficulty.spawner;

import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import net.minecraft.world.server.ServerWorld;

public class MoonSiegeSpawner extends AbstractEventSpawner {

    @Override
    public boolean shouldUpdate(ServerWorld serverWorld) {
        return PlayerDifficultyManager.isFullMoon(serverWorld);
    }

    @Override
    public void tickSpawner(ServerWorld serverWorld) {

    }
}
