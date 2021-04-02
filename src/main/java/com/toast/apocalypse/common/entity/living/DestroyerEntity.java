package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.api.IFullMoonMob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * This is a full moon mob similar to a ghast, though it has unlimited aggro range ignoring line of sight and
 * its fireballs can destroy anything within a small area.
 */
public class DestroyerEntity extends GhastEntity implements IFullMoonMob {

    public DestroyerEntity(EntityType<? extends GhastEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createDestroyerAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.FOLLOW_RANGE, Double.POSITIVE_INFINITY);
    }

    /**
     * Completely ignore line of sight; the target
     * is always "visible"
     */
    @Override
    public boolean canSee(Entity entity) {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damage) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        // Prevent instant fireball death and return to sender advancement
        else if (damageSource.getDirectEntity() instanceof FireballEntity && damageSource.getEntity() instanceof PlayerEntity) {
            super.hurt(DamageSource.playerAttack((PlayerEntity) damageSource.getEntity()), this.getMaxHealth() / 2.0F);
            return true;
        }
        else {
            return super.hurt(damageSource, damage);
        }
    }
}
