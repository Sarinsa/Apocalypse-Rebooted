package com.toast.apocalypse.common.mixin;

import com.toast.apocalypse.common.misc.mixin_work.CommonMixinHooks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private long timeAirborne = 0;

    @ModifyVariable(method = "travel", at = @At("STORE"), index = 4)
    public ModifiableAttributeInstance getGravityAttribute(ModifiableAttributeInstance attributeInstance) {
        return CommonMixinHooks.livingEntityOnTravelModifyVariable(attributeInstance, (LivingEntity) (Object) this, this.timeAirborne);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    public void onAiStep(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!entity.isOnGround())
            ++this.timeAirborne;
        else {
            this.timeAirborne = 0;
        }
    }
}
