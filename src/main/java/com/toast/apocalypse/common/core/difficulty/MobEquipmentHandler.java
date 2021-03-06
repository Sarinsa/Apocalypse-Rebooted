package com.toast.apocalypse.common.core.difficulty;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.util.References;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public final class MobEquipmentHandler {

    /**
     * Updated on config load/reload
     */
    public static double WEAPONS_TIME;
    public static double WEAPONS_CHANCE;
    public static double WEAPONS_LUNAR_CHANCE;
    public static double WEAPONS_CHANCE_MAX;
    public static boolean CURRENT_WEAPON_TIER_ONLY;

    public static final List<EntityType<?>> CAN_HAVE_WEAPONS = new ArrayList<>();
    public static final Map<Integer, List<Item>> WEAPON_LISTS = new HashMap<>();
    public static Set<Integer> WEAPON_TIERS = new TreeSet<>();


    public static void handleMobEquipment(LivingEntity entity, long difficulty, boolean fullMoon, Random random) {
        if (CAN_HAVE_WEAPONS.contains(entity.getType())) {
            double effectiveDifficulty = (double) (difficulty / References.DAY_LENGTH) / WEAPONS_TIME;
            double bonus = WEAPONS_CHANCE * effectiveDifficulty;

            if (WEAPONS_CHANCE_MAX >= 0.0 && bonus > WEAPONS_CHANCE_MAX) {
                bonus = WEAPONS_CHANCE_MAX;
            }
            if (fullMoon) {
                bonus += WEAPONS_LUNAR_CHANCE;
            }
            if (random.nextDouble() <= bonus) {
                ItemStack weapon = getRandomWeapon(difficulty, random);
                entity.setItemSlot(EquipmentSlotType.MAINHAND, weapon);
            }
        }
    }

    /**
     * Returns a new ItemStack of a weapon in the weapons lists.
     * What weapon is chosen depends on the parsed difficulty.
     */
    private static ItemStack getRandomWeapon(long difficulty, Random random) {
        int scaledDifficulty = (int) (difficulty / References.DAY_LENGTH);

        if (!WEAPON_LISTS.keySet().isEmpty()) {

            if (CURRENT_WEAPON_TIER_ONLY) {
                int tier = 0;

                for (int i : WEAPON_TIERS) {
                    if (i <= scaledDifficulty) {
                        tier = i;
                    }
                }
                List<Item> weapons = WEAPON_LISTS.get(tier);
                return weapons != null && !weapons.isEmpty() ? new ItemStack(weapons.get(random.nextInt(weapons.size()))) : ItemStack.EMPTY;
            }
            else {
                List<Integer> availableTiers = new ArrayList<>();

                for (int tier : WEAPON_TIERS) {
                    if (tier <= scaledDifficulty) {
                        availableTiers.add(tier);
                    }
                }
                if (availableTiers.isEmpty())
                    return ItemStack.EMPTY;

                List<Item> weapons = WEAPON_LISTS.get(availableTiers.get(random.nextInt(availableTiers.size())));
                return weapons.isEmpty() ? ItemStack.EMPTY : new ItemStack(weapons.get(random.nextInt(weapons.size())));
            }
        }
        return ItemStack.EMPTY;
    }

    /** Fetches an equipment config section and parses it into actual lists with items. */
    public static void refreshEquipmentList() {
        WEAPON_LISTS.clear();
        CommentedConfig config = ApocalypseCommonConfig.COMMON.getWeaponList();

        for (CommentedConfig.Entry entry : config.entrySet()) {
            String key = entry.getKey();

            if (StringUtils.isNumeric(key)) {
                int difficulty = Integer.parseInt(key);

                if (difficulty < 0) {
                    Apocalypse.LOGGER.warn("Weapon list tier found with negative difficulty: {}. This weapon tier will not be loaded.", difficulty);
                    return;
                }
                long difficultyLimit = (References.MAX_DIFFICULTY_HARD_LIMIT / References.DAY_LENGTH);

                if (difficulty > difficultyLimit) {
                    Apocalypse.LOGGER.warn("Equipment list tier found with difficulty that exceeds the maximum difficulty limit of {}. This weapon tier will not be loaded.", difficultyLimit);
                    return;
                }

                if (entry.getValue() instanceof List) {
                    List<? extends String> configList = entry.getValue();
                    List<Item> weapons = new ArrayList<>();

                    for (String s : configList) {
                        ResourceLocation itemId = ResourceLocation.tryParse(s);

                        if (itemId == null) {
                            Apocalypse.LOGGER.error("Weapon tier list for difficulty {} contains a malformed item id: \"{}\"", key, s);
                        }
                        else {
                            if (ForgeRegistries.ITEMS.containsKey(itemId)) {
                                weapons.add(ForgeRegistries.ITEMS.getValue(itemId));
                            }
                            else {
                                Apocalypse.LOGGER.error("Weapon tier list for difficulty {} contains an item id for an item that does not exist in the game: \"{}\"", key, itemId);
                            }
                        }
                    }
                    WEAPON_LISTS.put(difficulty, weapons);
                }
                else {
                    Apocalypse.LOGGER.error("Weapon tier list for difficulty {} is malformed and will not be loaded.", key);
                }
            }
            else {
                Apocalypse.LOGGER.error("Weapon lists config entry {} is invalid; should be a number representing a difficulty level.", key);
            }
        }
        WEAPON_TIERS.clear();
        WEAPON_TIERS.addAll(WEAPON_LISTS.keySet());
    }
}
