package com.toast.apocalypse.common.core.difficulty;

import com.toast.apocalypse.common.util.DataStructureUtils;
import com.toast.apocalypse.common.util.References;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

import java.util.List;

import static com.toast.apocalypse.common.core.config.ApocalypseConfig.MOB_BUFFING;

public final class MobPotionHandler {


    /**
     * Attempts to pick a potion/mob effect from the config and
     * apply to the given entity.<br>
     * Called from {@link com.toast.apocalypse.common.event.EntityEvents#onEntityJoinWorld(EntityJoinLevelEvent)}
     *
     * @param livingEntity The entity to find a potion effect for.
     * @param difficulty The raw difficulty of the nearest player.
     * @param fullMoon True if it is both nighttime and a full moon in the level the entity is in.
     * @param random The RNG of the level object the entity is in.
     */
    public static void handlePotions(LivingEntity livingEntity, long difficulty, boolean fullMoon, RandomSource random) {
        if (MOB_BUFFING.POTION_EFFECTS.entityBlacklist.contains(livingEntity)) return;

        if (MOB_BUFFING.POTION_EFFECTS.potionEffectList.isEmpty()) return;

        final double effectiveDifficulty = (double) (difficulty / References.DAY_LENGTH) / MOB_BUFFING.POTION_EFFECTS.potionEffectDifficultySpan.get();
        double bonus = MOB_BUFFING.POTION_EFFECTS.potionEffectChance.get() * effectiveDifficulty;

        final double maxPotionChance = MOB_BUFFING.POTION_EFFECTS.potionEffectMaxChance.get();

        if (maxPotionChance >= 0.0 && bonus > maxPotionChance) {
            bonus = maxPotionChance;
        }
        if (fullMoon) {
            bonus += MOB_BUFFING.POTION_EFFECTS.potionEffectLunarChance.get();
        }
        if (random.nextDouble() <= bonus) {
            final List<MobEffect> availableEffects = MOB_BUFFING.POTION_EFFECTS.potionEffectList.getAllUntil(difficulty);
            MobEffect mobEffect = DataStructureUtils.getRandomListElement(random, availableEffects);

            if (mobEffect != null) {
                livingEntity.addEffect(new MobEffectInstance(mobEffect, -1));
            }
        }
    }
}
