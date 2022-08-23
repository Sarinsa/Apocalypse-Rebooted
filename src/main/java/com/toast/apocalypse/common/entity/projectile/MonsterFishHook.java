package com.toast.apocalypse.common.entity.projectile;

import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * This is a fishhook projectile that can be fired by monsters to pull targets closer.<br>
 * Players are able to block this projectile with a shield, negating its effects.
 *
 * Essentially a copy-paste of {@link net.minecraft.entity.projectile.FishingBobberEntity}
 */
public class MonsterFishHook extends ProjectileEntity implements IEntityAdditionalSpawnData {

    private static final DataParameter<Integer> DATA_HOOKED_ENTITY = EntityDataManager.defineId(MonsterFishHook.class, DataSerializers.INT);
    private int life;
    private Entity hookedIn;
    private State currentState = State.FLYING;

    public MonsterFishHook(EntityType<? extends MonsterFishHook> entityType, World world) {
        super(entityType, world);
    }

    private MonsterFishHook(World world, MobEntity mobEntity) {
        super(ApocalypseEntities.MONSTER_FISH_HOOK.get(), world);
        this.setOwner(mobEntity);
        this.noCulling = true;
    }

    public MonsterFishHook(MobEntity mobEntity, LivingEntity target, World world) {
        this(world, mobEntity);

        final Vector3d lookVec = mobEntity.getViewVector(1.0F).scale(mobEntity.getBbWidth());
        this.setPos(mobEntity.getX() + lookVec.x, mobEntity.getEyeY() - 0.1, mobEntity.getZ() + lookVec.z);

        final double dX = target.getX() - getX();
        final double dY = target.getY( 0.3333 ) - getY();
        final double dZ = target.getZ() - getZ();
        final double dH = MathHelper.sqrt( dX * dX + dZ * dZ );
        this.shoot(dX, dY + dH * 0.2, dZ, 1.3F, 0);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_HOOKED_ENTITY, 0);
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> dataParameter) {
        if (DATA_HOOKED_ENTITY.equals(dataParameter)) {
            int i = this.getEntityData().get(DATA_HOOKED_ENTITY);
            this.hookedIn = i > 0 ? this.level.getEntity(i - 1) : null;
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
        super.tick();
        LivingEntity livingEntity = this.getLivingOwner();

        if (livingEntity == null) {
            this.remove();
        }
        else if (this.level.isClientSide || !this.shouldStopFishing(livingEntity)) {
            if (this.onGround) {
                ++this.life;
                if (this.life >= 1200) {
                    this.remove();
                    return;
                }
            }
            else {
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
            }
            else {
                if (this.currentState == State.HOOKED_IN_ENTITY) {
                    if (this.hookedIn != null) {
                        if (!this.hookedIn.isAlive()) {
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

    private boolean shouldStopFishing(LivingEntity owner) {
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
                PlayerEntity player = (PlayerEntity) entity;

                if (player.isBlocking()) {
                    player.disableShield(true);
                    this.remove();
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
        LivingEntity livingEntity = this.getLivingOwner();

        if (livingEntity != null) {
            this.level.playSound(null, livingEntity.blockPosition(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundCategory.HOSTILE, 0.6F, 0.4F / (this.level.random.nextFloat() * 0.4F + 0.8F));
            Entity entity = this.hookedIn;

            double xMotion = livingEntity.getX() - this.getX();
            double yMotion = livingEntity.getY() - this.getY();
            double zMotion = livingEntity.getZ() - this.getZ();

            double v = Math.sqrt(xMotion * xMotion + yMotion * yMotion + zMotion * zMotion);
            double multiplier = 0.3;

            Vector3d velocity = new Vector3d(xMotion * multiplier, yMotion * multiplier + Math.sqrt(v) * 0.1, zMotion * multiplier);

            if (entity instanceof ServerPlayerEntity) {
                NetworkHelper.sendEntityVelocityUpdate((ServerPlayerEntity) entity, entity, velocity);
            }
            entity.setDeltaMovement(velocity);
        }
    }

    @Override
    protected boolean isMovementNoisy() {
        return false;
    }

    @Nullable
    public LivingEntity getLivingOwner() {
        Entity entity = this.getOwner();
        return entity instanceof LivingEntity ? (LivingEntity) entity : null;
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

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeInt(this.getOwner() == null ? this.getId() : this.getOwner().getId());
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        this.setOwner(this.level.getEntity(additionalData.readInt()));
    }

    enum State {
        FLYING,
        HOOKED_IN_ENTITY,
        BOBBING;
    }
}
