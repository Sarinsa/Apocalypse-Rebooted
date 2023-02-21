package com.toast.apocalypse.common.entity.projectile;

import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.entity.living.Seeker;
import com.toast.apocalypse.common.misc.SeekerExplosionCalculator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class SeekerFireballEntity extends Fireball {

    private boolean sawTarget = false;
    private boolean reflected = false;
    private int explosionStrength = 1;

    public SeekerFireballEntity(EntityType<? extends Fireball> entityType, Level level) {
        super(entityType, level);
    }

    public SeekerFireballEntity(Level level, Seeker seeker, boolean sawTarget, double x, double y, double z) {
        super(ApocalypseEntities.SEEKER_FIREBALL.get(), seeker, x, y, z, level);
        this.sawTarget = sawTarget;
        this.explosionStrength = seeker.getExplosionPower();
    }

    /**
     * Helper method for creating the seeker fireball
     * explosion. A custom ExplosionContext is used in order
     * to explode blocks even if they are surrounded by a fluid.
     */
    public static void seekerExplosion(Level level, @Nullable Entity entity, DamageSource damageSource, double x, double y, double z, float explosionPower, boolean enableMobGrief) {
        Explosion.BlockInteraction mode = enableMobGrief ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
        level.explode(entity, damageSource, new SeekerExplosionCalculator(), x, y, z, explosionPower, true, mode);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        Level level = entity.getCommandSenderWorld();

        if (entity instanceof Seeker) {
            boolean enableMobGrief = level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level, this);
            Explosion.BlockInteraction interaction = enableMobGrief ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;

            if (!level.isClientSide) {
                entity.hurt(DamageSource.fireball(this, getOwner()), 1000.0F);
                level.explode(null, getX(), getY(), getZ(), 2.0F, interaction);
                discard();
            }
        }
        else if (!entity.fireImmune()) {
            Entity owner = getOwner();
            int remainingFireTicks = entity.getRemainingFireTicks();
            entity.setSecondsOnFire(5);
            boolean flag = entity.hurt(DamageSource.fireball(this, owner), 5.0F);

            if (!flag) {
                entity.setRemainingFireTicks(remainingFireTicks);
            }
            else if (owner instanceof LivingEntity livingEntity) {
                this.doEnchantDamageEffects(livingEntity, entity);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        Direction direction = result.getDirection();
        LivingEntity owner = null;

        if (getOwner() instanceof LivingEntity) {
            owner = (LivingEntity) getOwner();
        }
        boolean enabledMobGrief = level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || ForgeEventFactory.getMobGriefingEvent(level, this);

        if (!level.isClientSide) {
            if (sawTarget) {
                if (!(owner instanceof Mob) || enabledMobGrief) {
                    BlockPos firePos = result.getBlockPos().relative(direction);

                    if (level.isEmptyBlock(firePos)) {
                        level.setBlockAndUpdate(firePos, FireBlock.getState(level, firePos));
                        level.playSound(null, blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.MASTER, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
                    }
                }
            }
            else {
                seekerExplosion(level, owner, DamageSource.fireball(this, getOwner()), getX(), getY(), getZ(), (float) explosionStrength, enabledMobGrief);
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!level.isClientSide) {
            discard();
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damage) {
        if (isInvulnerableTo(damageSource))
            return false;

        markHurt();

        if (damageSource.getEntity() != null) {
            Entity entity = damageSource.getEntity();
            Vec3 vec = entity.getLookAngle();
            setDeltaMovement(vec);
            xPower = vec.x * 0.1D;
            yPower = vec.y * 0.1D;
            zPower = vec.z * 0.1D;
            setOwner(entity);
            return true;
        }
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("ExplosionPower", this.explosionStrength);
        compoundTag.putBoolean("SawTarget", this.sawTarget);
        compoundTag.putBoolean("Reflected", this.reflected);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);

        if (compoundTag.contains("ExplosionPower", Tag.TAG_ANY_NUMERIC))
            this.explosionStrength = compoundTag.getInt("ExplosionPower");

        if (compoundTag.contains("SawTarget", Tag.TAG_BYTE))
            this.sawTarget = compoundTag.getBoolean("SawTarget");

        if (compoundTag.contains("Reflected", Tag.TAG_BYTE))
            this.reflected = compoundTag.getBoolean("Reflected");
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
