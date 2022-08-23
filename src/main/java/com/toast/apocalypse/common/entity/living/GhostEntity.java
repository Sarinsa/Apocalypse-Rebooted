package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.common.core.register.ApocalypseEffects;
import com.toast.apocalypse.common.core.register.ApocalypseSounds;
import com.toast.apocalypse.common.entity.living.goals.MobEntityAttackedByTargetGoal;
import com.toast.apocalypse.common.entity.living.goals.MoonMobPlayerTargetGoal;
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
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;
import java.util.UUID;

/**
 * This is a full moon mob that has the odd ability to completely ignore block collision. To compliment this, it has
 * unlimited aggro range and ignores line of sight.
 * These are the bread and butter of invasions. Ghosts deal light damage that can't be reduced below 1 and apply
 * a short increased gravity effect to help deal with flying players.
 *
 * The ghost will also occasionally maneuver away if damaged, potentially phasing
 * through walls and disorienting the target.
 *
 * //TODO - "Freezing Counter": Provide the player with a gadget for temporarily
 *          immobilizing/freezing ghosts in place.
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
        this.goalSelector.addGoal(3, new GhostLookAtGoal(this, PlayerEntity.class,8.0F));
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
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize entitySize) {
        return 1.80F;
    }

    @Override
    public void knockback(float strength, double xRatio, double zRatio) {
        if (!isFrozen())
            super.knockback(strength, xRatio, zRatio);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damage) {
        Entity entity = damageSource.getEntity();

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;

            if (!isFrozen() && livingEntity.getItemInHand(Hand.MAIN_HAND).getItem() == Items.BEDROCK) {
                freeze(200);

                if (!level.isClientSide) {
                    playSound(ApocalypseSounds.GHOST_FREEZE.get(), 1.0F, 1.0F - (random.nextFloat() / 5));
                }
            }
        }
        if (super.hurt(damageSource, damage)) {
            if (!isFrozen() && entity != null && entity == getTarget() && random.nextInt(2) == 0) {
                setManeuvering(true);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (super.doHurtTarget(entity)) {
            if (entity instanceof PlayerEntity) {
                int duration = level.getDifficulty() == Difficulty.HARD ? 140 : 80;
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
        if (isAlive()) {
            boolean flag = isSunBurnTick();

            if (flag) {
                ItemStack itemstack = getItemBySlot(EquipmentSlotType.HEAD);
                if (!itemstack.isEmpty()) {
                    if (itemstack.isDamageableItem()) {
                        itemstack.setDamageValue(itemstack.getDamageValue() + random.nextInt(2));
                        if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                            broadcastBreakEvent(EquipmentSlotType.HEAD);
                            setItemSlot(EquipmentSlotType.HEAD, ItemStack.EMPTY);
                        }
                    }
                    flag = false;
                }
                if (flag) {
                    setSecondsOnFire(8);
                }
            }
            if (!level.isClientSide) {
                if (freezeTime > 0) {
                    --freezeTime;

                    if (freezeTime <= 0 && isFrozen()) {
                        unfreeze();
                    }
                }
            }
        }
        super.aiStep();
    }

    @Override
    public SoundCategory getSoundSource() {
        return SoundCategory.HOSTILE;
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

    public boolean isFrozen() {
        return this.entityData.get(IS_FROZEN);
    }

    protected void freeze(int freezeTime) {
        this.entityData.set(IS_FROZEN, true);
        this.freezeTime = freezeTime;

        if (level != null && !level.isClientSide) {
            level.broadcastEntityEvent(this, (byte) 7);
        }
    }

    private void unfreeze() {
        this.entityData.set(IS_FROZEN, false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte eventId) {
        if (eventId == 7) {
            displayFreezeParticles();
        }
        else {
            super.handleEntityEvent(eventId);
        }
    }

    private void displayFreezeParticles() {
        for (int i = 0; i < 13; i++) {
            level.addParticle(
                    ParticleTypes.END_ROD,
                    getX() + 0.5D,
                    getY() + 0.5D,
                    getZ() + 0.5D,
                    random.nextGaussian() / 3,
                    random.nextGaussian() / 3,
                    random.nextGaussian() / 3);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isFrozen() ? null : SoundEvents.BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENDERMAN_SCREAM;
    }

    @Override
    protected void playHurtSound(DamageSource damageSource) {
        if (isFrozen())
            return;

        super.playHurtSound(damageSource);
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

                if (ghost.isFrozen())
                    return;

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
            setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.ghost = ghost;
        }

        private void setWantedPosition(LivingEntity target) {
            Vector3d vector = target.getEyePosition(1.0F).add(0.0D, -(ghost.getBbHeight() / 1.8), 0.0D);
            final double speed = ghost.getAttributeValue(Attributes.FLYING_SPEED);
            ghost.moveControl.setWantedPosition(vector.x, vector.y, vector.z, speed);
        }

        @Override
        public boolean canUse() {
            return !ghost.isFrozen() && ghost.getTarget() != null && ghost.getTarget().isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void start() {
            ghost.setAggressive(true);
        }

        @Override
        public void stop() {
            ghost.setAggressive(false);
            ghost.setTarget(null);
        }

        @Override
        public void tick() {
            LivingEntity target = ghost.getTarget();
            double distance = ghost.distanceToSqr(target);

            if (!ghost.isManeuvering()) {
                setWantedPosition(target);
            }
            if (ticksUntilNextAttack <= 0) {
                if (canAttackReach(target, distance)) {
                    ghost.doHurtTarget(target);
                    ticksUntilNextAttack = 20;
                }
            }
            else {
                --ticksUntilNextAttack;
            }
        }

        private boolean canAttackReach(LivingEntity target, double distance) {
            return distance <= (double)(ghost.getBbWidth() * 2.0F * ghost.getBbWidth() * 2.0F + target.getBbWidth());
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
            MovementController moveControl = this.ghost.getMoveControl();

            if (this.ghost.getTarget() != null || this.ghost.isManeuvering())
                return false;

            if (!moveControl.hasWanted()) {
                return true;
            }
            else {
                double x = moveControl.getWantedX() - this.ghost.getX();
                double y = moveControl.getWantedY() - this.ghost.getY();
                double z = moveControl.getWantedZ() - this.ghost.getZ();
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

    static class GhostLookAtGoal extends LookAtGoal {

        private final GhostEntity ghost;

        public GhostLookAtGoal(GhostEntity ghost, Class<? extends LivingEntity> lookAt, float range) {
            super(ghost, lookAt, range);
            this.ghost = ghost;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !ghost.isFrozen();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !ghost.isFrozen();
        }
    }
}
