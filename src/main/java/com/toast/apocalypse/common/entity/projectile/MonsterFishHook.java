package com.toast.apocalypse.common.entity.projectile;

import com.toast.apocalypse.common.register.ApocalypseEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * This is a fish hook projectile that can be fired by monsters to pull targets closer.<br>
 * Players are able to block this projectile, negating its effects.
 */
public class MonsterFishHook extends ProjectileEntity {

    private final Random syncronizedRandom = new Random();
    private boolean biting;
    private static final DataParameter<Integer> DATA_HOOKED_ENTITY = EntityDataManager.defineId(MonsterFishHook.class, DataSerializers.INT);
    private static final DataParameter<Boolean> DATA_BITING = EntityDataManager.defineId(MonsterFishHook.class, DataSerializers.BOOLEAN);
    private int life;
    private Entity hookedIn;
    private State currentState = State.FLYING;

    private MonsterFishHook(World world, MobEntity mobEntity) {
        super(ApocalypseEntities.MONSTER_FISH_HOOK.get(), world);
        this.noCulling = true;
        this.setOwner(mobEntity);
    }

    @OnlyIn(Dist.CLIENT)
    public MonsterFishHook(World world, MobEntity mobEntity, double x, double y, double z) {
        this(world, mobEntity);
        this.setPos(x, y, z);
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
    }

    public MonsterFishHook(MobEntity mobEntity, World world) {
        this(world, mobEntity);
        float f = mobEntity.xRot;
        float f1 = mobEntity.yRot;
        float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
        double d0 = mobEntity.getX() - (double)f3 * 0.3D;
        double d1 = mobEntity.getEyeY();
        double d2 = mobEntity.getZ() - (double)f2 * 0.3D;
        this.moveTo(d0, d1, d2, f1, f);
        Vector3d vector3d = new Vector3d(-f3, MathHelper.clamp(-(f5 / f4), -5.0F, 5.0F), -f2);
        double d3 = vector3d.length();
        vector3d = vector3d.multiply(0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D);
        this.setDeltaMovement(vector3d);
        this.yRot = (float)(MathHelper.atan2(vector3d.x, vector3d.z) * (double)(180F / (float)Math.PI));
        this.xRot = (float)(MathHelper.atan2(vector3d.y, MathHelper.sqrt(getHorizontalDistanceSqr(vector3d))) * (double)(180F / (float)Math.PI));
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_HOOKED_ENTITY, 0);
        this.getEntityData().define(DATA_BITING, false);
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> dataParameter) {
        if (DATA_HOOKED_ENTITY.equals(dataParameter)) {
            int i = this.getEntityData().get(DATA_HOOKED_ENTITY);
            this.hookedIn = i > 0 ? this.level.getEntity(i - 1) : null;
        }

        if (DATA_BITING.equals(dataParameter)) {
            this.biting = this.getEntityData().get(DATA_BITING);
            if (this.biting) {
                this.setDeltaMovement(this.getDeltaMovement().x, -0.4F * MathHelper.nextFloat(this.syncronizedRandom, 0.6F, 1.0F), this.getDeltaMovement().z);
            }
        }
        super.onSyncedDataUpdated(dataParameter);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 4096.0D;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double parameter, double mappings, double would, float be, float nice, int to, boolean have) {
    }

    @Override
    public void tick() {
        this.syncronizedRandom.setSeed(this.getUUID().getLeastSignificantBits() ^ this.level.getGameTime());
        super.tick();
        MobEntity mobEntity = this.getMobOwner();
        if (mobEntity == null) {
            this.remove();
        } else if (this.level.isClientSide || !this.shouldStopFishing(mobEntity)) {
            if (this.onGround) {
                ++this.life;
                if (this.life >= 1200) {
                    this.remove();
                    return;
                }
            } else {
                this.life = 0;
            }

            float f = 0.0F;
            BlockPos blockpos = this.blockPosition();
            FluidState fluidstate = this.level.getFluidState(blockpos);
            if (fluidstate.is(FluidTags.WATER)) {
                f = fluidstate.getHeight(this.level, blockpos);
            }

            boolean flag = f > 0.0F;
            if (this.currentState == State.FLYING) {
                if (this.hookedIn != null) {
                    this.setDeltaMovement(Vector3d.ZERO);
                    this.currentState = State.HOOKED_IN_ENTITY;
                    return;
                }

                if (flag) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.3D, 0.2D, 0.3D));
                    this.currentState = State.BOBBING;
                    return;
                }

                this.checkCollision();
            } else {
                if (this.currentState == State.HOOKED_IN_ENTITY) {
                    if (this.hookedIn != null) {
                        if (this.hookedIn.removed) {
                            this.hookedIn = null;
                            this.currentState = State.FLYING;
                        } else {
                            this.setPos(this.hookedIn.getX(), this.hookedIn.getY(0.8D), this.hookedIn.getZ());
                        }
                    }

                    return;
                }

                if (this.currentState == State.BOBBING) {
                    Vector3d vector3d = this.getDeltaMovement();
                    double d0 = this.getY() + vector3d.y - (double)blockpos.getY() - (double)f;
                    if (Math.abs(d0) < 0.01D) {
                        d0 += Math.signum(d0) * 0.1D;
                    }

                    this.setDeltaMovement(vector3d.x * 0.9D, vector3d.y - d0 * (double)this.random.nextFloat() * 0.2D, vector3d.z * 0.9D);
                }
            }

            if (!fluidstate.is(FluidTags.WATER)) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.updateRotation();

            if (this.currentState == State.FLYING && (this.onGround || this.horizontalCollision)) {
                this.setDeltaMovement(Vector3d.ZERO);
            }
            this.setDeltaMovement(this.getDeltaMovement().scale(0.92D));
            this.reapplyPosition();
        }
    }

    private boolean shouldStopFishing(MobEntity owner) {
        if (!owner.isAlive() || !(this.distanceToSqr(owner) > 1024.0D)) {
            return false;
        } else {
            this.remove();
            return true;
        }
    }

    private void checkCollision() {
        RayTraceResult raytraceresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
        this.onHit(raytraceresult);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) || entity.isAlive() && entity instanceof ItemEntity;
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult rayTraceResult) {
        super.onHitEntity(rayTraceResult);
        if (!this.level.isClientSide) {
            Entity entity = rayTraceResult.getEntity();

            if (entity instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity) entity;

                if (playerEntity.isBlocking()) {
                    if (playerEntity.getCommandSenderWorld().getDifficulty() == Difficulty.HARD)
                        playerEntity.disableShield(true);

                    return;
                }
            }

            this.hookedIn = rayTraceResult.getEntity();
            this.setHookedEntity();
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult rayTraceResult) {
        super.onHitBlock(rayTraceResult);
        this.setDeltaMovement(this.getDeltaMovement().normalize().scale(rayTraceResult.distanceTo(this)));
    }

    private void setHookedEntity() {
        this.getEntityData().set(DATA_HOOKED_ENTITY, this.hookedIn.getId() + 1);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        // Nothing to write.
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        // Nothing to read.
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte event) {
        if (event == 31 && this.level.isClientSide && this.hookedIn instanceof PlayerEntity && ((PlayerEntity)this.hookedIn).isLocalPlayer()) {
            this.bringInHookedEntity();
        }
        super.handleEntityEvent(event);
    }

    public void bringInHookedEntity() {
        Entity entity = this.getOwner();
        if (entity != null) {
            Vector3d vector3d = (new Vector3d(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ())).scale(0.1D);
            this.hookedIn.setDeltaMovement(this.hookedIn.getDeltaMovement().add(vector3d));
        }
    }

    @Override
    protected boolean isMovementNoisy() {
        return false;
    }

    @Nullable
    public MobEntity getMobOwner() {
        Entity entity = this.getOwner();
        return entity instanceof MobEntity ? (MobEntity) entity : null;
    }

    @Nullable
    public Entity getHookedIn() {
        return this.hookedIn;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    enum State {
        FLYING,
        HOOKED_IN_ENTITY,
        BOBBING;
    }

    enum WaterType {
        ABOVE_WATER,
        INSIDE_WATER,
        INVALID;
    }
}
