package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.api.impl.SeekerAlertRegister;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.IFullMoonMob;
import com.toast.apocalypse.common.entity.living.goals.MobEntityAttackedByTargetGoal;
import com.toast.apocalypse.common.entity.projectile.DestroyerFireballEntity;
import com.toast.apocalypse.common.entity.projectile.SeekerFireballEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * This is a full moon mob whose entire goal in life is to break through your defenses. It is similar to a ghast, only
 * it has unlimited target range that ignores line of sight and shoots creeper-sized fireballs when it does not have a
 * clear line of sight. When it does have direct vision, it shoots much weaker fireballs that can be easily reflected
 * back at the seeker. The seeker also alerts nearby monsters of the player's whereabouts when in it's direct line of sight.
 */
public class SeekerEntity extends GhastEntity implements IFullMoonMob {

    private static final DataParameter<Boolean> ALERTING = EntityDataManager.defineId(SeekerEntity.class, DataSerializers.BOOLEAN);
    private static final BiPredicate<LivingEntity, MobEntity> ALERT_PREDICATE = (livingEntity, seeker) -> !(livingEntity instanceof IFullMoonMob) && seeker.getTarget() != livingEntity;

    private int nextTimeAlerting;

    public SeekerEntity(EntityType<? extends GhastEntity> entityType, World world) {
        super(entityType, world);
        this.xpReward = 5;
    }

    public static AttributeModifierMap.MutableAttribute createSeekerAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.FOLLOW_RANGE, Double.POSITIVE_INFINITY);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SeekerEntity.FireballAttackGoal(this));
        this.goalSelector.addGoal(0, new SeekerEntity.LookAroundGoal(this));
        this.goalSelector.addGoal(1, new SeekerEntity.RandomOrRelativeToTargetFlyGoal(this));
        this.goalSelector.addGoal(2, new SeekerEntity.AlertOtherMonstersGoal(this));
        this.targetSelector.addGoal(0, new SeekerEntity.SeekerNearestAttackableTargetGoal<>(this, PlayerEntity.class));
        this.targetSelector.addGoal(1, new MobEntityAttackedByTargetGoal(this, IFullMoonMob.class));
    }

    public static boolean checkSeekerSpawnRules(EntityType<? extends SeekerEntity> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && MobEntity.checkMobSpawnRules(entityType, world, spawnReason, pos, random);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ALERTING, false);
    }

    public boolean canAlert() {
        return this.nextTimeAlerting <= 0 && this.getTarget() != null && !this.isCharging() && !this.isAlerting() && this.canSeeDirectly(this, this.getTarget());
    }

    public boolean isAlerting() {
        return this.entityData.get(ALERTING);
    }

    private void setAlerting(boolean alerting) {
        this.entityData.set(ALERTING, alerting);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.nextTimeAlerting > 0)
            --this.nextTimeAlerting;
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

    /**
     * Completely ignore line of sight; the target
     * is always "visible"
     */
    @Override
    public boolean canSee(Entity entity) {
        return true;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true; // Immune to drowning
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public int getExplosionPower() {
        return 3;
    }

    public void setNextTimeAlerting(int time) {
        this.nextTimeAlerting = time;
    }

    private static class SeekerNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

        public SeekerNearestAttackableTargetGoal(MobEntity entity, Class<T> targetClass) {
            super(entity, targetClass, false, false);
        }

        /** Friggin' large bounding box */
        protected AxisAlignedBB getTargetSearchArea(double followRange) {
            return this.mob.getBoundingBox().inflate(followRange, followRange, followRange);
        }
    }

    /** Essentially a copy of the ghast's fireball goal */
    private static class FireballAttackGoal extends Goal {

        private final SeekerEntity seeker;
        public int chargeTime;

        public FireballAttackGoal(SeekerEntity seeker) {
            this.seeker = seeker;
        }

        @Override
        public boolean canUse() {
            return this.seeker.getTarget() != null && !this.seeker.isAlerting();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.seeker.isAlerting() && this.seeker.getTarget() != null;
        }

        @Override
        public void start() {
            this.chargeTime = 0;
        }

        @Override
        public void stop() {
            this.seeker.setCharging(false);
        }

        @Override
        public void tick() {
            LivingEntity target = this.seeker.getTarget();

            if (target.distanceToSqr(this.seeker) < 4096.0D) {
                World world = this.seeker.level;
                ++this.chargeTime;
                if (this.chargeTime == 10 && !this.seeker.isSilent()) {
                    world.levelEvent(null, 1015, this.seeker.blockPosition(), 0);
                }

                if (this.chargeTime == 20) {
                    Vector3d vector3d = this.seeker.getViewVector(1.0F);
                    double x = target.getX() - (this.seeker.getX() + vector3d.x * 4.0D);
                    double y = target.getY(0.5D) - (0.5D + this.seeker.getY(0.5D));
                    double z = target.getZ() - (this.seeker.getZ() + vector3d.z * 4.0D);

                    if (!this.seeker.isSilent()) {
                        world.levelEvent(null, 1016, this.seeker.blockPosition(), 0);
                    }
                    boolean canSeeTarget = this.seeker.canSeeDirectly(this.seeker, target);
                    SeekerFireballEntity fireball = new SeekerFireballEntity(world, this.seeker, canSeeTarget, x, y, z);
                    fireball.setPos(this.seeker.getX() + vector3d.x * 4.0D, this.seeker.getY(0.5D) + 0.2D, fireball.getZ() + vector3d.z * 4.0D);
                    world.addFreshEntity(fireball);

                    this.chargeTime = -40;
                }
            }
            else if (this.chargeTime > 0) {
                --this.chargeTime;
            }
            this.seeker.setCharging(this.chargeTime > 10);
        }
    }

    /** Copied from ghast */
    static class LookAroundGoal extends Goal {
        private final SeekerEntity seeker;

        public LookAroundGoal(SeekerEntity seeker) {
            this.seeker = seeker;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        public void tick() {
            if (this.seeker.getTarget() == null) {
                Vector3d vector3d = this.seeker.getDeltaMovement();
                this.seeker.yRot = -((float) MathHelper.atan2(vector3d.x, vector3d.z)) * (180F / (float)Math.PI);
            } else {
                LivingEntity target = this.seeker.getTarget();

                double x = target.getX() - this.seeker.getX();
                double z = target.getZ() - this.seeker.getZ();
                this.seeker.yRot = -((float)MathHelper.atan2(x, z)) * (180F / (float)Math.PI);
            }
            this.seeker.yBodyRot = this.seeker.yRot;
        }
    }

    static class RandomOrRelativeToTargetFlyGoal extends Goal {

        private static final double maxDistanceBeforeFollow = 1400.0D;
        private final SeekerEntity seeker;

        public RandomOrRelativeToTargetFlyGoal(SeekerEntity seeker) {
            this.seeker = seeker;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            MovementController controller = this.seeker.getMoveControl();

            if (!controller.hasWanted()) {
                return true;
            }
            else {
                double x = controller.getWantedX() - this.seeker.getX();
                double y = controller.getWantedY() - this.seeker.getY();
                double z = controller.getWantedZ() - this.seeker.getZ();
                double d3 = x * x + y * y + z * z;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        private void setRandomWantedPosition() {
            Random random = this.seeker.getRandom();
            double x = this.seeker.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            // Don't want the destroyer moving too much on the Y axis
            // in case it just decides to vanish into space.
            double y = this.seeker.getY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 6.0F);
            double z = this.seeker.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.seeker.getMoveControl().setWantedPosition(x, y, z, 1.0D);
        }

        @Override
        public void start() {
            if (this.seeker.getTarget() != null) {
                LivingEntity target = this.seeker.getTarget();
                double distanceToTarget = this.seeker.distanceToSqr(target);

                if (distanceToTarget > maxDistanceBeforeFollow) {
                    this.seeker.moveControl.setWantedPosition(target.getX(), target.getY() + 10.0D, target.getZ(), 1.0D);
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

    private static class AlertOtherMonstersGoal extends Goal {

        private final SeekerEntity seeker;
        private int timeAlerting;

        public AlertOtherMonstersGoal(SeekerEntity seeker) {
            this.seeker = seeker;
        }

        @Override
        public boolean canUse() {
            return this.seeker.canAlert();
        }

        @Override
        public boolean canContinueToUse() {
            return this.timeAlerting < 0;
        }

        @Override
        public void start() {
            SeekerAlertRegister alertRegister = Apocalypse.INSTANCE.getRegistryHelper().getAlertRegister();
            LivingEntity target = this.seeker.getTarget();
            this.timeAlerting = -60;

            if (target != null) {
                AxisAlignedBB searchBox = target.getBoundingBox().inflate(60.0D, 30.0D, 60.0D);
                List<LivingEntity> toAlert = this.seeker.level.getLoadedEntitiesOfClass(LivingEntity.class, searchBox, (entity) -> ALERT_PREDICATE.test(entity, this.seeker));

                if (toAlert.isEmpty())
                    return;

                for (LivingEntity livingEntity : toAlert) {
                    Class<? extends LivingEntity> entityClass = livingEntity.getClass();

                    if (alertRegister.containsEntry(entityClass)) {
                        alertRegister.getFromEntity(entityClass).accept(livingEntity, target, this.seeker);
                        return;
                    }
                    else {
                        if (livingEntity instanceof MobEntity && livingEntity instanceof IMob) {
                            MobEntity mobEntity = (MobEntity) livingEntity;

                            if (mobEntity.getTarget() != this.seeker.getTarget()) {
                                mobEntity.setLastHurtByMob(null);
                                mobEntity.setTarget(this.seeker.getTarget());
                                mobEntity.getNavigation().moveTo(target, 1.0D);
                            }
                        }
                    }
                }
            }
            this.seeker.setAlerting(true);
            this.seeker.playSound(SoundEvents.GHAST_SCREAM, 5.0F, 0.6F);
        }

        @Override
        public void stop() {
            this.seeker.setNextTimeAlerting(600);
            this.timeAlerting = 0;
            this.seeker.setAlerting(false);
        }

        @Override
        public void tick() {
            ++this.timeAlerting;
        }
    }
}
