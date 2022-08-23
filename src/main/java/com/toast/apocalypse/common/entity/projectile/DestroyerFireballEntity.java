package com.toast.apocalypse.common.entity.projectile;

import com.toast.apocalypse.common.entity.living.DestroyerEntity;
import com.toast.apocalypse.common.misc.DestroyerExplosionContext;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * This is the type of fireball shot by destroyers. It is capable of destroying any block (except bedrock).
 */
public class DestroyerFireballEntity extends AbstractFireballEntity {

    /** The explosion power for this fireball */
    private int explosionPower = 1;
    /** The time until this fireball explodes. Set when reflected.
     *  This makes it harder to damage the destroyer with it's own fireball. */
    private int fuseTime = -1;

    public DestroyerFireballEntity(EntityType<? extends AbstractFireballEntity> entityType, World world) {
        super(entityType, world);
    }

    public DestroyerFireballEntity(World world, DestroyerEntity destroyer, double x, double y, double z) {
        super(ApocalypseEntities.DESTROYER_FIREBALL.get(), destroyer, x, y, z, world);
        this.explosionPower = destroyer.getExplosionPower();
    }

    /**
     * Helper method for creating the destroyer
     * explosion that can destroy any type of block.
     */
    public static void destroyerExplosion(World world, Entity entity, DamageSource damageSource, double x, double y, double z, float explosionPower) {
        Explosion.Mode mode = ForgeEventFactory.getMobGriefingEvent(world, entity) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
        world.explode(entity, damageSource, new DestroyerExplosionContext(), x, y, z, explosionPower, false, mode);
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (result.getType() == RayTraceResult.Type.ENTITY) {
            EntityRayTraceResult entityResult = (EntityRayTraceResult) result;
            Entity entity = entityResult.getEntity();
            DamageSource directImpact = DamageSource.fireball(this, this.getOwner());
            entity.hurt(directImpact, 4.0F);

            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                boolean damageBlocked = livingEntity.isDamageSourceBlocked(directImpact);
                final int armorDamage = 100;

                // Deal heavy damage to shield, if blocking
                if (damageBlocked) {
                    livingEntity.hurtCurrentlyUsedShield(armorDamage);
                }
                // Deal heavy damage to armor, if not blocking
                else {
                    if (livingEntity instanceof ServerPlayerEntity) {
                        ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;

                        for (ItemStack armorStack : livingEntity.getArmorSlots()) {
                            armorStack.hurt(armorDamage, player.getRandom(), player);
                        }
                    }
                    else {
                        for (ItemStack armorStack : livingEntity.getArmorSlots()) {
                            armorStack.hurtAndBreak(armorDamage, livingEntity, (e) -> {
                                if (armorStack.getEquipmentSlot() != null)
                                    e.broadcastBreakEvent(armorStack.getEquipmentSlot());
                            });
                        }
                    }
                }
            }
        }
        if (!this.level.isClientSide) {
            destroyerExplosion(this.getCommandSenderWorld(), this, DamageSource.fireball(this, this.getOwner()), this.getX(), this.getY(), this.getZ(), this.explosionPower);
            this.remove();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.fuseTime >= 0 && --this.fuseTime < 0) {
            // Could have called this.onHit() here with a miss
            // BlockRayTraceResult, but just in case some other mod
            // wants to use the dummy info that would be parsed, lets not.
            // Did that explanation make sense? Probably not.
            if (!this.level.isClientSide) {
                destroyerExplosion(this.getCommandSenderWorld(), this, DamageSource.fireball(this, this.getOwner()), this.getX(), this.getY(), this.getZ(), this.explosionPower);
                this.remove();
            }
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damage) {
        if (this.isInvulnerableTo(damageSource))
            return false;

        this.markHurt();

        if (this.fuseTime < 0 && damageSource.getEntity() != null) {
            // Reflect fireball and set fuse time
            Entity entity = damageSource.getEntity();
            Vector3d vec = entity.getLookAngle();
            entity.getCommandSenderWorld().playSound(null, this.blockPosition(), SoundEvents.TNT_PRIMED, SoundCategory.NEUTRAL, 0.8F, 1.0F);
            this.fuseTime = 10;
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
        compoundNBT.putInt("ExplosionPower", this.explosionPower);
    }

    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("ExplosionPower", 99)) {
            this.explosionPower = compoundNBT.getInt("ExplosionPower");
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
