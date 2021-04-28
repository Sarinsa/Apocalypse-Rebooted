package com.toast.apocalypse.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

/** Represents a mob type that spawns during full moons */
public interface IFullMoonMob {

    /**
     * Actually checks if this mob has direct
     * line of sight to it's target.
     *
     * @param mob The mob's line of sight to check.
     * @param entity The entity that should be checked if this mob can see.
     */
    default boolean canSeeDirectly(Entity mob, Entity entity) {
        Vector3d vector3d = new Vector3d(mob.getX(), mob.getEyeY(), mob.getZ());
        Vector3d vector3d1 = new Vector3d(entity.getX(), entity.getEyeY(), entity.getZ());
        return mob.level.clip(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, mob)).getType() == RayTraceResult.Type.MISS;
    }
}
