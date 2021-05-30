package com.toast.apocalypse.common.misc.mixin_work;

import com.toast.apocalypse.common.core.difficulty.MobDifficultyHandler;
import com.toast.apocalypse.common.misc.EntityAttributeModifiers;
import com.toast.apocalypse.common.register.ApocalypseEffects;
import com.toast.apocalypse.common.register.ApocalypseItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class CommonMixinHooks {

    public static ModifiableAttributeInstance livingEntityOnTravelModifyVariable(ModifiableAttributeInstance attributeInstance, boolean hasHeavyEffect) {
        if (hasHeavyEffect) {
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
            return MobDifficultyHandler.getLivingDamage((LivingEntity) entity, player, originalDamage);
        }
        return originalDamage;
    }
}
