package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.common.entity.IFullMoonMob;
import com.toast.apocalypse.common.entity.living.goals.MobEntityAttackedByTargetGoal;
import com.toast.apocalypse.common.entity.projectile.DestroyerFireballEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Random;

/**
 * This is a full moon mob similar to a ghast, though it has unlimited aggro range ignoring line of sight and
 * its fireballs can destroy anything within a small area.
 */
public class DestroyerEntity extends AbstractFullMoonGhastEntity {

    public DestroyerEntity(EntityType<? extends GhastEntity> entityType, World world) {
        super(entityType, world);
        this.xpReward = 5;
    }

    public static AttributeModifierMap.MutableAttribute createDestroyerAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.FOLLOW_RANGE, Double.POSITIVE_INFINITY);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new DestroyerEntity.FireballAttackGoal(this));
        this.goalSelector.addGoal(0, new DestroyerEntity.LookAroundGoal(this));
        this.goalSelector.addGoal(1, new DestroyerEntity.RandomOrRelativeToTargetFlyGoal(this));
        this.targetSelector.addGoal(0, new DestroyerEntity.DestroyerNearestAttackableTargetGoal<>(this, PlayerEntity.class));
        this.targetSelector.addGoal(1, new MobEntityAttackedByTargetGoal(this, IFullMoonMob.class));

    }

    public static boolean checkDestroyerSpawnRules(EntityType<? extends DestroyerEntity> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && MobEntity.checkMobSpawnRules(entityType, world, spawnReason, pos, random);
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
    public boolean hurt(DamageSource damageSource, float damage) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        // Prevent instant fireball death and return to sender advancement
        else if (damageSource.getDirectEntity() instanceof AbstractFireballEntity) {
            // Prevent the destroyer from damaging itself
            // when close up to a wall or solid obstacle
            if (damageSource.getEntity() == this)
                return false;

            if (damageSource.getEntity() instanceof PlayerEntity) {
                super.hurt(DamageSource.playerAttack((PlayerEntity) damageSource.getEntity()), this.getMaxHealth() / 2.0F);
                return true;
            }
        }
        else if (damageSource.isExplosion() && damageSource.getEntity() == this) {
            return false;
        }

        return super.hurt(damageSource, damage);
    }

    private static class DestroyerNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

        public DestroyerNearestAttackableTargetGoal(MobEntity entity, Class<T> targetClass) {
            super(entity, targetClass, false, false);
        }

        /** Friggin' large bounding box */
        protected AxisAlignedBB getTargetSearchArea(double followRange) {
            return this.mob.getBoundingBox().inflate(followRange, followRange, followRange);
        }
    }

    /** Essentially a copy of the ghast's fireball goal */
    private static class FireballAttackGoal extends Goal {

        private final DestroyerEntity destroyer;
        public int chargeTime;

        public FireballAttackGoal(DestroyerEntity destroyer) {
            this.destroyer = destroyer;
        }

        @Override
        public boolean canUse() {
            return this.destroyer.getTarget() != null;
        }

        @Override
        public void start() {
            this.chargeTime = 0;
        }

        @Override
        public void stop() {
            this.destroyer.setCharging(false);
        }

        @Override
        public void tick() {
            LivingEntity target = this.destroyer.getTarget();

            if (this.destroyer.horizontalDistanceToSqr(target) < 4096.0D && this.destroyer.canSee(target)) {
                World world = this.destroyer.level;
                ++this.chargeTime;
                if (this.chargeTime == 10 && !this.destroyer.isSilent()) {
                    world.levelEvent(null, 1015, this.destroyer.blockPosition(), 0);
                }

                if (this.chargeTime == 20) {
                    Vector3d vector3d = this.destroyer.getViewVector(1.0F);
                    double x = target.getX() - (this.destroyer.getX() + vector3d.x * 4.0D);
                    double y = target.getY(0.5D) - (0.5D + this.destroyer.getY(0.5D));
                    double z = target.getZ() - (this.destroyer.getZ() + vector3d.z * 4.0D);

                    if (!this.destroyer.isSilent()) {
                        world.levelEvent(null, 1016, this.destroyer.blockPosition(), 0);
                    }
                    DestroyerFireballEntity fireball = new DestroyerFireballEntity(world, this.destroyer, x, y, z);
                    fireball.setPos(this.destroyer.getX() + vector3d.x * 4.0D, this.destroyer.getY(0.5D) + 0.5D, fireball.getZ() + vector3d.z * 4.0D);
                    world.addFreshEntity(fireball);
                    this.chargeTime = -40;
                }
            }
            else if (this.chargeTime > 0) {
                --this.chargeTime;
            }
            this.destroyer.setCharging(this.chargeTime > 10);
        }
    }

    /** Copied from ghast */
    static class LookAroundGoal extends Goal {
        private final DestroyerEntity destroyer;

        public LookAroundGoal(DestroyerEntity destroyer) {
            this.destroyer = destroyer;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        public void tick() {
            if (this.destroyer.getTarget() == null) {
                Vector3d vector3d = this.destroyer.getDeltaMovement();
                this.destroyer.yRot = -((float) MathHelper.atan2(vector3d.x, vector3d.z)) * (180F / (float)Math.PI);
            } else {
                LivingEntity target = this.destroyer.getTarget();

                double x = target.getX() - this.destroyer.getX();
                double z = target.getZ() - this.destroyer.getZ();
                this.destroyer.yRot = -((float)MathHelper.atan2(x, z)) * (180F / (float)Math.PI);
            }
            this.destroyer.yBodyRot = this.destroyer.yRot;
        }
    }

    static class RandomOrRelativeToTargetFlyGoal extends Goal {

        private static final double maxDistanceBeforeFollow = 1400.0D;
        private final DestroyerEntity destroyer;

        public RandomOrRelativeToTargetFlyGoal(DestroyerEntity destroyer) {
            this.destroyer = destroyer;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            MovementController controller = this.destroyer.getMoveControl();

            if (!controller.hasWanted()) {
                return true;
            }
            else {
                double x = controller.getWantedX() - this.destroyer.getX();
                double y = controller.getWantedY() - this.destroyer.getY();
                double z = controller.getWantedZ() - this.destroyer.getZ();
                double d3 = x * x + y * y + z * z;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        private void setRandomWantedPosition() {
            Random random = this.destroyer.getRandom();
            double x = this.destroyer.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double y = this.destroyer.getY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 10.0F);
            double z = this.destroyer.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.destroyer.getMoveControl().setWantedPosition(x, y, z, 1.0D);
        }

        @Override
        public void start() {
            MoveHelperController controller = this.destroyer.getMoveHelperController();

            if (this.destroyer.getTarget() != null) {
                LivingEntity target = this.destroyer.getTarget();
                boolean canSeeDirectly = this.destroyer.canSeeDirectly(target);
                double distanceToTarget = this.destroyer.distanceToSqr(target);

                if (distanceToTarget > maxDistanceBeforeFollow) {
                    if (!controller.canReachCurrentWanted()) {
                        this.setRandomWantedPosition();
                    }
                    else {
                        controller.setWantedPosition(target.getX(), target.getY() + 10.0D, target.getZ(), 1.0D);
                    }
                }
                else {
                    this.setRandomWantedPosition();
                }
            }
            else {
                this.setRandomWantedPosition();
            }
        }
    }
}
