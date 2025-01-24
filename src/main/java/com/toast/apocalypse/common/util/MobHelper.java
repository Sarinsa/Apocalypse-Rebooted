package com.toast.apocalypse.common.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class MobHelper {


    /**
     * This method comes with a max cap. If the amount of entities that have been found exceeds the cap,
     * entities in the list will be removed from the top of the list until the list size matches the cap.
     */
    public static <T extends Entity> List<? extends T> getLoadedEntitiesCapped(Class<? extends T> entityClass, LevelAccessor level, AABB box, @Nullable Predicate<? super T> predicate, final int cap) {
        List<? extends T> list = level.getEntitiesOfClass(entityClass, box, predicate);

        if (list.isEmpty()) {
            return list;
        }
        int count = list.size();

        // Limit the amount of mobs.
        while (count > cap) {
            --count;
            list.remove(count);
        }
        return list;
    }
}