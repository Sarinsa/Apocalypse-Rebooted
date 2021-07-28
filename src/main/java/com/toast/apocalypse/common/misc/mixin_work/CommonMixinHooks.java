package com.toast.apocalypse.common.misc.mixin_work;

import com.toast.apocalypse.common.core.difficulty.MobAttributeHandler;
import com.toast.apocalypse.common.misc.EntityAttributeModifiers;
import com.toast.apocalypse.common.register.ApocalypseEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;

public class CommonMixinHooks {

    public static ModifiableAttributeInstance livingEntityOnTravelModifyVariable(ModifiableAttributeInstance attributeInstance, LivingEntity entity, long airborneTime) {
        if (entity.hasEffect(ApocalypseEffects.HEAVY.get()) && airborneTime >= 10) {
            if (!attributeInstance.hasModifier(EntityAttributeModifiers.HEAVY)) {
                attributeInstance.addTransientModifier(EntityAttributeModifiers.HEAVY);
            }
        }
        else if (attributeInstance.hasModifier(EntityAttributeModifiers.HEAVY)) {
            attributeInstance.removeModifier(EntityAttributeModifiers.HEAVY);
        }
        return attributeInstance;
    }

    public static float livingEntityHurtModifyArg(DamageSource damageSource, PlayerEntity player, float originalDamage) {
        Entity entity = damageSource.getEntity();

        if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
            return MobAttributeHandler.getLivingDamage((LivingEntity) entity, player, originalDamage);
        }
        return originalDamage;
    }
}
