package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.entity.GhostEntity;
import com.toast.apocalypse.common.util.WorldDifficultyManager;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityEvents {

    /**
     * Cancel ghosts despawning during full moons.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDespawnCheck(LivingSpawnEvent.AllowDespawn event) {
        if (event.getEntityLiving() instanceof GhostEntity) {
            if (WorldDifficultyManager.isFullMoon(event.getWorld()))
                event.setResult(Event.Result.DENY);
        }
    }
}
