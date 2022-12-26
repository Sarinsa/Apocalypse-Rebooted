package com.toast.apocalypse.common.mixin;

import com.toast.apocalypse.common.misc.mixin_work.CommonMixinHooks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Creds to gudenau (a cool guy in the Fabric Discord server) for helping with this.
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Unique
    private DamageSource apocalypseDamageSource;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    private void onHurt(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir){
        apocalypseDamageSource = damageSource;
    }

    @ModifyVariable(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;onPlayerAttack(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/DamageSource;F)Z"), ordinal = 0, argsOnly = true)
    public float modifyDamage(float damage){
        damage = CommonMixinHooks.livingEntityHurtModifyArg(apocalypseDamageSource, (PlayerEntity)(Object) this, damage);
        apocalypseDamageSource = null;
        return damage;
    }
}