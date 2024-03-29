package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.entity.living.ai.MobEntityAttackedByTargetGoal;
import com.toast.apocalypse.common.entity.living.ai.MoonMobPlayerTargetGoal;
import com.toast.apocalypse.common.entity.projectile.DestroyerFireballEntity;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;

/**
 * This is a full moon mob similar to a ghast, though it has unlimited aggro range ignoring line of sight and
 * its fireballs can potentially destroy any type of block.<br>
 * <br>
 * In addition to the above, Destroyers can also attempt to destroy their target player's
 * respawn point, forcing the player to confront it.
 */
public class DestroyerEntity extends AbstractFullMoonGhastEntity {

    public static final DataParameter<Boolean> ATTACKED_BY_PT = EntityDataManager.defineId(DestroyerEntity.class, DataSerializers.BOOLEAN);
    protected boolean isTargetingSpawnPoint = false;

    public DestroyerEntity(EntityType<? extends GhastEntity> entityType, World world) {
        super(entityType, world);
        moveControl = new MoveHelperController(this);
        this.xpReward = 5;
    }

    public static AttributeModifierMap.MutableAttribute createDestroyerAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.FOLLOW_RANGE, Double.POSITIVE_INFINITY)
                .add(ForgeMod.SWIM_SPEED.get(), 1.1D);
    }

    public static boolean checkDestroyerSpawnRules(EntityType<? extends DestroyerEntity> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && MobEntity.checkMobSpawnRules(entityType, world, spawnReason, pos, random);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ATTACKED_BY_PT, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new DestroySpawnPointGoal<>(this));
        this.goalSelector.addGoal(1, new DestroyerEntity.FireballAttackGoal(this));
        this.goalSelector.addGoal(1, new DestroyerLookAroundGoal(this));
        this.goalSelector.addGoal(2, new DestroyerEntity.RandomOrRelativeToTargetFlyGoal(this));
        this.targetSelector.addGoal(0, new MobEntityAttackedByTargetGoal(this, IMob.class));
        this.targetSelector.addGoal(1, new MoonMobPlayerTargetGoal<>(this, false));
        this.targetSelector.addGoal(2, new DestroyerNearestAttackableTargetGoal<>(this, PlayerEntity.class));
    }

    /**
     * @return True if this Destroyer at any point
     * has been attacked by their siege target player
     * since it spawned.
     */
    public boolean attackedBySiegeTarget() {
        return entityData.get(ATTACKED_BY_PT);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
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

        if (damageSource.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) damageSource.getEntity();

            // If attacked by siege target player, notify the Destroyer, so it
            // focuses on the player rather than their respawn point.
            if (getPlayerTargetUUID() != null && getPlayerTargetUUID() == player.getUUID()) {
                entityData.set(ATTACKED_BY_PT, true);
            }
        }

        // Prevent instant fireball death and return to sender advancement
        else if (damageSource.getDirectEntity() instanceof AbstractFireballEntity) {
            // Prevent the destroyer from damaging itself
            // when close up to a wall or solid obstacle
            if (damageSource.getEntity() == this)
                return false;

            if (damageSource.getEntity() instanceof PlayerEntity) {
                super.hurt(DamageSource.playerAttack((PlayerEntity) damageSource.getEntity()), 7.0F);
                return true;
            }
        }
        else if (damageSource.isExplosion() && damageSource.getEntity() == this) {
            return false;
        }
        return super.hurt(damageSource, damage);
    }

    @Override
    public int getExplosionPower() {
        return this.explosionPower == 0 ? ApocalypseCommonConfig.COMMON.getDestroyerExplosionPower() : this.explosionPower;
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

    private boolean withinFiringRange(Vector3d vector3d) {
        return horizontalDistanceToSqr(vector3d) < 4096.0D;
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
            return destroyer.getTarget() != null && !destroyer.isTargetingSpawnPoint;
        }

        @Override
        public void start() {
            chargeTime = 0;
        }

        @Override
        public void stop() {
            destroyer.setCharging(false);
        }

        @Override
        public void tick() {
            LivingEntity target = destroyer.getTarget();

            if (destroyer.withinFiringRange(target.position()) && destroyer.canSee(target)) {
                World world = destroyer.level;
                ++chargeTime;
                if (chargeTime == 10 && !destroyer.isSilent()) {
                    world.levelEvent(null, 1015, destroyer.blockPosition(), 0);
                }

                if (chargeTime == 20) {
                    Vector3d vector3d = destroyer.getViewVector(1.0F);
                    double x = target.getX() - (destroyer.getX() + vector3d.x * 4.0D);
                    double y = target.getY(0.5D) - (0.5D + destroyer.getY(0.5D));
                    double z = target.getZ() - (destroyer.getZ() + vector3d.z * 4.0D);

                    if (!destroyer.isSilent()) {
                        world.levelEvent(null, 1016, destroyer.blockPosition(), 0);
                    }
                    DestroyerFireballEntity fireball = new DestroyerFireballEntity(world, destroyer, x, y, z);
                    fireball.setPos(destroyer.getX() + vector3d.x * 4.0D, destroyer.getY(0.5D) + 0.5D, fireball.getZ() + vector3d.z * 4.0D);
                    world.addFreshEntity(fireball);
                    chargeTime = -40;
                }
            }
            else if (chargeTime > 0) {
                --chargeTime;
            }
            destroyer.setCharging(chargeTime > 10);
        }
    }

    static class RandomOrRelativeToTargetFlyGoal extends Goal {

        private static final double maxDistanceBeforeFollow = 3000.0D;
        private final DestroyerEntity destroyer;

        public RandomOrRelativeToTargetFlyGoal(DestroyerEntity destroyer) {
            this.destroyer = destroyer;
            setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            MovementController controller = destroyer.getMoveControl();

            if (!controller.hasWanted()) {
                return true;
            }
            else {
                double x = controller.getWantedX() - destroyer.getX();
                double y = controller.getWantedY() - destroyer.getY();
                double z = controller.getWantedZ() - destroyer.getZ();
                double d3 = x * x + y * y + z * z;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        private void setRandomWantedPosition() {
            Random random = destroyer.getRandom();
            double x = destroyer.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double y = destroyer.getY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 10.0F);
            double z = destroyer.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.destroyer.getMoveControl().setWantedPosition(x, y, z, 1.0D);
        }

        @Override
        public void start() {
            MovementController controller = destroyer.getMoveControl();

            if (destroyer.getTarget() != null) {
                Optional<Vector3d> respawnPos = Optional.empty();

                if (ApocalypseCommonConfig.COMMON.getDestroyerTargetRespawnPos() && destroyer.getTarget() instanceof ServerPlayerEntity && !destroyer.attackedBySiegeTarget()) {
                    ServerPlayerEntity player = (ServerPlayerEntity) destroyer.getTarget();

                    if (destroyer.getPlayerTargetUUID() != null && destroyer.getPlayerTargetUUID() == player.getUUID()) {
                        BlockPos pos = player.getRespawnPosition();

                        if (isPlayerSpawnValid(pos, destroyer.level)) {
                            double x = pos.getX();
                            double y = pos.getY() + 10.0D;
                            double z = pos.getZ();

                            if (destroyer.canReachDist(x, y, z, 10)) {
                                respawnPos = Optional.of(new Vector3d(x, y, z));
                            }
                        }
                    }
                }
                LivingEntity target = destroyer.getTarget();
                double distanceToTarget = destroyer.distanceToSqr(target);

                if (respawnPos.isPresent()) {
                    Vector3d vec3d = respawnPos.get();

                    if (!destroyer.withinFiringRange(vec3d))
                        controller.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                }
                else {
                    if (distanceToTarget > maxDistanceBeforeFollow) {
                        controller.setWantedPosition(target.getX(), target.getY() + 10.0D, target.getZ(), 1.0D);
                    }
                    else {
                        setRandomWantedPosition();
                    }
                }
            }
            else {
                setRandomWantedPosition();
            }
        }
    }

    private static class DestroySpawnPointGoal<T extends DestroyerEntity> extends Goal {

        private final T destroyer;
        private BlockPos respawnPos;
        public int chargeTime;

        private DestroySpawnPointGoal(T destroyer) {
            this.destroyer = destroyer;
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public boolean canUse() {
            if (!ApocalypseCommonConfig.COMMON.getDestroyerTargetRespawnPos())
                return false;

            if (IFullMoonMob.getEventTarget(destroyer) instanceof ServerPlayerEntity && !destroyer.attackedBySiegeTarget()) {
                ServerPlayerEntity targetPlayer = (ServerPlayerEntity) IFullMoonMob.getEventTarget(destroyer);

                if (targetPlayer.getRespawnPosition() != null && (targetPlayer.getRespawnDimension().equals(destroyer.level.dimension())) && isPlayerSpawnValid(targetPlayer.getRespawnPosition(), destroyer.level)) {
                    respawnPos = targetPlayer.getRespawnPosition();
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            if (!ApocalypseCommonConfig.COMMON.getDestroyerTargetRespawnPos())
                return false;

            if (IFullMoonMob.getEventTarget(destroyer) instanceof ServerPlayerEntity && !destroyer.attackedBySiegeTarget()) {
                ServerPlayerEntity targetPlayer = (ServerPlayerEntity) IFullMoonMob.getEventTarget(destroyer);

                if (respawnPos != null && (targetPlayer.getRespawnDimension().equals(destroyer.level.dimension()))) {
                    return isPlayerSpawnValid(respawnPos, destroyer.level);
                }
            }
            return false;
        }

        @Override
        public void start() {
            chargeTime = 0;
            destroyer.isTargetingSpawnPoint = true;
        }

        @Override
        public void stop() {
            respawnPos = null;
            destroyer.setCharging(false);
            destroyer.isTargetingSpawnPoint = false;
        }

        @Override
        public void tick() {
            if (destroyer.horizontalDistanceToSqr(respawnPos) < 4096.0D) {
                World world = destroyer.level;
                ++chargeTime;

                if (chargeTime == 10 && !destroyer.isSilent()) {
                    world.levelEvent(null, 1015, destroyer.blockPosition(), 0);
                }

                if (chargeTime == 20) {
                    Vector3d vector3d = destroyer.getViewVector(1.0F);
                    double x = respawnPos.getX() - (destroyer.getX() + vector3d.x * 4.0D);
                    double y = respawnPos.getY() - (0.5D + destroyer.getY(0.5D));
                    double z = respawnPos.getZ() - (destroyer.getZ() + vector3d.z * 4.0D);

                    if (!destroyer.isSilent()) {
                        world.levelEvent(null, 1016, destroyer.blockPosition(), 0);
                    }
                    DestroyerFireballEntity fireball = new DestroyerFireballEntity(world, destroyer, x, y, z);
                    fireball.setPos(destroyer.getX() + vector3d.x * 4.0D, destroyer.getY(0.5D) + 0.5D, fireball.getZ() + vector3d.z * 4.0D);
                    world.addFreshEntity(fireball);
                    chargeTime = -40;
                }
            }
            else if (chargeTime > 0) {
                --chargeTime;
            }
            destroyer.setCharging(chargeTime > 10);
        }
    }

    protected static class DestroyerLookAroundGoal extends Goal {
        private final DestroyerEntity destroyer;

        public DestroyerLookAroundGoal(DestroyerEntity destroyer) {
            this.destroyer = destroyer;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        public void tick() {
            if (destroyer.getTarget() == null) {
                Vector3d vector3d = destroyer.getDeltaMovement();
                destroyer.yRot = -((float) MathHelper.atan2(vector3d.x, vector3d.z)) * (180F / (float)Math.PI);
            }
            else if (destroyer.getTarget() instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) destroyer.getTarget();
                double x, z;

                if (!destroyer.attackedBySiegeTarget() && player.getRespawnPosition() != null && (player.getRespawnDimension().equals(destroyer.level.dimension())) && isPlayerSpawnValid(player.getRespawnPosition(), destroyer.level)) {
                    BlockPos respawnPos = player.getRespawnPosition();
                    x = respawnPos.getX() - destroyer.getX();
                    z = respawnPos.getZ() - destroyer.getZ();
                }
                else {
                    x = player.getX() - destroyer.getX();
                    z = player.getZ() - destroyer.getZ();
                }
                destroyer.yRot = -((float)MathHelper.atan2(x, z)) * (180F / (float)Math.PI);
            }
            else {
                LivingEntity target = destroyer.getTarget();

                double x = target.getX() - destroyer.getX();
                double z = target.getZ() - destroyer.getZ();
                destroyer.yRot = -((float)MathHelper.atan2(x, z)) * (180F / (float)Math.PI);
            }
            destroyer.yBodyRot = destroyer.yRot;
        }
    }

    /**
     * Checks if the player's respawn point is still valid
     * after possibly having been destroyed.
     */
    private static boolean isPlayerSpawnValid(@Nullable BlockPos pos, World world) {
        if (pos == null)
            return false;

        Block block = world.getBlockState(pos).getBlock();
        return block instanceof BedBlock || block instanceof RespawnAnchorBlock;
    }
}
