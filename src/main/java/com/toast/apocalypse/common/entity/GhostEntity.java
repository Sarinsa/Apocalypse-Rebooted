package com.toast.apocalypse.common.entity;

import com.toast.apocalypse.api.IFullMoonMob;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class GhostEntity extends FlyingEntity implements IMob, IFullMoonMob {

    public GhostEntity(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
        this.xpReward = 3;
    }

    public static AttributeModifierMap.MutableAttribute createGhostAttributes() {
        return FlyingEntity.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.1D);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new GhostEntity.NearestAttackablePlayerTargetGoal<>(this, PlayerEntity.class, true));
        this.goalSelector.addGoal(0, new GhostEntity.MeleeAttackGoal<>(this));
        this.goalSelector.addGoal(0, new GhostEntity.FlyTowardsTargetGoal<>(this));
        this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true; // Immune to drowning
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENDERMAN_SCREAM;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLAZE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        // No step sound
    }

    @Override
    public CreatureAttribute getMobType() {
        return CreatureAttribute.UNDEAD;
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
    }

    private static class FlyTowardsTargetGoal<T extends GhostEntity> extends Goal {

        final T ghostEntity;

        protected FlyTowardsTargetGoal(T ghostEntity) {
            this.ghostEntity = ghostEntity;
        }

        @Override
        public boolean canUse() {
            MovementController moveControl = this.ghostEntity.getMoveControl();
            if (!moveControl.hasWanted()) {
                return true;
            } else {
                double d0 = moveControl.getWantedX() - this.ghostEntity.getX();
                double d1 = moveControl.getWantedY() - this.ghostEntity.getY();
                double d2 = moveControl.getWantedZ() - this.ghostEntity.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            Random random = this.ghostEntity.getRandom();
            double d0 = this.ghostEntity.getX() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d1 = this.ghostEntity.getY() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d2 = this.ghostEntity.getZ() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.ghostEntity.getMoveControl().setWantedPosition(d0, d1, d2, 1.0D);
        }
    }

    private static class NearestAttackablePlayerTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

        public NearestAttackablePlayerTargetGoal(MobEntity mobEntity, Class<T> targetClass, boolean longMemory) {
            super(mobEntity, targetClass, longMemory);
        }
    }

    private static class MeleeAttackGoal<T extends GhostEntity> extends Goal {

        final T ghostEntity;

        protected MeleeAttackGoal(T ghost) {
            this.ghostEntity = ghost;
        }

        @Override
        public boolean canUse() {
            return false;
        }
    }
}
