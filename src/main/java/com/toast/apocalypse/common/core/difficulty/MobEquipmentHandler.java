package com.toast.apocalypse.common.core.difficulty;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.util.References;
import javafx.collections.transformation.SortedList;
import net.minecraft.command.impl.SummonCommand;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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


    public static void handleMobEquipment(LivingEntity entity, long difficulty, boolean fullMoon) {
        if (CAN_HAVE_WEAPONS.contains(entity.getType())) {
            double effectiveDifficulty = (double) (difficulty / References.DAY_LENGTH) / WEAPONS_TIME;
            double bonus = WEAPONS_CHANCE * effectiveDifficulty;

            if (WEAPONS_CHANCE_MAX >= 0.0 && bonus > WEAPONS_CHANCE_MAX) {
                bonus = WEAPONS_CHANCE_MAX;
            }
            if (fullMoon) {
                bonus += WEAPONS_LUNAR_CHANCE;
            }
            Apocalypse.LOGGER.info("Weapon chance: " + bonus);

            if (entity.getRandom().nextDouble() <= bonus) {
                ItemStack weapon = getRandomWeapon(difficulty);
                entity.setItemSlot(EquipmentSlotType.MAINHAND, weapon);
            }
        }
    }

    /**
     * Returns a new ItemStack of a weapon in the weapons lists.
     * What weapon is chosen depends on the parsed difficulty.
     */
    private static ItemStack getRandomWeapon(long difficulty) {
        int scaledDifficulty = (int) (difficulty / References.DAY_LENGTH);

        if (!WEAPON_LISTS.keySet().isEmpty()) {
            final Random random = new Random();

            if (CURRENT_WEAPON_TIER_ONLY) {
                Iterator<Integer> iterator = WEAPON_TIERS.iterator();
                int tier = 0;

                while(iterator.hasNext()) {
                    int i = iterator.next();
                    Apocalypse.LOGGER.info("Loop tier: " + i);

                    if (i >= scaledDifficulty) {
                        if (iterator.hasNext()) {
                            int iNext = iterator.next();

                            if (scaledDifficulty < iNext) {
                                tier = i;
                                break;
                            }
                        }
                        else {
                            tier = i;
                        }
                    }
                }
                Apocalypse.LOGGER.info("Final tier: " + tier);
                List<Item> weapons = WEAPON_LISTS.get(tier);
                return weapons.isEmpty() ? ItemStack.EMPTY : new ItemStack(weapons.get(random.nextInt(weapons.size())));
            }
            else {
                List<Integer> availableTiers = new ArrayList<>();

                for (Integer tier : WEAPON_TIERS) {
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
    public static void refreshEquipmentLists(CommentedConfig weaponListConfig, Map<Integer, List<Item>> map) {
        map.clear();

        for (CommentedConfig.Entry entry : weaponListConfig.entrySet()) {
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
                    map.put(difficulty, weapons);
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
}
