package com.toast.apocalypse.common.misc.mixin_work;

import com.toast.apocalypse.common.core.difficulty.MobAttributeHandler;
import com.toast.apocalypse.common.core.register.ApocalypseEffects;
import com.toast.apocalypse.common.misc.EntityAttributeModifiers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;

import java.util.ArrayList;
import java.util.List;

public class CommonMixinHooks {

    public static AttributeInstance livingEntityOnTravelModifyVariable(AttributeInstance attributeInstance, LivingEntity entity, long airborneTime) {
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

    public static float livingEntityHurtModifyArg(DamageSource damageSource, Player player, float originalDamage) {
        Entity entity = damageSource.getEntity();

        if (entity instanceof LivingEntity && !(entity instanceof Player)) {
            return MobAttributeHandler.getLivingDamage((LivingEntity) entity, player, originalDamage);
        }
        return originalDamage;
    }

    public static void capAreaEffectCloudDurations(AreaEffectCloud effectCloud) {
        List<MobEffectInstance> overriddenPotionEffects = new ArrayList<>();
        final int cap = 600;

        for (MobEffectInstance effectInstance : effectCloud.potion.getEffects()) {
            if (effectInstance.getDuration() > cap) {
                overriddenPotionEffects.add(new MobEffectInstance(effectInstance.getEffect(), cap, effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.isVisible(), effectInstance.showIcon(), effectInstance.hiddenEffect, effectInstance.getFactorData()));
            }
        }
        if (!overriddenPotionEffects.isEmpty()) {
            effectCloud.setPotion(new Potion(overriddenPotionEffects.toArray(new MobEffectInstance[0])));
        }

        List<MobEffectInstance> overriddenEffects = new ArrayList<>();

        for (MobEffectInstance effectInstance : effectCloud.effects) {
            if (effectInstance.getDuration() > cap) {
                overriddenEffects.add(new MobEffectInstance(effectInstance.getEffect(), cap, effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.isVisible(), effectInstance.showIcon(), effectInstance.hiddenEffect, effectInstance.getFactorData()));
            }
        }
        if (!overriddenEffects.isEmpty()) {
            effectCloud.effects.clear();
            effectCloud.effects.addAll(overriddenEffects);
        }
    }
}
