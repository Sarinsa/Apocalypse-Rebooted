package com.toast.apocalypse.common.core.difficulty;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.util.CapabilityHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

import static com.toast.apocalypse.common.core.config.ApocalypseConfig.MOB_BUFFING;

/**
 * This class takes care of applying attribute
 * increments, effects and equipment to monsters
 * depending on the nearest player's difficulty.
 */
public final class MobAttributeHandler {

    public static final UUID FLAT_HEALTH_BONUS = UUID.fromString("bbbd7d2f-ee60-43cb-83b4-addc05a8401d");
    public static final UUID MULTIPLIER_HEALTH_BONUS = UUID.fromString("b6eb9c90-4192-4ce6-ac1c-d038a2ba7c22");
    public static final UUID MULTIPLIER_SPEED_BONUS = UUID.fromString("6b90c4e9-3dee-434f-a819-a0d761003697");
    public static final UUID FLAT_KNOCKBACK_RES_BONUS = UUID.fromString("695802b9-3f4c-463b-b620-9a043c523ace");


    /**
     * Handles entity attribute modifications such as health, knockback resistance and speed bonuses.
     *
     * @param livingEntity The entity to process.
     * @param difficulty The difficulty of the nearest player.
     * @param fullMoon Whether it is nighttime and a full moon in the world that this entity spawns in.
     */
    public static void handleAttributes(LivingEntity livingEntity, long difficulty, boolean fullMoon) {
        AttributeInstance attribute;
        double effectiveDifficulty;
        double bonus;
        double mult;

        // Health
        attribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);

        if (attribute != null && !MOB_BUFFING.ATTRIBUTES.maxHealthBlacklist.contains(livingEntity.getType())) {
            float prevMax = livingEntity.getMaxHealth();
            effectiveDifficulty = (double) difficulty / MOB_BUFFING.ATTRIBUTES.healthDifficultySpan.get();

            bonus = MOB_BUFFING.ATTRIBUTES.healthFlatBonus.get() * effectiveDifficulty;
            mult = MOB_BUFFING.ATTRIBUTES.healthMultBonus.get() * effectiveDifficulty;

            final double maxFlatHealthBonus = MOB_BUFFING.ATTRIBUTES.healthFlatBonusMax.get();
            final double maxMultHealthBonus = MOB_BUFFING.ATTRIBUTES.healthMultBonusMax.get();

            if (maxFlatHealthBonus >= 0.0 && bonus > maxFlatHealthBonus) {
                bonus = maxFlatHealthBonus;
            }
            if (maxMultHealthBonus >= 0.0 && mult > maxMultHealthBonus) {
                mult = maxMultHealthBonus;
            }
            if (fullMoon) {
                bonus += MOB_BUFFING.ATTRIBUTES.healthLunarFlatBonus.get();
                mult += MOB_BUFFING.ATTRIBUTES.healthLunarMultBonus.get();
            }

            if (bonus != 0.0) {
                attribute.addPermanentModifier(new AttributeModifier(FLAT_HEALTH_BONUS, "ApocalypseFlatHEALTH", bonus, AttributeModifier.Operation.ADDITION));
            }
            if (mult != 0.0) {
                attribute.addPermanentModifier(new AttributeModifier(MULTIPLIER_HEALTH_BONUS, "ApocalypseMultHEALTH", mult, AttributeModifier.Operation.MULTIPLY_BASE));
            }
            livingEntity.setHealth(livingEntity.getHealth() + livingEntity.getMaxHealth() - prevMax);
        }

        // Speed
        attribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);

        if (attribute != null && !MOB_BUFFING.ATTRIBUTES.moveSpeedBlacklist.contains(livingEntity.getType())) {
            effectiveDifficulty = (double) difficulty / MOB_BUFFING.ATTRIBUTES.speedDifficultySpan.get();

            mult = MOB_BUFFING.ATTRIBUTES.speedMultBonus.get() * effectiveDifficulty;

            final double maxMultSpeedBonus = MOB_BUFFING.ATTRIBUTES.speedMultBonusMax.get();

            if (maxMultSpeedBonus >= 0.0 && mult > maxMultSpeedBonus) {
                mult = maxMultSpeedBonus;
            }
            if (fullMoon) {
                mult += MOB_BUFFING.ATTRIBUTES.speedLunarMultBonus.get();
            }

            if (mult != 0.0) {
                attribute.addPermanentModifier(new AttributeModifier(MULTIPLIER_SPEED_BONUS, "ApocalypseMultSPEED", mult, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }

        // Knockback resistance
        attribute = livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);

        if (attribute != null && !MOB_BUFFING.ATTRIBUTES.knockbackResBlacklist.contains(livingEntity.getType())) {
            effectiveDifficulty = (double) difficulty / MOB_BUFFING.ATTRIBUTES.knockbackResDifficultySpan.get();

            bonus = MOB_BUFFING.ATTRIBUTES.knockbackResFlatBonus.get() * effectiveDifficulty;

            final double maxFlatKnockbackResBonus = MOB_BUFFING.ATTRIBUTES.knockbackResFlatBonusMax.get();

            if (maxFlatKnockbackResBonus >= 0.0 && bonus > maxFlatKnockbackResBonus) {
                bonus = maxFlatKnockbackResBonus;
            }
            if (fullMoon) {
                bonus += MOB_BUFFING.ATTRIBUTES.knockbackResLunarFlatBonus.get();
            }

            if (bonus != 0.0) {
                attribute.addPermanentModifier(new AttributeModifier(FLAT_KNOCKBACK_RES_BONUS, "ApocalypseFlatKNOCKRES", bonus, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    /** Used in {@link com.toast.apocalypse.common.mixin.PlayerEntityMixin} */
    public static float getLivingDamage(LivingEntity attacker, Player player, float originalDamage) {
        final long difficulty = CapabilityHelper.getPlayerDifficulty(player);
        double effectiveDifficulty = (double) difficulty / MOB_BUFFING.ATTRIBUTES.damageDifficultySpan.get();

        if (!MOB_BUFFING.ATTRIBUTES.attackDamageBlacklist.contains(attacker.getType()) && effectiveDifficulty > 1) {
            boolean fullMoon = Apocalypse.INSTANCE.getDifficultyManager().isFullMoonNight();

            double bonus = MOB_BUFFING.ATTRIBUTES.damageFlatBonus.get() * effectiveDifficulty;
            double mult = MOB_BUFFING.ATTRIBUTES.damageMultBonus.get() * effectiveDifficulty;

            final double maxFlatDamageBonus = MOB_BUFFING.ATTRIBUTES.damageFlatBonusMax.get();
            final double maxMultDamageBonus = MOB_BUFFING.ATTRIBUTES.damageMultBonusMax.get();

            if (maxFlatDamageBonus >= 0.0 && bonus > maxFlatDamageBonus) {
                bonus = maxFlatDamageBonus;
            }
            if (maxMultDamageBonus >= 0.0 && mult > maxMultDamageBonus) {
                mult = maxMultDamageBonus;
            }
            if (fullMoon) {
                bonus += MOB_BUFFING.ATTRIBUTES.damageLunarFlatBonus.get();
                mult += MOB_BUFFING.ATTRIBUTES.damageLunarMultBonus.get();
            }
            double newDamage = (originalDamage * (mult + 1.0D)) + bonus;
            return (float) newDamage;
        }
        return originalDamage;
    }
}
