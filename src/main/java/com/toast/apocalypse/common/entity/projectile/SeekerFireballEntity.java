package com.toast.apocalypse.common.entity.projectile;

import com.toast.apocalypse.common.entity.living.SeekerEntity;
import com.toast.apocalypse.common.register.ApocalypseEntities;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class SeekerFireballEntity extends AbstractFireballEntity {

    public SeekerFireballEntity(EntityType<? extends AbstractFireballEntity> entityType, World world) {
        super(entityType, world);
    }

    public SeekerFireballEntity(World world, SeekerEntity seeker, double x, double y, double z) {
        super(ApocalypseEntities.SEEKER_FIREBALL.get(), seeker, x, y, z, world);
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        Entity entity = result.getEntity();

        if (!entity.fireImmune()) {
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
        BlockPos pos = result.getBlockPos().relative(result.getDirection());
        BlockState state = this.level.getBlockState(result.getBlockPos());
        Direction direction = result.getDirection();
        LivingEntity owner = null;

        if (this.getOwner() instanceof LivingEntity) {
            owner = (LivingEntity) this.getOwner();
        }

        if (!this.level.isClientSide) {

            if (!(owner instanceof MobEntity) || this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.getEntity())) {

                if (state.isFlammable(this.level, pos, direction)) {
                    state.catchFire(this.level, pos, direction, owner);
                }
                else {
                    if (this.level.isEmptyBlock(pos)) {
                        this.level.setBlockAndUpdate(pos, AbstractFireBlock.getState(this.level, pos));
                    }
                }
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
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
