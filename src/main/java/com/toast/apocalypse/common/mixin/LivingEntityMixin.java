package com.toast.apocalypse.common.mixin;

import com.toast.apocalypse.common.misc.mixin_work.CommonMixinHooks;
import com.toast.apocalypse.common.register.ApocalypseEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.potion.Effect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    public abstract boolean hasEffect(Effect effect);

    public LivingEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method = "travel", at = @At("STORE"), index = 4)
    public ModifiableAttributeInstance getGravityAttribute(ModifiableAttributeInstance attributeInstance) {
        return CommonMixinHooks.livingEntityOnTravelModifyVariable(attributeInstance, this.hasEffect(ApocalypseEffects.HEAVY.get()));
    }
}
