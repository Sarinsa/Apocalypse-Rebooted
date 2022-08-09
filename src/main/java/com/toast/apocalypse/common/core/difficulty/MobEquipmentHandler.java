package com.toast.apocalypse.common.core.difficulty;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.common.collect.Lists;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.util.References;
import com.toast.apocalypse.common.util.StorageUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.*;

public final class MobEquipmentHandler {

    private static final EquipmentSlotType[] ARMOR_SLOTS = new EquipmentSlotType[] {
            EquipmentSlotType.FEET,
            EquipmentSlotType.LEGS,
            EquipmentSlotType.CHEST,
            EquipmentSlotType.HEAD
    };

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


    public static double ARMOR_TIME;
    public static double ARMOR_CHANCE;
    public static double ARMOR_LUNAR_CHANCE;
    public static double ARMOR_CHANCE_MAX;
    public static boolean CURRENT_ARMOR_TIER_ONLY;

    public static final List<EntityType<?>> CAN_HAVE_ARMOR = new ArrayList<>();
    public static final Map<Integer, Map<EquipmentSlotType, List<Item>>> ARMOR_MAPS = new HashMap<>();


    public static void handleMobEquipment(LivingEntity entity, long difficulty, boolean fullMoon, Random random) {
        EntityType<?> entityType = entity.getType();

        if (CAN_HAVE_WEAPONS.contains(entityType)) {
            double effectiveDifficulty = (double) (difficulty / References.DAY_LENGTH) / WEAPONS_TIME;
            double bonus = WEAPONS_CHANCE * effectiveDifficulty;

            if (WEAPONS_CHANCE_MAX >= 0.0 && bonus > WEAPONS_CHANCE_MAX) {
                bonus = WEAPONS_CHANCE_MAX;
            }
            if (fullMoon) {
                bonus += WEAPONS_LUNAR_CHANCE;
            }
            if (random.nextDouble() <= bonus) {
                equipWeapon(entity, difficulty, random);
            }
        }
        if (CAN_HAVE_ARMOR.contains(entityType)) {
            double effectiveDifficulty = (double) (difficulty / References.DAY_LENGTH) / ARMOR_TIME;
            double bonus = ARMOR_CHANCE * effectiveDifficulty;

            if (ARMOR_CHANCE_MAX >= 0.0 && bonus > ARMOR_CHANCE_MAX) {
                bonus = ARMOR_CHANCE_MAX;
            }
            if (fullMoon) {
                bonus += ARMOR_LUNAR_CHANCE;
            }
            if (random.nextDouble() <= bonus) {
                equipArmor(entity, difficulty, random);
            }
        }
    }

    /**
     * Returns a new ItemStack of a weapon in the weapons lists.
     * What weapon is chosen depends on the parsed difficulty.
     */
    private static void equipWeapon(LivingEntity entity, long difficulty, Random random) {
        int scaledDifficulty = (int) (difficulty / References.DAY_LENGTH);
        ItemStack weapon = null;

        if (!WEAPON_LISTS.keySet().isEmpty()) {
            if (CURRENT_WEAPON_TIER_ONLY) {
                int tier = 0;

                for (int i : WEAPON_LISTS.keySet()) {
                    if (i <= scaledDifficulty) {
                        tier = i;
                    }
                }
                List<Item> weaponList = WEAPON_LISTS.get(tier);
                Item item = StorageUtils.getRandomListElement(random, weaponList);

                if (item != null) {
                    weapon = new ItemStack(item);
                }
            }
            else {
                List<Integer> availableTiers = new ArrayList<>();

                for (int tier : WEAPON_LISTS.keySet()) {
                    if (tier <= scaledDifficulty) {
                        availableTiers.add(tier);
                    }
                }
                if (availableTiers.isEmpty())
                    return;

                List<Item> weaponList = WEAPON_LISTS.get(StorageUtils.getRandomListElement(random, availableTiers));
                Item item = StorageUtils.getRandomListElement(random, weaponList);

                if (item != null) {
                    weapon = new ItemStack(item);
                }
            }
        }
        if (weapon != null) {
            entity.setItemInHand(Hand.MAIN_HAND, weapon);
        }
    }

    private static void equipArmor(LivingEntity entity, long difficulty, Random random) {
        Apocalypse.LOGGER.info("Armor config: " + ARMOR_MAPS);

        int scaledDifficulty = (int) (difficulty / References.DAY_LENGTH);
        ItemStack[] toEquip = new ItemStack[] {
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        };

        if (!ARMOR_MAPS.keySet().isEmpty()) {
            if (CURRENT_ARMOR_TIER_ONLY) {
                int tier = 0;

                for (int i : ARMOR_MAPS.keySet()) {
                    if (i <= scaledDifficulty) {
                        tier = i;
                    }
                }
                Map<EquipmentSlotType, List<Item>> armors = ARMOR_MAPS.get(tier);
                toEquip[0] = new ItemStack(StorageUtils.getRandomListElement(random, armors.get(EquipmentSlotType.FEET)));
                toEquip[1] = new ItemStack(StorageUtils.getRandomListElement(random, armors.get(EquipmentSlotType.LEGS)));
                toEquip[2] = new ItemStack(StorageUtils.getRandomListElement(random, armors.get(EquipmentSlotType.CHEST)));
                toEquip[3] = new ItemStack(StorageUtils.getRandomListElement(random, armors.get(EquipmentSlotType.HEAD)));
            }
            else {
                List<Integer> availableTiers = new ArrayList<>();

                for (int tier : ARMOR_MAPS.keySet()) {
                    if (tier <= scaledDifficulty) {
                        availableTiers.add(tier);
                    }
                }
                if (availableTiers.isEmpty())
                    return;

                Map<EquipmentSlotType, List<Item>> armors = ARMOR_MAPS.get(StorageUtils.getRandomListElement(random, availableTiers));
                toEquip[0] = new ItemStack(StorageUtils.getRandomListElement(random, armors.get(EquipmentSlotType.FEET)));
                toEquip[1] = new ItemStack(StorageUtils.getRandomListElement(random, armors.get(EquipmentSlotType.LEGS)));
                toEquip[2] = new ItemStack(StorageUtils.getRandomListElement(random, armors.get(EquipmentSlotType.CHEST)));
                toEquip[3] = new ItemStack(StorageUtils.getRandomListElement(random, armors.get(EquipmentSlotType.HEAD)));
            }
        }
        for (int i = 0; i < toEquip.length; i++) {
            if (entity.getItemBySlot(ARMOR_SLOTS[i]).isEmpty() && !toEquip[i].isEmpty()) {
                entity.setItemSlot(ARMOR_SLOTS[i], toEquip[i]);
            }
        }
    }

    /** Fetches an equipment config section and parses it into actual lists with items. */
    public static void refreshWeaponLists() {
        WEAPON_LISTS.clear();
        CommentedConfig weaponConfig = ApocalypseCommonConfig.COMMON.getWeaponList();

        for (CommentedConfig.Entry entry : weaponConfig.entrySet()) {
            String key = entry.getKey();

            if (StringUtils.isNumeric(key)) {
                int difficulty = Integer.parseInt(key);

                if (difficulty < 0) {
                    Apocalypse.LOGGER.warn("Weapon list tier found with negative difficulty: {}. This weapon tier will not be loaded.", difficulty);
                    continue;
                }
                long difficultyLimit = (References.MAX_DIFFICULTY_HARD_LIMIT / References.DAY_LENGTH);

                if (difficulty > difficultyLimit) {
                    Apocalypse.LOGGER.warn("Equipment list tier found with difficulty that exceeds the maximum difficulty limit of {}. This weapon tier will not be loaded.", difficultyLimit);
                    continue;
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
    }

    // Map<Integer, Map<EquipmentSlotType, List<Item>>>
    public static void refreshArmorMaps() {
        ARMOR_MAPS.clear();
        CommentedConfig armorConfig = ApocalypseCommonConfig.COMMON.getArmorList();

        for (CommentedConfig.Entry entry : armorConfig.entrySet()) {
            String key = entry.getKey();

            if (StringUtils.isNumeric(key)) {
                int difficulty = Integer.parseInt(key);

                if (difficulty < 0) {
                    Apocalypse.LOGGER.warn("Armor list tier found with negative difficulty: {}. This armor tier will not be loaded.", difficulty);
                    continue;
                }
                long difficultyLimit = (References.MAX_DIFFICULTY_HARD_LIMIT / References.DAY_LENGTH);

                if (difficulty > difficultyLimit) {
                    Apocalypse.LOGGER.warn("Equipment list tier found with difficulty that exceeds the maximum difficulty limit of {}. This weapon tier will not be loaded.", difficultyLimit);
                    continue;
                }

                if (entry.getValue() instanceof List) {
                    List<? extends String> configList = entry.getValue();
                    Map<EquipmentSlotType, List<Item>> armor = new HashMap<>();
                    armor.put(EquipmentSlotType.FEET, new ArrayList<>());
                    armor.put(EquipmentSlotType.LEGS, new ArrayList<>());
                    armor.put(EquipmentSlotType.CHEST, new ArrayList<>());
                    armor.put(EquipmentSlotType.HEAD, new ArrayList<>());

                    // WOWOWOWOWOWO
                    for (String s : configList) {
                        ResourceLocation itemId = ResourceLocation.tryParse(s);

                        if (itemId == null) {
                            Apocalypse.LOGGER.error("Armor tier list for difficulty {} contains a malformed item id: \"{}\"", key, s);
                        }
                        else {
                            if (ForgeRegistries.ITEMS.containsKey(itemId)) {
                                Item item = ForgeRegistries.ITEMS.getValue(itemId); assert item != null;
                                @Nullable EquipmentSlotType slotType = item instanceof ArmorItem ? ((ArmorItem)item).getSlot() : item.getEquipmentSlot(new ItemStack(item));

                                // Default to head slot
                                if (slotType == null || slotType.getType() != EquipmentSlotType.Group.ARMOR) {
                                    slotType = EquipmentSlotType.HEAD;
                                }
                                armor.get(slotType).add(item);
                            }
                            else {
                                Apocalypse.LOGGER.error("Armor tier list for difficulty {} contains an item id for an item that does not exist in the game: \"{}\"", key, itemId);
                            }
                        }
                    }
                    ARMOR_MAPS.put(difficulty, armor);
                }
                else {
                    Apocalypse.LOGGER.error("Armor tier list for difficulty {} is malformed and will not be loaded.", key);
                }
            }
            else {
                Apocalypse.LOGGER.error("Armor lists config entry {} is invalid; should be a number representing a difficulty level.", key);
            }
        }
    }
}
