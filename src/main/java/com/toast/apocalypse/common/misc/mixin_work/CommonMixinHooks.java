package com.toast.apocalypse.common.misc.mixin_work;

import com.toast.apocalypse.common.core.difficulty.MobAttributeHandler;
import com.toast.apocalypse.common.core.register.ApocalypseEffects;
import com.toast.apocalypse.common.misc.EntityAttributeModifiers;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;

import java.util.ArrayList;
import java.util.List;

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

    public static void capAreaEffectCloudDurations(AreaEffectCloudEntity effectCloud) {
        List<EffectInstance> overriddenPotionEffects = new ArrayList<>();
        final int cap = 600;

        for (EffectInstance effectInstance : effectCloud.potion.getEffects()) {
            if (effectInstance.getDuration() > cap) {
                overriddenPotionEffects.add(new EffectInstance(effectInstance.getEffect(), cap, effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.isVisible(), effectInstance.showIcon(), effectInstance.hiddenEffect));
            }
        }
        if (!overriddenPotionEffects.isEmpty()) {
            effectCloud.setPotion(new Potion(overriddenPotionEffects.toArray(new EffectInstance[0])));
        }

        List<EffectInstance> overriddenEffects = new ArrayList<>();

        for (EffectInstance effectInstance : effectCloud.effects) {
            if (effectInstance.getDuration() > cap) {
                overriddenEffects.add(new EffectInstance(effectInstance.getEffect(), cap, effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.isVisible(), effectInstance.showIcon(), effectInstance.hiddenEffect));
            }
        }
        if (!overriddenEffects.isEmpty()) {
            effectCloud.effects.clear();
            effectCloud.effects.addAll(overriddenEffects);
        }
    }
}
