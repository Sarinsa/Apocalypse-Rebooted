package com.toast.apocalypse.common.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

/**
 * Contains various helper methods common to many mobs.
 */
public class MobHelper {

    /**
     * Checks the area around a mob to see if it can spawn.
     * @param entity The entity to check around.
     * @return True if the area is valid for spawning.
     */
    public static boolean canSpawn(LivingEntity entity) {
        World world = entity.getCommandSenderWorld();
        return world.noCollision(entity.getBoundingBox()) && world.noCollision(entity, entity.getBoundingBox());
    }
}