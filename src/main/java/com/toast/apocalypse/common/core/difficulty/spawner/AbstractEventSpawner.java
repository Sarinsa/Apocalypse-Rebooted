package com.toast.apocalypse.common.core.difficulty.spawner;

import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;

public abstract class AbstractEventSpawner implements ISpecialSpawner {

    @Override
    public int tick(ServerWorld serverWorld, boolean spawnEnemies, boolean spawnFriendlies) {

        if (this.shouldUpdate(serverWorld)) {
            this.tickSpawner(serverWorld);
        }
        // I don't see this number actually being used for anything, so always returning 0 shouldn't be a problem.
        return 0;
    }

    public abstract boolean shouldUpdate(ServerWorld serverWorld);

    public abstract void tickSpawner(ServerWorld serverWorld);
}
