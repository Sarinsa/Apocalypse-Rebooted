package com.toast.apocalypse.common.core.difficulty;

import com.toast.apocalypse.common.core.config.MobBuffingConfig;
import com.toast.apocalypse.common.core.config.field.DifficultyRegistryEntryListField;
import com.toast.apocalypse.common.core.config.value.DifficultyRegListEntry;
import com.toast.apocalypse.common.util.DataStructureUtils;
import com.toast.apocalypse.common.util.References;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

import static com.toast.apocalypse.common.core.config.ApocalypseConfig.MOB_BUFFING;

public final class MobEquipmentHandler {

    public static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[] {
            EquipmentSlot.FEET,
            EquipmentSlot.LEGS,
            EquipmentSlot.CHEST,
            EquipmentSlot.HEAD
    };

    public static final Map<Integer, Map<EquipmentSlot, List<Item>>> ARMOR_MAPS = new HashMap<>();


    /**
     * Handles Apocalypse equipment for mobs when they spawn, such as weapon and armor.<br>
     * Called from {@link com.toast.apocalypse.common.event.EntityEvents#onEntityJoinWorld(EntityJoinLevelEvent)}
     *
     * @param entity The entity to handle equipment for.
     * @param difficulty The raw difficulty of the nearest player.
     * @param fullMoon True if it is both nighttime and a full moon in the level the entity is in.
     * @param random The RNG of the level the entity is in.
     */
    public static void handleMobEquipment(LivingEntity entity, long difficulty, boolean fullMoon, RandomSource random) {
        EntityType<?> entityType = entity.getType();

        // Try to equip a weapon
        if (MOB_BUFFING.EQUIPMENT.canReceiveWeapons.contains(entityType)) {
            double effectiveDifficulty = (double) (difficulty / References.DAY_LENGTH) / MOB_BUFFING.EQUIPMENT.weaponsDifficultySpan.get();
            double chance = MOB_BUFFING.EQUIPMENT.weaponsChance.get() * effectiveDifficulty;

            final double maxWeaponChance = MOB_BUFFING.EQUIPMENT.weaponsMaxChance.get();

            if (fullMoon) {
                chance += MOB_BUFFING.EQUIPMENT.weaponsLunarChance.get();
            }
            if (maxWeaponChance >= 0.0 && chance > maxWeaponChance) {
                chance = maxWeaponChance;
            }
            if (random.nextDouble() <= chance) {
                equipWeapon(entity, difficulty, random);
            }
        }

        // Try to equip a suitable set of armor
        if (MOB_BUFFING.EQUIPMENT.canReceiveArmor.contains(entityType)) {
            double effectiveDifficulty = (double) (difficulty / References.DAY_LENGTH) / MOB_BUFFING.EQUIPMENT.armorDifficultySpan.get();
            double chance = MOB_BUFFING.EQUIPMENT.armorChance.get() * effectiveDifficulty;

            final double maxArmorChance = MOB_BUFFING.EQUIPMENT.armorMaxChance.get();

            if (fullMoon) {
                chance += MOB_BUFFING.EQUIPMENT.armorLunarChance.get();
            }
            if (maxArmorChance >= 0.0 && chance > maxArmorChance) {
                chance = maxArmorChance;
            }
            if (random.nextDouble() <= chance) {
                equipArmor(entity, difficulty, random);
            }
        }
    }

    /**
     * Attempts to pick a suitable weapon from the equipment config
     * and equip it on the given entity.
     *
     * @param entity The entity to try and equip with a weapon.
     * @param difficulty The raw difficulty of the nearest player.
     * @param random The RNG of the level object the entity is in.
     */
    private static void equipWeapon(LivingEntity entity, long difficulty, RandomSource random) {
        ItemStack weapon = null;

        if (!MOB_BUFFING.EQUIPMENT.weaponTierList.isEmpty()) {
            if (MOB_BUFFING.EQUIPMENT.currentWeaponTierOnly.get()) {
                Item item = DataStructureUtils.getRandomListElement(random, MOB_BUFFING.EQUIPMENT.weaponTierList.getClosestValues(difficulty));

                if (item != null) {
                    weapon = new ItemStack(item);
                }
            }
            else {
                List<Item> items = MOB_BUFFING.EQUIPMENT.weaponTierList.getAllUntil(difficulty);
                Item item = DataStructureUtils.getRandomListElement(random, items);

                if (item != null) {
                    weapon = new ItemStack(item);
                }
            }
        }
        if (weapon != null) {
            entity.setItemInHand(InteractionHand.MAIN_HAND, weapon);
        }
    }

    /**
     * Attempts to pick a suitable set of armor from the equipment config to equip the given entity with.
     *
     * @param entity The entity to try and equip with a weapon.
     * @param difficulty The raw difficulty of the nearest player.
     * @param random The RNG of the level the entity is in.
     */
    @SuppressWarnings("ConstantConditions")
    private static void equipArmor(LivingEntity entity, long difficulty, RandomSource random) {
        if (!ARMOR_MAPS.keySet().isEmpty()) {
            int scaledDifficulty = (int) (difficulty / References.DAY_LENGTH);
            ItemStack[] toEquip = new ItemStack[] {
                    ItemStack.EMPTY,
                    ItemStack.EMPTY,
                    ItemStack.EMPTY,
                    ItemStack.EMPTY
            };

            if (MOB_BUFFING.EQUIPMENT.currentArmorTierOnly.get()) {
                int tier = 0;

                for (int i : ARMOR_MAPS.keySet()) {
                    if (i <= scaledDifficulty) {
                        tier = i;
                    }
                }
                Map<EquipmentSlot, List<Item>> armors = ARMOR_MAPS.get(tier);
                toEquip[0] = new ItemStack(DataStructureUtils.getRandomListElement(random, armors.get(EquipmentSlot.FEET)));
                toEquip[1] = new ItemStack(DataStructureUtils.getRandomListElement(random, armors.get(EquipmentSlot.LEGS)));
                toEquip[2] = new ItemStack(DataStructureUtils.getRandomListElement(random, armors.get(EquipmentSlot.CHEST)));
                toEquip[3] = new ItemStack(DataStructureUtils.getRandomListElement(random, armors.get(EquipmentSlot.HEAD)));
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

                Map<EquipmentSlot, List<Item>> armors = ARMOR_MAPS.get(DataStructureUtils.getRandomListElement(random, availableTiers));
                toEquip[0] = new ItemStack(DataStructureUtils.getRandomListElement(random, armors.get(EquipmentSlot.FEET)));
                toEquip[1] = new ItemStack(DataStructureUtils.getRandomListElement(random, armors.get(EquipmentSlot.LEGS)));
                toEquip[2] = new ItemStack(DataStructureUtils.getRandomListElement(random, armors.get(EquipmentSlot.CHEST)));
                toEquip[3] = new ItemStack(DataStructureUtils.getRandomListElement(random, armors.get(EquipmentSlot.HEAD)));
            }
            for (int i = 0; i < toEquip.length; i++) {
                if (entity.getItemBySlot(ARMOR_SLOTS[i]).isEmpty() && !toEquip[i].isEmpty()) {
                    entity.setItemSlot(ARMOR_SLOTS[i], toEquip[i]);
                }
            }
        }
    }

    /**
     * Called in the 'armor_tier_list' config field's callback ({@link com.toast.apocalypse.common.core.config.MobBuffingConfig.Equipment}).
     */
    public static void refreshArmorMaps(DifficultyRegistryEntryListField<Item> field) {
        ARMOR_MAPS.clear();

        for (DifficultyRegListEntry<Item> entry : field.getEntries()) {
            List<Item> items = entry.getRegistryEntries(ForgeRegistries.ITEMS, null);

            if (items == null || items.isEmpty())
                continue;

            Map<EquipmentSlot, List<Item>> armorTier = new HashMap<>();
            armorTier.put(EquipmentSlot.FEET, new ArrayList<>());
            armorTier.put(EquipmentSlot.LEGS, new ArrayList<>());
            armorTier.put(EquipmentSlot.CHEST, new ArrayList<>());
            armorTier.put(EquipmentSlot.HEAD, new ArrayList<>());

            for (Item item : items) {
                EquipmentSlot equipmentSlot = item instanceof Equipable equipable
                        ? equipable.getEquipmentSlot()
                        : EquipmentSlot.HEAD;

                armorTier.get(equipmentSlot).add(item);
            }
            ARMOR_MAPS.put(entry.DIFFICULTY_LEVEL, armorTier);
        }
    }
}
