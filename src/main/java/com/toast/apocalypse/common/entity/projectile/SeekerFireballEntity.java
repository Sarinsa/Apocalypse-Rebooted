package com.toast.apocalypse.common.entity.projectile;

import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.entity.living.SeekerEntity;
import com.toast.apocalypse.common.misc.SeekerExplosionContext;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class SeekerFireballEntity extends AbstractFireballEntity {

    private boolean sawTarget = false;
    private boolean reflected = false;
    private int explosionStrength = 1;

    public SeekerFireballEntity(EntityType<? extends AbstractFireballEntity> entityType, World world) {
        super(entityType, world);
    }

    public SeekerFireballEntity(World world, SeekerEntity seeker, boolean sawTarget, double x, double y, double z) {
        super(ApocalypseEntities.SEEKER_FIREBALL.get(), seeker, x, y, z, world);
        this.sawTarget = sawTarget;
        this.explosionStrength = seeker.getExplosionPower();
    }

    /**
     * Helper method for creating the seeker fireball
     * explosion. A custom ExplosionContext is used in order
     * to explode blocks even if they are surrounded by a fluid.
     */
    public static void seekerExplosion(World world, @Nullable Entity entity, DamageSource damageSource, double x, double y, double z, float explosionPower, boolean enableMobGrief) {
        Explosion.Mode mode = enableMobGrief ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
        world.explode(entity, damageSource, new SeekerExplosionContext(), x, y, z, explosionPower, true, mode);
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        Entity entity = result.getEntity();
        World world = entity.getCommandSenderWorld();

        if (entity instanceof SeekerEntity) {
            boolean enableMobGrief = world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, this.getEntity());
            Explosion.Mode mode = enableMobGrief ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;

            if (!world.isClientSide) {
                entity.hurt(DamageSource.fireball(this, this.getOwner()), 1000.0F);
                world.explode(null, this.getX(), this.getY(), this.getZ(), 2.0F, mode);
                this.remove();
            }
        }
        else if (!entity.fireImmune()) {
            Entity owner = this.getOwner();
            int remainingFireTicks = entity.getRemainingFireTicks();
            entity.setSecondsOnFire(5);
            boolean flag = entity.hurt(DamageSource.fireball(this, owner), 5.0F);

            if (!flag) {
                entity.setRemainingFireTicks(remainingFireTicks);
            }
            else if (owner instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity) owner, entity);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult result) {
        World world = this.level;
        Direction direction = result.getDirection();
        LivingEntity owner = null;

        if (this.getOwner() instanceof LivingEntity) {
            owner = (LivingEntity) this.getOwner();
        }
        boolean enabledMobGrief = world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, this.getEntity());

        if (!this.level.isClientSide) {
            if (this.sawTarget) {
                if (!(owner instanceof MobEntity) || enabledMobGrief) {
                    BlockPos firePos = result.getBlockPos().relative(direction);

                    if (this.level.isEmptyBlock(firePos)) {
                        this.level.setBlockAndUpdate(firePos, AbstractFireBlock.getState(this.level, firePos));
                        this.level.playSound(null, this.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundCategory.MASTER, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
                    }
                }
            }
            else {
                seekerExplosion(this.level, owner, DamageSource.fireball(this, this.getOwner()), this.getX(), this.getY(), this.getZ(), (float) this.explosionStrength, enabledMobGrief);
            }
        }
    }

    @Override
    protected void onHit(RayTraceResult result) {
        super.onHit(result);

        if (!this.level.isClientSide) {
            this.remove();
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damage) {
        if (this.isInvulnerableTo(damageSource))
            return false;

        this.markHurt();

        if (damageSource.getEntity() != null) {
            Entity entity = damageSource.getEntity();
            Vector3d vec = entity.getLookAngle();
            this.setDeltaMovement(vec);
            this.xPower = vec.x * 0.1D;
            this.yPower = vec.y * 0.1D;
            this.zPower = vec.z * 0.1D;
            this.setOwner(entity);
            return true;
        }
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("ExplosionPower", this.explosionStrength);
        compoundNBT.putBoolean("SawTarget", this.sawTarget);
        compoundNBT.putBoolean("Reflected", this.reflected);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        this.explosionStrength = compoundNBT.getInt("ExplosionPower");
        this.sawTarget = compoundNBT.getBoolean("SawTarget");
        this.reflected = compoundNBT.getBoolean("Reflected");
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
