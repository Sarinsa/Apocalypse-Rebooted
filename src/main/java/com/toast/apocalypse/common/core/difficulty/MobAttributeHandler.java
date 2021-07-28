package com.toast.apocalypse.common.core.difficulty;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.IFullMoonMob;
import com.toast.apocalypse.common.event.CommonConfigReloadListener;
import com.toast.apocalypse.common.util.CapabilityHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This class takes care of applying attribute
 * increments, effects and equipment to monsters
 * depending on the nearest player's difficulty.
 */
public final class MobAttributeHandler {

    /**
     * These values are updated during common config reload.
     *
     * {@link CommonConfigReloadListener#updateInfo()}
     */

    // Health
    public static List<EntityType<?>> HEALTH_BLACKLIST = new ArrayList<>();
    public static double HEALTH_TIME_SPAN;
    public static double HEALTH_FLAT_BONUS;
    public static double HEALTH_MULT_BONUS;
    public static double HEALTH_FLAT_BONUS_MAX;
    public static double HEALTH_MULT_BONUS_MAX;
    public static double HEALTH_LUNAR_FLAT_BONUS;
    public static double HEALTH_LUNAR_MULT_BONUS;

    // Speed
    public static List<EntityType<?>> SPEED_BLACKLIST = new ArrayList<>();
    public static double SPEED_TIME_SPAN;
    public static double SPEED_MULT_BONUS;
    public static double SPEED_MULT_BONUS_MAX;
    public static double SPEED_LUNAR_MULT_BONUS;

    // Damage
    public static List<EntityType<?>> DAMAGE_BLACKLIST = new ArrayList<>();
    public static double DAMAGE_TIME_SPAN;
    public static double DAMAGE_FLAT_BONUS;
    public static double DAMAGE_MULT_BONUS;
    public static double DAMAGE_FLAT_BONUS_MAX;
    public static double DAMAGE_MULT_BONUS_MAX;
    public static double DAMAGE_LUNAR_FLAT_BONUS;
    public static double DAMAGE_LUNAR_MULT_BONUS;

    // Knockback resistance
    public static List<EntityType<?>> KNOCKBACK_BLACKLIST = new ArrayList<>();
    public static double KNOCKBACK_RES_TIME_SPAN;
    public static double KNOCKBACK_RES_FLAT_BONUS;
    public static double KNOCKBACK_RES_FLAT_BONUS_MAX;
    public static double KNOCKBACK_RES_LUNAR_FLAT_BONUS;


    /**
     * Handles entity attribute modifications such as health, damage and speed bonuses.
     *
     * @param livingEntity The spawning entity.
     * @param difficulty The difficulty of the nearest player.
     * @param fullMoon Whether or not it is night time and a full moon in the world that this entity spawns in.
     */
    public static void handleAttributes(LivingEntity livingEntity, long difficulty, boolean fullMoon) {
        ModifiableAttributeInstance attribute;
        double effectiveDifficulty;
        double bonus;
        double mult;

        // Health
        attribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);

        if (attribute != null && !HEALTH_BLACKLIST.contains(livingEntity.getType())) {
            float prevMax = livingEntity.getMaxHealth();
            effectiveDifficulty = (double) difficulty / HEALTH_TIME_SPAN;

            bonus = HEALTH_FLAT_BONUS * effectiveDifficulty;
            mult = HEALTH_MULT_BONUS * effectiveDifficulty;

            if (HEALTH_FLAT_BONUS_MAX >= 0.0 && bonus > HEALTH_FLAT_BONUS_MAX) {
                bonus = HEALTH_FLAT_BONUS_MAX;
            }
            if (HEALTH_MULT_BONUS_MAX >= 0.0 && mult > HEALTH_MULT_BONUS_MAX) {
                mult = HEALTH_MULT_BONUS_MAX;
            }
            if (fullMoon) {
                bonus += HEALTH_LUNAR_FLAT_BONUS;
                mult += HEALTH_LUNAR_MULT_BONUS;
            }

            if (bonus != 0.0) {
                attribute.addPermanentModifier(new AttributeModifier("ApocalypseFlatHEALTH", bonus, AttributeModifier.Operation.ADDITION));
            }
            if (mult != 0.0) {
                attribute.addPermanentModifier(new AttributeModifier("ApocalypseMultHEALTH", mult, AttributeModifier.Operation.MULTIPLY_BASE));
            }
            livingEntity.setHealth(livingEntity.getHealth() + livingEntity.getMaxHealth() - prevMax);
        }

        // Speed
        attribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);

        if (attribute != null && !SPEED_BLACKLIST.contains(livingEntity.getType())) {
            effectiveDifficulty = (double) difficulty / SPEED_TIME_SPAN;

            mult = SPEED_MULT_BONUS * effectiveDifficulty;

            if (SPEED_MULT_BONUS_MAX >= 0.0 && mult > SPEED_MULT_BONUS_MAX) {
                mult = SPEED_MULT_BONUS_MAX;
            }
            if (fullMoon) {
                mult += SPEED_LUNAR_MULT_BONUS;
            }

            if (mult != 0.0) {
                attribute.addPermanentModifier(new AttributeModifier("ApocalypseMultSPEED", mult, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }

        // Knockback resistance

        attribute = livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);

        if (attribute != null && !KNOCKBACK_BLACKLIST.contains(livingEntity.getType())) {
            effectiveDifficulty = (double) difficulty / KNOCKBACK_RES_TIME_SPAN;

            bonus = KNOCKBACK_RES_FLAT_BONUS * effectiveDifficulty;

            if (KNOCKBACK_RES_FLAT_BONUS_MAX >= 0.0 && bonus > KNOCKBACK_RES_FLAT_BONUS_MAX) {
                bonus = KNOCKBACK_RES_FLAT_BONUS_MAX;
            }
            if (fullMoon) {
                bonus += KNOCKBACK_RES_LUNAR_FLAT_BONUS;
            }

            if (bonus != 0.0) {
                attribute.addPermanentModifier(new AttributeModifier("ApocalypseFlatRESIST", bonus, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    /** Used in {@link com.toast.apocalypse.common.mixin.PlayerEntityMixin} */
    public static float getLivingDamage(LivingEntity attacker, PlayerEntity player, float originalDamage) {
        final long difficulty = CapabilityHelper.getPlayerDifficulty(player);

        if (!DAMAGE_BLACKLIST.contains(attacker.getType()) || difficulty <= 0) {
            double effectiveDifficulty = (double) difficulty / DAMAGE_TIME_SPAN;
            boolean fullMoon = Apocalypse.INSTANCE.getDifficultyManager().isFullMoonNight();
            double bonus, mult;

            bonus = DAMAGE_FLAT_BONUS * effectiveDifficulty;
            mult = DAMAGE_MULT_BONUS * effectiveDifficulty;

            if (DAMAGE_FLAT_BONUS_MAX >= 0.0 && bonus > DAMAGE_FLAT_BONUS_MAX) {
                bonus = DAMAGE_FLAT_BONUS_MAX;
            }
            if (DAMAGE_MULT_BONUS_MAX >= 0.0 && mult > DAMAGE_MULT_BONUS_MAX) {
                mult = DAMAGE_MULT_BONUS_MAX;
            }
            if (fullMoon) {
                bonus += DAMAGE_LUNAR_FLAT_BONUS;
                mult += DAMAGE_LUNAR_MULT_BONUS;
            }
            double newDamage = (originalDamage * (mult + 1.0D)) + bonus;
            return (float) newDamage;
        }
        return originalDamage;
    }
}
