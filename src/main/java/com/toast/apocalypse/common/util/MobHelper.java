package com.toast.apocalypse.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * Contains various helper methods common to many mobs.
 */
public class MobHelper {

    /**
     * Checks the area around a mob to see if it can spawn (no preventing collision).
     *
     * @param entity The entity to check around.
     * @return True if the area is valid for spawning.
     */
    public static boolean canSpawn(LivingEntity entity) {
        World world = entity.level;
        return world.noCollision(entity.getBoundingBox()) && world.noCollision(entity, entity.getBoundingBox());
    }

    /**
     * Operates similarly to {@link IWorld#getLoadedEntitiesOfClass(Class, AxisAlignedBB, Predicate)}.<br>
     * <br>
     * This method comes with a max cap. If the amount of entities that have been found exceeds the cap,
     * entities in the list will be removed from the top of the list until the list size matches the cap.
     *
     */
    public static <T extends Entity> List<T> getLoadedEntitiesCapped(Class<? extends T> entityClass, IWorld world, AxisAlignedBB box, @Nullable Predicate<? super T> predicate, final int cap) {
        List<T> list = world.getLoadedEntitiesOfClass(entityClass, box, predicate);

        if (list.isEmpty()) {
            return list;
        }
        int count = list.size();

        // Limit the amount of mobs to alert
        while (count > cap) {
            --count;
            list.remove(count);
        }
        return list;
    }
}