package com.toast.apocalypse.common.core.difficulty;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.mojang.datafixers.util.Pair;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.util.References;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public final class MobPotionHandler {

    /**
     * Updated on config load/reload
     */
    public static Map<Integer, Pair<Effect, List<EntityType<?>>>> POTIONS = new HashMap<>();
    public static double POTION_TIME;
    public static double POTION_CHANCE;
    public static double POTION_LUNAR_CHANCE;
    public static double POTION_CHANCE_MAX;

    public static void handlePotions(LivingEntity livingEntity, long difficulty, boolean fullMoon, Random random) {
        final List<Effect> availableEffects = new ArrayList<>();
        int scaledDifficulty = (int) (difficulty / References.DAY_LENGTH);

        double effectiveDifficulty = (double) (difficulty / References.DAY_LENGTH) / POTION_TIME;
        double bonus = POTION_CHANCE * effectiveDifficulty;

        if (POTION_CHANCE_MAX >= 0.0 && bonus > POTION_CHANCE_MAX) {
            bonus = POTION_CHANCE_MAX;
        }
        if (fullMoon) {
            bonus += POTION_LUNAR_CHANCE;
        }
        if (random.nextDouble() <= bonus) {
            // TODO - Temporary; reconsider
            POTIONS.forEach((difficultyUnlock, pair) -> {
                if (difficultyUnlock <= scaledDifficulty) {
                    if (!pair.getSecond().contains(livingEntity.getType())) {
                        availableEffects.add(pair.getFirst());
                    }
                }
            });
            if (availableEffects.isEmpty()) {
                return;
            }
            livingEntity.addEffect(new EffectInstance(availableEffects.get(random.nextInt(availableEffects.size())), 1000000));
        }
    }

    public static void refreshPotionMap() {
        POTIONS.clear();
        CommentedConfig config = ApocalypseCommonConfig.COMMON.getPotionMap();

        for (CommentedConfig.Entry entry : config.entrySet()) {
            String[] key = entry.getKey().split(" ");

            if (key.length != 2) {
                logError("Invalid potion effect entry \"{}\" found. A potion effect entry's key must consist of a potion effect ID first and an unlock-difficulty second, separated by a space.");
                continue;
            }
            ResourceLocation effectId = ResourceLocation.tryParse(key[0]);

            if (effectId == null) {
                logError("Invalid potion effect entry found in potion effect map: {}. Entry name must contain a potion effect ID first.", key[0]);
                continue;
            }
            Effect effect;
            int difficulty;
            List<EntityType<?>> blackList = new ArrayList<>();

            if (ForgeRegistries.POTIONS.containsKey(effectId)) {
                effect = ForgeRegistries.POTIONS.getValue(effectId);
            }
            else {
                logError("Found potion effect entry with a potion effect that does not exist in the Forge registry: {}. This potion effect entry will not be loaded.", effectId);
                continue;
            }
            if (StringUtils.isNumeric(key[1])) {
                difficulty = Integer.parseInt(key[1]);

                if (difficulty < 0 || difficulty > (References.MAX_DIFFICULTY_HARD_LIMIT / References.DAY_LENGTH)) {
                    logError("Found potion effect entry \"{}\" with invalid unlock-difficulty; value must be greater than 0 and less than the difficulty hard limit; defaulting to 0.", key);
                    difficulty = 0;
                }
            }
            else {
                logError("Found potion effect entry \"{}\" with missing or invalid unlock-difficulty; the first argument in the content list must be the unlock-difficulty. Defaulting to 0.", key);
                difficulty = 0;
            }

            if (entry.getValue() instanceof List) {
                List<? extends String> configList = entry.getValue();

                if (config.isEmpty()) {
                    logError("Found potion effect entry \"{}\" with empty content list. Entry must AT LEAST contain an unlock-difficulty level.", key);
                    continue;
                }

                for (String s : configList) {
                    ResourceLocation entityId = ResourceLocation.tryParse(s);

                    if (entityId == null) {
                        logError("Found potion effect entry \"{}\" with an invalid blacklisted entity type ({}); not a valid ResourceLocation.", key, s);
                    }
                    else {
                        if (ForgeRegistries.ENTITIES.containsKey(entityId)) {
                            blackList.add(ForgeRegistries.ENTITIES.getValue(entityId));
                        }
                        else {
                            logError("Found potion effect entry \"{}\" with blacklisted entity type \"{}\" that does not exist in the Forge registry. The entity type will not be added to the blacklist.", key, entityId);
                        }
                    }
                }
            }
            POTIONS.put(difficulty, new Pair<>(effect, blackList));
        }
    }

    private static void logError(String message, Object... args) {
        Apocalypse.LOGGER.error("[Apocalypse Config] " + message, args);
    }
}
