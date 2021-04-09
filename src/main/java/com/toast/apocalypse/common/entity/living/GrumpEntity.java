package com.toast.apocalypse.common.entity.living;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Random;

/**
 * This is a full moon mob that is meant to be a high threat to players that are not in a safe area from them.
 * Grumps fly, have a pulling attack, and have a melee attack that can't be reduced below 2 damage and applies a
 * short gravity effect.<br>
 * Unlike most full moon mobs, this one has no means of breaking through defenses and therefore relies on the
 * player being vulnerable to attack - whether by will or by other mobs breaking through to the player.
 */
public class GrumpEntity extends GhastEntity implements IMob {

    public GrumpEntity(EntityType<? extends GhastEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createGrumpAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FLYING_SPEED, 0.8D)
                .add(Attributes.FOLLOW_RANGE, Double.POSITIVE_INFINITY);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new GrumpEntity.MeleeAttackGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, (p_213812_1_) -> {
            return Math.abs(p_213812_1_.getY() - this.getY()) <= 4.0D;
        }));
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize entitySize) {
        return 0.65F;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true; // Immune to drowning
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    public static boolean checkGrumpSpawnRules(EntityType<? extends GrumpEntity> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && MobEntity.checkMobSpawnRules(entityType, world, spawnReason, pos, random);
    }

    /** Copied from ghast */
    static class LookAroundGoal extends Goal {
        private final GrumpEntity grump;

        public LookAroundGoal(GrumpEntity grump) {
            this.grump = grump;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        public void tick() {
            if (this.grump.getTarget() == null) {
                Vector3d vector3d = this.grump.getDeltaMovement();
                this.grump.yRot = -((float) MathHelper.atan2(vector3d.x, vector3d.z)) * (180F / (float)Math.PI);
            } else {
                LivingEntity target = this.grump.getTarget();

                double x = target.getX() - this.grump.getX();
                double z = target.getZ() - this.grump.getZ();
                this.grump.yRot = -((float)MathHelper.atan2(x, z)) * (180F / (float)Math.PI);
            }
            this.grump.yBodyRot = this.grump.yRot;
        }
    }

    private static class MeleeAttackGoal extends Goal {

        final GrumpEntity grump;

        public MeleeAttackGoal(GrumpEntity grump) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.grump = grump;
        }

        private void setWantedPosition(LivingEntity target) {
            Vector3d vector = target.getEyePosition(1.0F).add(0.0D, -(this.grump.getBbHeight() / 1.8), 0.0D);
            this.grump.moveControl.setWantedPosition(vector.x, vector.y, vector.z, 1.0D);
        }

        @Override
        public boolean canUse() {
            return this.grump.getTarget() != null && !this.grump.getMoveControl().hasWanted();
        }

        @Override
        public boolean canContinueToUse() {
            return this.grump.getMoveControl().hasWanted() && this.grump.getTarget() != null && this.grump.getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity target = this.grump.getTarget();

            if (target != null)
                this.setWantedPosition(target);
        }

        @Override
        public void stop() {
        }

        @Override
        public void tick() {
            LivingEntity target = this.grump.getTarget();

            if (this.grump.getBoundingBox().intersects(target.getBoundingBox())) {
                this.grump.doHurtTarget(target);
            }
            else {
                this.setWantedPosition(target);
            }
        }
    }
}
