package com.toast.apocalypse.common.core.difficulty;

import com.toast.apocalypse.common.event.CommonConfigReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * This event listener takes care of applying the right
 * attributes, effects and equipment to monsters
 * depending on the nearest player's difficulty.
 */
public class MobDifficultyHandler {

    /**
     * These values are updated during common config reload.
     *
     * {@link CommonConfigReloadListener#updateInfo()}
     */
    public static List<EntityType<?>> HEALTH_BLACKLIST = new ArrayList<>();
    public static double HEALTH_TIME_SPAN;
    public static double HEALTH_FLAT_BONUS;
    public static double HEALTH_MULT_BONUS;
    public static double HEALTH_FLAT_BONUS_MAX;
    public static double HEALTH_MULT_BONUS_MAX;
    public static double HEALTH_LUNAR_FLAT_BONUS;
    public static double HEALTH_LUNAR_MULT_BONUS;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
        LivingEntity spawnedEntity = event.getEntityLiving();
        World world = spawnedEntity.getCommandSenderWorld();
        long difficulty = WorldDifficultyManager.getNearestPlayerDifficulty(world, spawnedEntity);
        boolean fullMoon = WorldDifficultyManager.isFullMoon(world) && world.isNight();

        if (difficulty <= 0)
            return;

        handleAttributes(spawnedEntity, difficulty, fullMoon);
        handlePotions(spawnedEntity, difficulty, fullMoon);
    }

    /**
     * Handles entity attribute modifications such as health, damage and speed bonuses.
     *
     * @param livingEntity The spawning entity.
     * @param difficulty The difficulty of the nearest player.
     * @param fullMoon Whether or not it is night time and a full moon in the world that this entity spawns in.
     */
    private static void handleAttributes(LivingEntity livingEntity, long difficulty, boolean fullMoon) {
        double effectiveDifficulty;
        double bonus, mult;
        ModifiableAttributeInstance attribute;

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
    }

    private static void handlePotions(LivingEntity livingEntity, long difficulty, boolean fullMoon) {

    }
}
