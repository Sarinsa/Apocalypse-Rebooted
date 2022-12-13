package com.toast.apocalypse.common.mixin;

import com.toast.apocalypse.common.misc.mixin_work.CommonMixinHooks;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IChargeableMob;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends MonsterEntity implements IChargeableMob {

    protected CreeperEntityMixin(EntityType<? extends CreeperEntity> entityType, World world) {
        super(entityType, world);
    }


    /**
     * A somewhat wonky solution to preventing
     * Creepers from spawning lingering effect
     * clouds that can grant "infinite" buffs.
     */
    @Redirect(
            method = "spawnLingeringCloud",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addFreshEntity(Lnet/minecraft/entity/Entity;)Z"))
    public boolean redirectSpawnLingeringCloud(World world, Entity entity) {
        CommonMixinHooks.capAreaEffectCloudDurations((AreaEffectCloudEntity) entity);
        return world.addFreshEntity(entity);
    }
}
