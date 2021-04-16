package com.toast.apocalypse.api.plugin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

/**
 * All sub-helpers and registration methods
 * for a mod plugin are present here.
 */
public interface IRegistryHelper {

    /**
     * @param entityType The entity type of the full moon mob to register.
     *
     * @param baseSpawnChance The base chance for this mob to spawn during full moon events.
     *                        The spawn chance will later be inserted into Apocalypse's config
     *                        where it can be tweaked further by the user if wanted.
     *
     * @param persistent Determines whether or not this full moon mob should be
     *                   forced to not despawn during full moons or not.
     */
    <T extends LivingEntity> void registerFullMoonMob(EntityType<T> entityType, int baseSpawnChance, boolean persistent);
}
