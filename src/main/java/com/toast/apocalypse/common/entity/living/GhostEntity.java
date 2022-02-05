package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.common.entity.living.goals.MobEntityAttackedByTargetGoal;
import com.toast.apocalypse.common.entity.living.goals.MoonMobPlayerTargetGoal;
import com.toast.apocalypse.common.register.ApocalypseEffects;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;
import java.util.UUID;

/**
 * This is a full moon mob that has the odd ability to completely ignore blocks. To compliment this, it has
 * unlimited aggro range and ignores line of sight.
 * These are the bread and butter of invasions. Ghosts deal light damage that can't be reduced below 1 and apply
 * a short increased gravity effect to help deal with flying players.
 */
public class GhostEntity extends FlyingEntity implements IMob, IFullMoonMob {

    /**
     *  Used to determine if the ghost should be frozen in place
     *  and if the time freeze render effect should be rendered.<br>
     *  <br>
     *  <strong>(Currently unused)</strong>
     */
    private static final DataParameter<Boolean> IS_FROZEN = EntityDataManager.defineId(GhostEntity.class, DataSerializers.BOOLEAN);

    /** The constant player target, if this mob was spawned by the full moon event */
    private UUID playerTargetUUID;
    /** If the ghost should move away from its target in a random direction */
    private boolean isManeuvering;
    /** How long the ghost should be frozen in ticks */
    private int freezeTime = 0;


    public GhostEntity(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new GhostMovementController<>(this);
        this.xpReward = 3;
    }

    public static AttributeModifierMap.MutableAttribute createGhostAttributes() {
        return FlyingEntity.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.FLYING_SPEED, 0.50D)
                .add(ForgeMod.SWIM_SPEED.get(), 1.1F)
                .add(Attributes.FOLLOW_RANGE, Double.POSITIVE_INFINITY);
    }

    public static boolean checkGhostSpawnRules(EntityType<? extends GhostEntity> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && MonsterEntity.isDarkEnoughToSpawn(world, pos, random);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new GhostEntity.ManeuverAttackerGoal<>(this));
        this.goalSelector.addGoal(1, new GhostEntity.MeleeAttackGoal<>(this));
        this.goalSelector.addGoal(2, new RandomFlyGoal(this));
        this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class,8.0F));
        this.targetSelector.addGoal(0, new MobEntityAttackedByTargetGoal(this, IMob.class));
        this.targetSelector.addGoal(1, new MoonMobPlayerTargetGoal<>(this, false));
        this.targetSelector.addGoal(2, new GhostEntity.NearestAttackablePlayerTargetGoal<>(this, PlayerEntity.class));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(IS_FROZEN, false);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize entitySize) {
        return 1.80F;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damage) {
        if (super.hurt(damageSource, damage)) {
            Entity entity = damageSource.getEntity();

            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;

                if (livingEntity.getItemInHand(Hand.MAIN_HAND).getItem() == Items.BEDROCK) {
                    this.freeze();
                    return true;
                }
            }

            if (!this.isFrozen() && entity != null && entity == this.getTarget() && this.random.nextInt(2) == 0) {
                this.setManeuvering(true);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (super.doHurtTarget(entity)) {
            if (entity instanceof PlayerEntity) {
                int duration = this.level.getDifficulty() == Difficulty.HARD ? 140 : 80;
                ((PlayerEntity)entity).addEffect(new EffectInstance(ApocalypseEffects.HEAVY.get(), duration));
            }
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void aiStep() {
        if (this.isAlive()) {
            boolean flag = this.isSunBurnTick();

            if (flag) {
                ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.HEAD);
                if (!itemstack.isEmpty()) {
                    if (itemstack.isDamageableItem()) {
                        itemstack.setDamageValue(itemstack.getDamageValue() + this.random.nextInt(2));
                        if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                            this.broadcastBreakEvent(EquipmentSlotType.HEAD);
                            this.setItemSlot(EquipmentSlotType.HEAD, ItemStack.EMPTY);
                        }
                    }
                    flag = false;
                }
                if (flag) {
                    this.setSecondsOnFire(8);
                }
            }

            if (this.freezeTime > 0) {
                --this.freezeTime;

                if (this.freezeTime <= 0) {
                    this.unfreeze();
                }
            }
        }
        super.aiStep();
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
    public boolean isPushable() {
        return false; // Cannot be pushed by entities
    }

    @Override
    protected void doPush(Entity entity) {
        // Does not push other entities
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true; // Immune to drowning
    }

    @Override
    public boolean isPushedByFluid() {
        return false; // Not pushed by fluids
    }

    @Override
    protected boolean isAffectedByFluids() {
        return false; // Not affected by fluids
    }

    @Override
    protected void lavaHurt() {
        // Immune to lava
    }

    public boolean isManeuvering() {
        return this.isManeuvering;
    }

    protected void setManeuvering(boolean maneuvering) {
        this.isManeuvering = maneuvering;
    }

    protected void setFrozen(boolean frozen, int freezeTime) {
        this.entityData.set(IS_FROZEN, frozen);
        this.freezeTime = freezeTime;
    }

    public boolean isFrozen() {
        return this.entityData.get(IS_FROZEN);
    }

    protected void freeze() {
        this.entityData.set(IS_FROZEN, true);
        this.goalSelector.disableControlFlag(Goal.Flag.MOVE);
        this.goalSelector.disableControlFlag(Goal.Flag.LOOK);
    }

    private void unfreeze() {
        this.entityData.set(IS_FROZEN, false);
        this.goalSelector.enableControlFlag(Goal.Flag.MOVE);
        this.goalSelector.enableControlFlag(Goal.Flag.LOOK);
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

    @Nullable
    @Override
    public UUID getPlayerTargetUUID() {
        return this.playerTargetUUID;
    }

    @Override
    public void setPlayerTargetUUID(UUID playerTargetUUID) {
        this.playerTargetUUID = playerTargetUUID;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);

        if (this.getPlayerTargetUUID() != null) {
            compoundNBT.putUUID(PLAYER_UUID_KEY, this.getPlayerTargetUUID());
        }
        compoundNBT.putInt("FreezeTime", this.freezeTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        if (compoundNBT.hasUUID(PLAYER_UUID_KEY)) {
            this.setPlayerTargetUUID(compoundNBT.getUUID(PLAYER_UUID_KEY));
        }
        if (compoundNBT.contains("FreezeTime", Constants.NBT.TAG_ANY_NUMERIC)) {
            this.freezeTime = compoundNBT.getInt("FreezeTime");

            if (this.freezeTime > 0) {
                this.freeze();
            }
        }
    }

    private static class GhostMovementController<T extends GhostEntity> extends MovementController {

        final T ghostEntity;

        public GhostMovementController(T ghost) {
            super(ghost);
            this.ghostEntity = ghost;
        }

        public void tick() {
            if (this.operation == MovementController.Action.MOVE_TO) {

                T ghost = this.ghostEntity;
                Vector3d vector3d = new Vector3d(this.wantedX - ghost.getX(), this.wantedY - ghost.getY(), this.wantedZ - ghost.getZ());
                double d0 = vector3d.length();

                if (d0 < ghost.getBoundingBox().getSize()) {
                    this.operation = MovementController.Action.WAIT;
                    ghost.setDeltaMovement(ghost.getDeltaMovement().scale(0.5D));
                }
                else {
                    ghost.setDeltaMovement(ghost.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05D / d0)));

                    if (this.ghostEntity.getTarget() == null) {
                        Vector3d velocity = ghost.getDeltaMovement();
                        ghost.yRot = -((float) MathHelper.atan2(velocity.x, velocity.z)) * (180F / (float)Math.PI);
                    }
                    else {
                        double xDist = ghost.getTarget().getX() - ghost.getX();
                        double zDist = ghost.getTarget().getZ() - ghost.getZ();
                        ghost.yRot = -((float)MathHelper.atan2(xDist, zDist)) * (180F / (float)Math.PI);
                    }
                    ghost.yBodyRot = ghost.yRot;
                }
            }
        }
    }

    private static class ManeuverAttackerGoal<T extends GhostEntity> extends Goal {

        private final T ghost;

        public ManeuverAttackerGoal(T ghost) {
            this.ghost = ghost;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.ghost.getTarget() != null && this.ghost.isManeuvering();
        }

        @Override
        public boolean canContinueToUse() {
            return this.ghost.getTarget() != null && this.ghost.getTarget().isAlive() && this.ghost.isManeuvering() && this.ghost.moveControl.hasWanted();
        }

        @Override
        public void start() {
            Random random = this.ghost.getRandom();
            final double speed = this.ghost.getAttributeValue(Attributes.FLYING_SPEED) * 2;
            this.ghost.moveControl.setWantedPosition(this.ghost.getX() + (random.nextGaussian() * 10), this.ghost.getY() + (random.nextGaussian() * 10), this.ghost.getZ() + (random.nextGaussian() * 10), speed);
        }

        @Override
        public void stop() {
            this.ghost.setManeuvering(false);
        }
    }

    private static class NearestAttackablePlayerTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

        public NearestAttackablePlayerTargetGoal(MobEntity mobEntity, Class<T> targetClass) {
            super(mobEntity, targetClass, false, false);
        }

        /**
         * Friggin' large bounding box.
         */
        @Override
        protected AxisAlignedBB getTargetSearchArea(double radius) {
            return this.mob.getBoundingBox().inflate(this.getFollowDistance());
        }
    }

    private static class MeleeAttackGoal<T extends GhostEntity> extends Goal {

        private final T ghost;
        private int ticksUntilNextAttack;

        public MeleeAttackGoal(T ghost) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.ghost = ghost;
        }

        private void setWantedPosition(LivingEntity target) {
            Vector3d vector = target.getEyePosition(1.0F).add(0.0D, -(this.ghost.getBbHeight() / 1.8), 0.0D);
            final double speed = this.ghost.getAttributeValue(Attributes.FLYING_SPEED);
            this.ghost.moveControl.setWantedPosition(vector.x, vector.y, vector.z, speed);
        }

        @Override
        public boolean canUse() {
            return this.ghost.getTarget() != null && this.ghost.getTarget().isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void start() {
            this.ghost.setAggressive(true);
        }

        @Override
        public void stop() {
            this.ghost.setAggressive(false);
            this.ghost.setTarget(null);
        }

        @Override
        public void tick() {
            LivingEntity target = this.ghost.getTarget();
            double distance = this.ghost.distanceToSqr(target);

            if (!this.ghost.isManeuvering()) {
                this.setWantedPosition(target);
            }

            if (this.ticksUntilNextAttack <= 0) {
                if (canAttackReach(target, distance)) {
                    this.ghost.doHurtTarget(target);
                    this.ticksUntilNextAttack = 20;
                }
            }
            else {
                --this.ticksUntilNextAttack;
            }
        }

        private boolean canAttackReach(LivingEntity target, double distance) {
            return distance <= (double)(this.ghost.getBbWidth() * 2.0F * this.ghost.getBbWidth() * 2.0F + target.getBbWidth());
        }
    }

    /** Copied from ghast */
    static class RandomFlyGoal extends Goal {

        private final GhostEntity ghost;

        public RandomFlyGoal(GhostEntity ghost) {
            this.ghost = ghost;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            MovementController movementcontroller = this.ghost.getMoveControl();

            if (this.ghost.getTarget() != null || this.ghost.isManeuvering())
                return false;

            if (!movementcontroller.hasWanted()) {
                return true;
            }
            else {
                double x = movementcontroller.getWantedX() - this.ghost.getX();
                double y = movementcontroller.getWantedY() - this.ghost.getY();
                double z = movementcontroller.getWantedZ() - this.ghost.getZ();
                double d3 = x * x + y * y + z * z;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        @Override
        public void stop() {
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            Random random = this.ghost.getRandom();
            final double x = this.ghost.getX() + (double)((random.nextFloat() * 2.0F - 1.0F) * 8.0F);
            final double y = this.ghost.getY() + (double)((random.nextFloat() * 2.0F - 1.0F) * 8.0F);
            final double z = this.ghost.getZ() + (double)((random.nextFloat() * 2.0F - 1.0F) * 8.0F);
            final double speed = this.ghost.getAttributeValue(Attributes.FLYING_SPEED);
            this.ghost.getMoveControl().setWantedPosition(x, y, z, speed);
        }
    }
}
