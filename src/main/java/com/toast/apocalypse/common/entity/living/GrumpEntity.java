package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.entity.living.goals.MobEntityAttackedByTargetGoal;
import com.toast.apocalypse.common.entity.projectile.MonsterFishHook;
import com.toast.apocalypse.common.register.ApocalypseEffects;
import com.toast.apocalypse.common.register.ApocalypseItems;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

/**
 * This is a full moon mob that is meant to be a high threat to players that are not in a safe area from them.
 * Grumps fly, have a pulling attack, and have a melee attack that can't be reduced below 2 damage and applies a
 * short gravity effect.<br>
 * Unlike most full moon mobs, this one has no means of breaking through defenses and therefore relies on the
 * player being vulnerable to attack - whether by will or by other mobs breaking through to the player.
 */
public class GrumpEntity extends AbstractFullMoonGhastEntity {

    /**
     * The current fish hook entity
     * launched by the grump.
     */
    private MonsterFishHook fishHook;

    public GrumpEntity(EntityType<? extends GhastEntity> entityType, World world) {
        super(entityType, world);
        this.xpReward = 3;
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
        this.goalSelector.addGoal(1, new LaunchMonsterHookGoal(this));
        this.goalSelector.addGoal(1, new LookAroundGoal(this));
        this.goalSelector.addGoal(5, new GrumpEntity.RandomFlyGoal(this));
        this.targetSelector.addGoal(0, new GrumpNearestAttackableTargetGoal<>(this, PlayerEntity.class));
        this.targetSelector.addGoal(1, new MobEntityAttackedByTargetGoal(this, IFullMoonMob.class));
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize entitySize) {
        return 0.60F;
    }

    // Not nearly as loud as a ghast since it is much smaller.
    @Override
    protected float getSoundVolume() {
        return 2.5F;
    }

    public static boolean checkGrumpSpawnRules(EntityType<? extends GrumpEntity> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && MobEntity.checkMobSpawnRules(entityType, world, spawnReason, pos, random);
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {
        int chance = ApocalypseCommonConfig.COMMON.getGrumpBucketHelmetChance();

        if (chance <= 0)
            return;

        chance -= 1;

        if (this.random.nextInt(100) <= chance) {
            this.setItemSlot(EquipmentSlotType.HEAD, new ItemStack(ApocalypseItems.BUCKET_HELM.get()));
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damage) {
        if (damageSource.getDirectEntity() instanceof AbstractArrowEntity) {
            if (this.getItemBySlot(EquipmentSlotType.HEAD).getItem() == ApocalypseItems.BUCKET_HELM.get()) {
                damage = Math.min(damage, 1.0F);
            }
        }
        return super.hurt(damageSource, damage);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (super.doHurtTarget(entity)) {
            if (entity instanceof PlayerEntity) {
                int duration = this.level.getDifficulty() == Difficulty.HARD ? 100 : 60;
                ((PlayerEntity)entity).addEffect(new EffectInstance(ApocalypseEffects.HEAVY.get(), duration));
            }
            return true;
        }
        else {
            return false;
        }
    }

    @Nullable
    public ILivingEntityData finalizeSpawn(IServerWorld serverWorld, DifficultyInstance difficultyInstance, SpawnReason spawnReason, @Nullable ILivingEntityData spawnData, @Nullable CompoundNBT compoundNBT) {
        spawnData = super.finalizeSpawn(serverWorld, difficultyInstance, spawnReason, spawnData, compoundNBT);
        this.populateDefaultEquipmentSlots(difficultyInstance);
        return spawnData;
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
                this.grump.xRot = 0.0F;
            } else {
                LivingEntity target = this.grump.getTarget();

                double x = target.getX() - this.grump.getX();
                double z = target.getZ() - this.grump.getZ();
                this.grump.yRot = -((float)MathHelper.atan2(x, z)) * (180F / (float)Math.PI);
                this.grump.xRot = (float) target.getEyeY() - (float) this.grump.getEyeY();
            }
            this.grump.yBodyRot = this.grump.yRot;
        }
    }

    private static class MeleeAttackGoal extends Goal {

        final GrumpEntity grump;

        public MeleeAttackGoal(GrumpEntity grump) {
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.grump = grump;
        }

        private void setWantedPosition(LivingEntity target) {
            Vector3d vector = target.getEyePosition(1.0F).add(0.0D, -(this.grump.getBbHeight() / 1.3), 0.0D);
            this.grump.moveControl.setWantedPosition(vector.x, vector.y, vector.z, 1.0D);
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.grump.getTarget();
            return target != null && this.grump.canSeeDirectly(target);
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.grump.getTarget();

            if (target != null && target.isAlive()) {
                return this.grump.moveControl.hasWanted();
            }
            return false;
        }

        @Override
        public void start() {
            LivingEntity target = this.grump.getTarget();

            if (target != null) {
                this.setWantedPosition(target);
            }
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

    private static class LaunchMonsterHookGoal extends Goal {

        private final GrumpEntity grump;
        private int timeHookExisted;
        private int timeNextHookLaunch;

        public LaunchMonsterHookGoal(GrumpEntity grump) {
            this.setFlags(EnumSet.of(Flag.TARGET));
            this.grump = grump;
        }

        @Override
        public boolean canUse() {
            if (grump.getTarget() != null) {
                LivingEntity target = grump.getTarget();
                return grump.canSee(target) && grump.distanceToSqr(target) < 180.0D;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void start() {
            this.spawnMonsterFishHook();
        }

        @Override
        public void stop() {
            if (this.grump.fishHook != null) {
                this.grump.fishHook.remove();
                this.grump.fishHook = null;
            }
            this.timeHookExisted = 0;
            this.timeNextHookLaunch = 0;
        }

        @Override
        public void tick() {
            GrumpEntity grump = this.grump;

            if (grump.fishHook == null) {
                if (++this.timeNextHookLaunch >= 40) {
                    this.spawnMonsterFishHook();
                    this.timeNextHookLaunch = 0;
                }
            }
            else {
                if (grump.fishHook.getHookedIn() != null) {
                    // The grump might end up accidentally hooking itself, who knows?
                    if (grump.fishHook.getHookedIn() != this.grump) {
                        grump.fishHook.bringInHookedEntity();
                    }
                    this.removeMonsterFishHook();
                    return;
                }

                if (++this.timeHookExisted >= 60) {
                    this.timeHookExisted = 0;
                    this.removeMonsterFishHook();
                }
            }
        }

        private void removeMonsterFishHook() {
            this.grump.fishHook.remove();
            this.grump.fishHook = null;
        }

        private void spawnMonsterFishHook() {
            World world = this.grump.getCommandSenderWorld();
            MonsterFishHook fishHook = new MonsterFishHook(this.grump, world);
            world.addFreshEntity(fishHook);
            this.grump.fishHook = fishHook;
            world.playSound(null, this.grump.blockPosition(), SoundEvents.FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.6F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
        }
    }

    private static class GrumpNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

        public GrumpNearestAttackableTargetGoal(MobEntity entity, Class<T> targetClass) {
            super(entity, targetClass, true);
        }

        /** Friggin' large bounding box */
        @Override
        protected AxisAlignedBB getTargetSearchArea(double followRange) {
            return this.mob.getBoundingBox().inflate(followRange, followRange, followRange);
        }
    }

    /** Copied from ghast */
    static class RandomFlyGoal extends Goal {

        private final GrumpEntity grump;

        public RandomFlyGoal(GrumpEntity grump) {
            this.grump = grump;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.grump.getTarget() != null) {
                return false;
            }
            MovementController movementcontroller = this.grump.getMoveControl();

            if (!movementcontroller.hasWanted()) {
                return true;
            }
            else {
                double x = movementcontroller.getWantedX() - this.grump.getX();
                double y = movementcontroller.getWantedY() - this.grump.getY();
                double z = movementcontroller.getWantedZ() - this.grump.getZ();
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
            Random random = this.grump.getRandom();
            double x = this.grump.getX() + (double)((random.nextFloat() * 2.0F - 1.0F) * 8.0F);
            double y = this.grump.getY() + (double)((random.nextFloat() * 2.0F - 1.0F) * 8.0F);
            double z = this.grump.getZ() + (double)((random.nextFloat() * 2.0F - 1.0F) * 8.0F);
            this.grump.getMoveControl().setWantedPosition(x, y, z, 1.0D);
        }
    }
}
