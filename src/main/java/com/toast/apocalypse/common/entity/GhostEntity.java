package com.toast.apocalypse.common.entity;

import com.toast.apocalypse.api.IFullMoonMob;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.register.ApocalypseEffects;
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
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.util.EnumSet;

public class GhostEntity extends FlyingEntity implements IMob, IFullMoonMob {

    public GhostEntity(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new GhostMovementController<>(this);
        this.xpReward = 3;
    }

    public static AttributeModifierMap.MutableAttribute createGhostAttributes() {
        return FlyingEntity.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.FLYING_SPEED, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new GhostEntity.NearestAttackablePlayerTargetGoal<>(this, PlayerEntity.class, true));
        this.goalSelector.addGoal(0, new GhostEntity.MeleeAttackGoal());
        this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (super.doHurtTarget(entity)) {
            if (entity instanceof PlayerEntity) {
                int duration = this.getCommandSenderWorld().getDifficulty() == Difficulty.HARD ? 120 : 80;
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
        }
        super.aiStep();
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true; // Immune to drowning
    }

    @Override
    public boolean isPushedByFluid() {
        return false; // Not affected by fluid motion
    }

    @Override
    protected void lavaHurt() {
        // Immune to lava fire and damage
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
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
    }

    /**
     * Essentially a copy of the Vex' movement controller.
     */
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

    private static class NearestAttackablePlayerTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

        public NearestAttackablePlayerTargetGoal(MobEntity mobEntity, Class<T> targetClass, boolean longMemory) {
            super(mobEntity, targetClass, longMemory);
        }
    }

    private class MeleeAttackGoal extends Goal {

        final GhostEntity ghost;

        public MeleeAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.ghost = GhostEntity.this;
        }

        @Override
        public boolean canUse() {
            if (this.ghost.getTarget() != null && !this.ghost.getMoveControl().hasWanted()) {
                return this.ghost.distanceToSqr(this.ghost.getTarget()) > 4.0D;
            }
            else {
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.ghost.getMoveControl().hasWanted() && this.ghost.getTarget() != null && this.ghost.getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity target = this.ghost.getTarget();
            Vector3d vector3d = target.getEyePosition(1.0F);
            this.ghost.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
        }

        @Override
        public void stop() {
        }

        @Override
        public void tick() {
            LivingEntity target = this.ghost.getTarget();

            if (this.ghost.getBoundingBox().intersects(target.getBoundingBox())) {
                this.ghost.doHurtTarget(target);
            }
            else {
                double distanceSqr = this.ghost.distanceToSqr(target);
                if (distanceSqr < 9.0D) {
                    Vector3d vector3d = target.getEyePosition(-4.0F);
                    Apocalypse.LOGGER.info("Target Y coord: " + vector3d.y);
                    this.ghost.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                }
            }
        }
    }
}
