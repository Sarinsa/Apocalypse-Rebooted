package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.entity.living.ai.MobEntityAttackedByTargetGoal;
import com.toast.apocalypse.common.entity.living.ai.MoonMobPlayerTargetGoal;
import com.toast.apocalypse.common.entity.projectile.DestroyerFireballEntity;
import com.toast.apocalypse.common.entity.projectile.SeekerFireballEntity;
import com.toast.apocalypse.common.util.ApocalypseEventFactory;
import com.toast.apocalypse.common.util.MobHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;

/**
 * This is a full moon mob whose entire goal in life is to break through your defenses. It is similar to a ghast, only
 * it has unlimited target range that ignores line of sight and shoots creeper-sized fireballs when it does not have a
 * clear line of sight. When it does have direct vision, it shoots much weaker fireballs that can be easily reflected
 * back at the seeker. The seeker also alerts nearby monsters of the player's whereabouts when in it's direct line of sight.
 */
public class SeekerEntity extends AbstractFullMoonGhastEntity {

    private static final DataParameter<Boolean> ALERTING = EntityDataManager.defineId(SeekerEntity.class, DataSerializers.BOOLEAN);
    private static final BiPredicate<MobEntity, SeekerEntity> ALERT_PREDICATE = (mob, seeker) -> !(mob instanceof IFullMoonMob) && mob instanceof IMob;

    /** The seeker's current target. Updated when the seeker alerts nearby mobs. */
    private LivingEntity currentTarget;

    public SeekerEntity(EntityType<? extends GhastEntity> entityType, World world) {
        super(entityType, world);
        this.xpReward = 5;
    }

    public static AttributeModifierMap.MutableAttribute createSeekerAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.FOLLOW_RANGE, Double.POSITIVE_INFINITY)
                .add(ForgeMod.SWIM_SPEED.get(), 1.1D);
    }

    public static boolean checkSeekerSpawnRules(EntityType<? extends SeekerEntity> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && MobEntity.checkMobSpawnRules(entityType, world, spawnReason, pos, random);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SeekerEntity.AlertOtherMonstersGoal(this));
        this.goalSelector.addGoal(1, new SeekerEntity.FireballAttackGoal(this));
        this.goalSelector.addGoal(2, new LookAroundGoal(this));
        this.goalSelector.addGoal(2, new SeekerEntity.RandomOrRelativeToTargetFlyGoal(this));
        this.targetSelector.addGoal(0, new MobEntityAttackedByTargetGoal(this, IMob.class));
        this.targetSelector.addGoal(1, new MoonMobPlayerTargetGoal<>(this, false));
        this.targetSelector.addGoal(2, new SeekerNearestAttackableTargetGoal<>(this, PlayerEntity.class));
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ALERTING, false);
    }

    public boolean isAlerting() {
        return this.entityData.get(ALERTING);
    }

    private void setAlerting(boolean alerting) {
        this.entityData.set(ALERTING, alerting);
    }

    // Essentially just an inverted ghast.isCharging() check,
    // but that method is client side only... so here we go!
    private boolean canAlert() {
        return !this.entityData.get(DATA_IS_CHARGING);
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
        else if (damageSource.getDirectEntity() instanceof SeekerFireballEntity || damageSource.getDirectEntity() instanceof DestroyerFireballEntity) {

            if (damageSource.getEntity() == this) {
                return false;
            }
            else {
                return super.hurt(damageSource, damage);
            }
        }
        else if (damageSource.isExplosion() && damageSource.getEntity() == this) {
            return false;
        }
        return super.hurt(damageSource, damage);
    }

    @Override
    public int getExplosionPower() {
        return this.explosionPower == 0 ? ApocalypseCommonConfig.COMMON.getSeekerExplosionPower() : this.explosionPower;
    }

    @Override
    @Nullable
    public ILivingEntityData finalizeSpawn(IServerWorld serverWorld, DifficultyInstance difficultyInstance, SpawnReason spawnReason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        data = super.finalizeSpawn(serverWorld, difficultyInstance, spawnReason, data, compoundNBT);

        if (compoundNBT != null && compoundNBT.contains("ExplosionPower", Constants.NBT.TAG_ANY_NUMERIC)) {
            this.explosionPower = compoundNBT.getInt("ExplosionPower");
        }
        else {
            this.explosionPower = 0;
        }
        return data;
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
            if (this.seeker.getTarget() != null) {
                return !this.seeker.isAlerting();
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
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

            if (this.seeker.horizontalDistanceToSqr(target) < 4096.0D) {
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
                    boolean canSeeTarget = this.seeker.canSeeDirectly(target);
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

    static class RandomOrRelativeToTargetFlyGoal extends Goal {

        private static final double maxDistanceBeforeFollow = 3000.0D;
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
            double x = seeker.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double y = seeker.getY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 6.0F);
            double z = seeker.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.seeker.getMoveControl().setWantedPosition(x, y, z, 1.0D);
        }

        @Override
        public void start() {
            if (this.seeker.getTarget() != null) {
                LivingEntity target = seeker.getTarget();
                double distanceToTarget = seeker.distanceToSqr(target);

                if (distanceToTarget > maxDistanceBeforeFollow) {
                    seeker.moveControl.setWantedPosition(target.getX(), target.getY() + 10.0D, target.getZ(), 1.0D);
                }
                else {
                    setRandomWantedPosition();
                }
            }
            else {
                setRandomWantedPosition();
            }
        }
    }

    public static class AlertOtherMonstersGoal extends Goal {

        private static final int maxAlertCount = 25;

        private final SeekerEntity seeker;
        private int timeAlerting;

        public AlertOtherMonstersGoal(SeekerEntity seeker) {
            this.seeker = seeker;
        }

        @Override
        public boolean canUse() {
            if (seeker.getTarget() != null) {
                return seeker.canAlert()
                        && seeker.distanceToSqr(seeker.getTarget()) < 4096.0D
                        && seeker.canSeeDirectly(seeker.getTarget())
                        && seeker.currentTarget != seeker.getTarget();
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return timeAlerting < 0;
        }

        @Override
        @SuppressWarnings("all")
        public void start() {
            LivingEntity target = seeker.getTarget();
            timeAlerting = -60;

            if (target != null) {
                AxisAlignedBB searchBox = target.getBoundingBox().inflate(60.0D, 30.0D, 60.0D);
                List<MobEntity> toAlert = MobHelper.getLoadedEntitiesCapped(MobEntity.class, seeker.level, searchBox, (entity) -> ALERT_PREDICATE.test(entity, seeker), maxAlertCount);

                if (toAlert.isEmpty()) {
                    // No need to perform further checks if the list is empty
                    timeAlerting = 0;
                    return;
                }
                ApocalypseEventFactory.fireSeekerAlertEvent(seeker.level, seeker, toAlert, target);

                for (MobEntity mob : toAlert) {
                    if (mob.getTarget() != target) {
                        mob.setLastHurtByMob(null);
                        mob.setTarget(target);
                        ModifiableAttributeInstance attributeInstance = mob.getAttribute(Attributes.FOLLOW_RANGE);
                        attributeInstance.setBaseValue(Math.max(attributeInstance.getValue(), 60.0D));
                    }
                }
                seeker.currentTarget = seeker.getTarget();
                seeker.setAlerting(true);
                seeker.playSound(SoundEvents.GHAST_SCREAM, 5.0F, 0.6F);
            }
        }

        @Override
        public void stop() {
            timeAlerting = 0;
            seeker.setAlerting(false);
        }

        @Override
        public void tick() {
            ++timeAlerting;
        }
    }
}
