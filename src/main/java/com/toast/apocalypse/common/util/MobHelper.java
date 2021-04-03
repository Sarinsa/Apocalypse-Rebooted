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

    /**
     * External implementation of EntityLivingBase.attackEntityAsMob(Entity) for the mobs in this mod with optional minimum damage.
     *
     * @param attacker The attacking entity.
     * @param target The entity being attacked.
     * @param minDamage The minimum damage that will be dealt (will not apply if the attack fails).
     * @return True if the attack is successful.
     *
     * @see LivingEntity#hurt(DamageSource, float)
     */
    public static boolean attackEntityForMob(LivingEntity attacker, Entity target, float minDamage) {
        LivingEntity livingTarget;
        float prevHealth;
        float damage;
        int knockback;

        try {
            damage = (float) attacker.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        }
        catch (Exception e) {
            damage = 4.0F;
        }

        if (target instanceof LivingEntity) {
            livingTarget = (LivingEntity) target;
            prevHealth = livingTarget.getHealth() + livingTarget.getAbsorptionAmount();
            // TODO: check if this accepts a potentially empty stack
            damage += EnchantmentHelper.getDamageBonus(attacker.getUseItem(), livingTarget.getMobType());
            knockback = EnchantmentHelper.getKnockbackBonus(attacker);
        }
        else {
            livingTarget = null;
            prevHealth = 0.0F;
            knockback = 0;
        }

        if (target.hurt(DamageSource.mobAttack(attacker), damage)) {
            if (knockback > 0) {
                Vector3d velocity = target.getDeltaMovement();
                velocity.add(-MathHelper.sin(attacker.yRot * (float) Math.PI / 180.0F) * knockback * 0.5F, 0.1, MathHelper.cos(attacker.yRot * (float) Math.PI / 180.0F) * knockback * 0.5F);
                target.setDeltaMovement(velocity);
                attacker.xo *= 0.6;
                attacker.zo *= 0.6;
            }

            int fire = EnchantmentHelper.getFireAspect(attacker) << 2;
            if (attacker.isOnFire()) {
                fire += 2;
            }
            if (fire > 0) {
                target.setSecondsOnFire(fire);
            }

            if (target instanceof LivingEntity) {
                EnchantmentHelper.doPostHurtEffects((LivingEntity) target, attacker); // Triggers hit entity's enchants
            }
            EnchantmentHelper.doPostDamageEffects(attacker, target); // Triggers attacker's enchants

            // Enforce minimum damage limit
            if (minDamage > 0.0F && livingTarget != null) {
                float remainingDamage = livingTarget.getHealth() + livingTarget.getAbsorptionAmount() + minDamage - prevHealth;
                if (remainingDamage > 0.0F) {
                    if (livingTarget.getAbsorptionAmount() >= remainingDamage) {
                        livingTarget.setAbsorptionAmount(livingTarget.getAbsorptionAmount() - remainingDamage);
                    }
                    else {
                        if (livingTarget.getAbsorptionAmount() > 0.0F) {
                            remainingDamage -= livingTarget.getAbsorptionAmount();
                            livingTarget.setAbsorptionAmount(0.0F);
                        }
                        livingTarget.setHealth(livingTarget.getHealth() - remainingDamage);
                    }
                }
            }
            return true;
        }
        return false;
    }
}