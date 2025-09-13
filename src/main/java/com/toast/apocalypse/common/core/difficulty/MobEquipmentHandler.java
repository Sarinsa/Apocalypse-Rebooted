package com.toast.apocalypse.common.core.difficulty;

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
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Called from {@link com.toast.apocalypse.common.event.EntityEvents#onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn)} (EntityJoinLevelEvent)}
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
        // Weapon tier list is empty, abort
        if (MOB_BUFFING.EQUIPMENT.weaponTierList.isEmpty()) return;

        ItemStack weapon = null;

        // Check if we are only picking from the current tier
        if (MOB_BUFFING.EQUIPMENT.currentWeaponTierOnly.get()) {
            Item item = DataStructureUtils.getRandomListValue(random, MOB_BUFFING.EQUIPMENT.weaponTierList.getClosestValues(difficulty));

            if (item != null) {
                weapon = new ItemStack(item);
            }
        }
        // All tiers are viable, lets pick a random tier!
        else {
            final List<Item> items = MOB_BUFFING.EQUIPMENT.weaponTierList.getAllUntil(difficulty);
            Item item = DataStructureUtils.getRandomListValue(random, items);

            if (item != null) {
                weapon = new ItemStack(item);
            }
        }
        // Equip the weapon if it isn't null or empty
        if (weapon != null && !weapon.isEmpty()) {
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
        // No armor tiers defined, abort
        if (ARMOR_MAPS.keySet().isEmpty()) return;

        int scaledDifficulty = (int) (difficulty / References.DAY_LENGTH);
        ItemStack[] toEquip = new ItemStack[] {
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY,
                ItemStack.EMPTY
        };

        // Check if we are only picking from the current armor tier
        if (MOB_BUFFING.EQUIPMENT.currentArmorTierOnly.get()) {
            int tier = 0;

            for (int i : ARMOR_MAPS.keySet()) {
                if (i <= scaledDifficulty) {
                    tier = i;
                }
            }
            Map<EquipmentSlot, List<Item>> armors = ARMOR_MAPS.get(tier);

            if (armors != null) {
            toEquip[0] = new ItemStack(DataStructureUtils.getRandomListValue(random, armors.get(EquipmentSlot.FEET)));
            toEquip[1] = new ItemStack(DataStructureUtils.getRandomListValue(random, armors.get(EquipmentSlot.LEGS)));
            toEquip[2] = new ItemStack(DataStructureUtils.getRandomListValue(random, armors.get(EquipmentSlot.CHEST)));
            toEquip[3] = new ItemStack(DataStructureUtils.getRandomListValue(random, armors.get(EquipmentSlot.HEAD)));
            }
        }
        // All tiers are viable, lets pick a random tier!
        else {
            final List<Integer> viableTiers = new ArrayList<>();

            // Filter out tiers that haven't been reached
            for (int tier : ARMOR_MAPS.keySet()) {
                if (tier <= scaledDifficulty) {
                    viableTiers.add(tier);
                }
            }
            // No viable tiers were found, abort
            if (viableTiers.isEmpty()) return;

            final Map<EquipmentSlot, List<Item>> armors = ARMOR_MAPS.get(DataStructureUtils.getRandomListValue(random, viableTiers));

            if (armors != null) {
                toEquip[0] = new ItemStack(DataStructureUtils.getRandomListValue(random, armors.get(EquipmentSlot.FEET)));
                toEquip[1] = new ItemStack(DataStructureUtils.getRandomListValue(random, armors.get(EquipmentSlot.LEGS)));
                toEquip[2] = new ItemStack(DataStructureUtils.getRandomListValue(random, armors.get(EquipmentSlot.CHEST)));
                toEquip[3] = new ItemStack(DataStructureUtils.getRandomListValue(random, armors.get(EquipmentSlot.HEAD)));
            }
        }
        // Equip the armor we picked, if any
        for (int i = 0; i < toEquip.length; i++) {
            ItemStack armorPiece = toEquip[i];

            if (entity.getItemBySlot(ARMOR_SLOTS[i]).isEmpty() && armorPiece != null && !armorPiece.isEmpty()) {
                entity.setItemSlot(ARMOR_SLOTS[i], armorPiece);
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
