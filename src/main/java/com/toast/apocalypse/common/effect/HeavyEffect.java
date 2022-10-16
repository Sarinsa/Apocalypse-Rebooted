package com.toast.apocalypse.common.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This effect is inflicted on entities attacked by
 * ghosts, which makes them fall fast.
 */
public class HeavyEffect extends Effect {

    public HeavyEffect(EffectType effectType, int color) {
        super(effectType, color);
    }

    /**
     * This effect is merely used as a marker.
     * The actual gravity manipulation is
     * handled in {@link com.toast.apocalypse.common.mixin.LivingEntityMixin}
     */
    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        // No
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, @Nonnull LivingEntity livingEntity, int amplifier, double health) {
        // Overriding to skip unnecessary checks in super class
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
