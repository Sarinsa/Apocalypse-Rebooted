package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.common.entity.living.ai.SimpleFlyingMoveController;
import com.toast.apocalypse.common.misc.ApocalypseDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class Shadefiend extends FlyingMob implements Enemy {

    protected static final EntityDataAccessor<Boolean> IS_IN_LIGHT = SynchedEntityData.defineId(Shadefiend.class, EntityDataSerializers.BOOLEAN);


    public Shadefiend(EntityType<? extends Shadefiend> type, Level level) {
        super(type, level);
        moveControl = new ShadefiendMoveControl(this);
        lookControl = new ShadefiendLookControl(this);
    }

    public static boolean checkShadefiendSpawnRules(EntityType<? extends Shadefiend> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(IS_IN_LIGHT, false);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new Shadefiend.MeleeAttackGoal(this));
        targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    public boolean isInLight() {
        return entityData.get(IS_IN_LIGHT);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            boolean hasTarget = getTarget() != null;
            float flapTickOffset = getId() * 3;

            float f = Mth.cos((flapTickOffset + tickCount) * 7.448451F * ((float) Math.PI / 180F) + (float) Math.PI);
            float f1 = Mth.cos((flapTickOffset + tickCount + 1) * 7.448451F * ((float) Math.PI / 180F) + (float) Math.PI);

            if (hasTarget && f > 0.0F && f1 <= 0.0F) {
                level().playLocalSound(
                        getX(), getY(), getZ(),
                        SoundEvents.PHANTOM_FLAP,
                        getSoundSource(),
                        0.95F + random.nextFloat() * 0.05F,
                        0.95F + random.nextFloat() * 0.05F,
                        false
                );
            }
            float xOffset = Mth.cos(getYRot() * ((float) Math.PI / 180F)) * (1.3F + 0.21F);
            float zOffset = Mth.sin(getYRot() * ((float) Math.PI / 180F)) * (1.3F + 0.21F);
            float yOffset = hasTarget ? (0.3F + f * 0.45F) * 1.2F : 0.3F;

            level().addParticle(
                    ParticleTypes.SMOKE,
                    getX() + (double) xOffset,
                    getY() + (double) yOffset,
                    getZ() + (double) zOffset,
                    0.0D, 0.0D, 0.0D
            );
            level().addParticle(ParticleTypes.SMOKE,
                    getX() - (double) xOffset,
                    getY() + (double) yOffset,
                    getZ() - (double) zOffset,
                    0.0D, 0.0D, 0.0D
            );
        }
        boolean inHarmfulLight = (level().getBrightness(LightLayer.BLOCK, blockPosition()) > 7)
                || (level().isDay() && level().getBrightness(LightLayer.SKY, blockPosition()) > 7);
        if (!level().isClientSide) {
            entityData.set(IS_IN_LIGHT, inHarmfulLight);
        }

        if (inHarmfulLight)  {
            hurt(ApocalypseDamageSources.of(level(), ApocalypseDamageSources.LIGHT_INTOLERANCE), 2);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (super.doHurtTarget(target)) {
            if (target instanceof LivingEntity livingEntity) {
                int effectDuration = 40;

                if (level().getDifficulty() == Difficulty.HARD) {
                    effectDuration = 80;
                }
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, effectDuration, 0), this);
            }
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.35F;
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new ShadefiendBodyRotationControl(this);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    public void playAmbientSound() {
        if (getTarget() != null) {
            super.playAmbientSound();
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PHANTOM_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }

    class ShadefiendBodyRotationControl extends BodyRotationControl {
        public ShadefiendBodyRotationControl(Mob mob) {
            super(mob);
        }

        @Override
        public void clientTick() {
            Shadefiend.this.yHeadRot = Shadefiend.this.yBodyRot;
            Shadefiend.this.yBodyRot = Shadefiend.this.getYRot();
        }
    }

    static class ShadefiendLookControl extends LookControl {

        public ShadefiendLookControl(Mob mob) {
            super(mob);
        }

        @Override
        public void tick() {
        }
    }

    static class ShadefiendMoveControl extends SimpleFlyingMoveController {

        public ShadefiendMoveControl(FlyingMob mob) {
            super(mob);
        }

        @Override
        public void tick() {
            if (operation == Operation.MOVE_TO) {
                if (floatDuration-- <= 0) {
                    floatDuration += mob.getRandom().nextInt(5) + 2;
                    Vec3 moveVec = new Vec3(
                            wantedX - mob.getX(),
                            wantedY - mob.getY(),
                            wantedZ - mob.getZ() );
                    final int distance = Mth.ceil( moveVec.length() );
                    moveVec = moveVec.normalize();

                    if (!isNearWanted()) {
                        if (mob.getRandom().nextBoolean() || canReach(moveVec, distance)) {
                            mob.setDeltaMovement(mob.getDeltaMovement().add(moveVec.scale(getScaledMoveSpeed())));
                        }
                    }
                    else {
                        operation = Operation.WAIT;
                    }
                }
            }
            if (mob.getTarget() != null) {
                updateRotations();
            }
        }

        private boolean isNearWanted() {
            return hasWanted() && mob.distanceToSqr(
                            getWantedX(),
                            getWantedY(),
                            getWantedZ()) < 1.0;
        }

        private void updateRotations() {
            double x = wantedX - mob.getX();
            double y = wantedY - mob.getY();
            double z = wantedZ - mob.getZ();
            double xzSqrRoot = Math.sqrt(x * x + z * z);

            if (Math.abs(xzSqrRoot) > (double) 1.0E-5F) {
                double offset = 1.0D - Math.abs(y * (double) 0.7F) / xzSqrRoot;
                x *= offset;
                z *= offset;
                xzSqrRoot = Math.sqrt(x * x + z * z);
                float horizontalAngle = (float) Mth.atan2(z, x);
                float yDegrees = Mth.wrapDegrees(mob.getYRot() + 90.0F);
                float xzDegrees = Mth.wrapDegrees(horizontalAngle * (180F / (float) Math.PI));
                mob.setYRot(Mth.approachDegrees(yDegrees, xzDegrees, 4.0F) - 90.0F);
                mob.yBodyRot = mob.getYRot();

                float xRot = (float)(-(Mth.atan2(-y, xzSqrRoot) * (double)(180F / (float) Math.PI)));
                mob.setXRot(xRot);
            }
        }
    }

    private static class MeleeAttackGoal extends Goal {

        final Shadefiend shadefiend;

        public MeleeAttackGoal(Shadefiend shadefiend) {
            setFlags(EnumSet.of(Flag.MOVE));
            this.shadefiend = shadefiend;
        }

        private void setWantedPosition(LivingEntity target) {
            Vec3 vec3 = target.getEyePosition(1.0F).add(0.0D, -(shadefiend.getBbHeight() / 2), 0.0D);
            shadefiend.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
        }

        @Override
        public boolean canUse() {
            LivingEntity target = shadefiend.getTarget();
            return shadefiend.isAlive() && target != null && shadefiend.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = shadefiend.getTarget();

            if (shadefiend.isAlive() && !shadefiend.isVehicle() && target != null && target.isAlive()) {
                return ((SimpleFlyingMoveController) shadefiend.moveControl).canReachCurrentWanted();
            }
            return false;
        }

        @Override
        public void start() {
            shadefiend.setAggressive(true);
            LivingEntity target = shadefiend.getTarget();

            if (target != null) {
                setWantedPosition(target);
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            shadefiend.setAggressive(false);
            shadefiend.setTarget(null);
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public void tick() {
            LivingEntity target = shadefiend.getTarget();

            // Just in case
            if (target == null) return;

            if (shadefiend.getBoundingBox().inflate(0.3F).intersects(target.getBoundingBox())) {
                shadefiend.doHurtTarget(target);
            }
            else {
                if ((shadefiend.tickCount & 20) == 0) {
                    setWantedPosition(target);
                }
            }
        }
    }
}
