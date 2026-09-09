package com.toast.apocalypse.common.core.difficulty;

import com.toast.apocalypse.common.core.config.field.ItemsByDifficultyMapField;
import com.toast.apocalypse.common.event.GameEventListener;
import com.toast.apocalypse.common.util.DataStructureUtils;
import fathertoast.crust.api.config.common.value.collection.ItemStackList;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.MobSpawnEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.toast.apocalypse.common.core.config.ApocalypseConfig.MOB_BUFFING;

public final class MobEquipmentHandler {
    
    public static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[] {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };
    
    public static final Map<FuzzyKey<Double>, Map<EquipmentSlot, List<ItemStack>>> ARMOR_MAPS = new HashMap<>();
    
    /**
     * Handles Apocalypse equipment for mobs when they spawn, such as weapon and armor.<br>
     * Called from {@link GameEventListener#onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn)} (EntityJoinLevelEvent)}
     *
     * @param entity           The entity to handle equipment for.
     * @param scaledDifficulty The difficulty (in days) of the nearest player.
     * @param fullMoon         True if it is both nighttime and a full moon in the level the entity is in.
     * @param random           The RNG of the level the entity is in.
     */
    public static void handleMobEquipment( LivingEntity entity, double scaledDifficulty, boolean fullMoon, RandomSource random ) {
        // Try to equip a weapon
        if( MOB_BUFFING.EQUIPMENT.weaponWhitelist.contains( entity ) ) {
            double multiplier = scaledDifficulty / MOB_BUFFING.EQUIPMENT.weaponsDifficultySpan.get();
            
            double chance = MOB_BUFFING.EQUIPMENT.weaponsChance.get() * multiplier;
            double maxWeaponChance = MOB_BUFFING.EQUIPMENT.weaponsMaxChance.get();
            if( maxWeaponChance >= 0.0 && chance > maxWeaponChance ) {
                chance = maxWeaponChance;
            }
            if( fullMoon ) {
                chance += MOB_BUFFING.EQUIPMENT.weaponsLunarChance.get();
            }
            
            if( random.nextDouble() < chance ) {
                equipWeapon( entity, scaledDifficulty, random );
            }
        }
        
        // Try to equip a suitable set of armor
        if( MOB_BUFFING.EQUIPMENT.armorWhitelist.contains( entity ) ) {
            double multiplier = scaledDifficulty / MOB_BUFFING.EQUIPMENT.armorDifficultySpan.get();
            
            double chance = MOB_BUFFING.EQUIPMENT.armorChance.get() * multiplier;
            double maxArmorChance = MOB_BUFFING.EQUIPMENT.armorMaxChance.get();
            if( maxArmorChance >= 0.0 && chance > maxArmorChance ) {
                chance = maxArmorChance;
            }
            if( fullMoon ) {
                chance += MOB_BUFFING.EQUIPMENT.armorLunarChance.get();
            }
            
            if( random.nextDouble() < chance ) {
                //TODO might be fun to have a small chance to slap a random trim on the equipped armor
                equipArmor( entity, scaledDifficulty, random );
            }
        }
        
        // Try to enchant equipment
        if( MOB_BUFFING.EQUIPMENT.enchantWhitelist.contains( entity ) ) {
            double multiplier = scaledDifficulty / MOB_BUFFING.EQUIPMENT.enchantDifficultySpan.get();
            
            double chance = MOB_BUFFING.EQUIPMENT.enchantChance.get() * multiplier;
            double maxEnchantChance = MOB_BUFFING.EQUIPMENT.armorMaxChance.get();
            if( maxEnchantChance >= 0.0 && chance > maxEnchantChance ) {
                chance = maxEnchantChance;
            }
            if( fullMoon ) {
                chance += MOB_BUFFING.EQUIPMENT.enchantLunarChance.get();
            }
            
            // Loop through equipment and roll chance for each
            for( EquipmentSlot slot : EquipmentSlot.values() ) {
                if( random.nextDouble() < chance ) {
                    maybeEnchantSlot( entity, random, slot );
                }
            }
        }
    }
    
    /**
     * Attempts to enchant the item stack in the given slot for the entity.<br>
     * The level used is a random number within the range specified in the config.
     *
     * @param entity The entity to try and enchant equipment for.
     * @param random The RNG of the level object the entity is in.
     * @param slot   The equipment slot to enchant for.
     */
    private static void maybeEnchantSlot( LivingEntity entity, RandomSource random, EquipmentSlot slot ) {
        ItemStack itemStack = entity.getItemBySlot( slot );
        
        // Abort if stack is empty or already enchanted
        if( itemStack.isEmpty() || !EnchantmentHelper.getEnchantments( itemStack ).isEmpty() ) return;
        
        int level = MOB_BUFFING.EQUIPMENT.enchantLevelRange.next( random );//TODO Difficulty scale
        EnchantmentHelper.enchantItem( random, itemStack, level, false );
    }
    
    /**
     * Attempts to pick a suitable weapon from the equipment config
     * and equip it on the given entity.
     *
     * @param entity           The entity to try and equip with a weapon.
     * @param scaledDifficulty The difficulty (in days) of the nearest player.
     * @param random           The RNG of the level object the entity is in.
     */
    private static void equipWeapon( LivingEntity entity, double scaledDifficulty, RandomSource random ) {
        if( MOB_BUFFING.EQUIPMENT.weaponTierList.isEmpty() ) return;
        
        // Pick a random enabled weapon
        final List<ItemStack> availableWeapons = getAllWeaponsFor( scaledDifficulty );
        ItemStack weapon = DataStructureUtils.getRandomListValue( random, availableWeapons );
        
        // Equip the weapon if it isn't null or empty
        if( weapon != null && !weapon.isEmpty() ) {
            entity.setItemInHand( InteractionHand.MAIN_HAND, weapon );
        }
    }
    
    /** @return All weapons active for the difficulty level. */
    private static List<ItemStack> getAllWeaponsFor( double difficulty ) {
        List<ItemStack> list = new ArrayList<>();
        MOB_BUFFING.EQUIPMENT.weaponTierList.entries().forEach( entry -> {
            if( entry.matches( difficulty ) ) {
                for( ItemStack item : new ItemStackList( entry.get() ).entries() ) {
                    if( item != null && !item.isEmpty() ) list.add( item );
                }
            }
        } );
        return list;
    }
    
    /**
     * Attempts to pick a suitable set of armor from the equipment config to equip the given entity with.
     *
     * @param entity           The entity to try and equip with a weapon.
     * @param scaledDifficulty The difficulty (in days) of the nearest player.
     * @param random           The RNG of the level the entity is in.
     */
    private static void equipArmor( LivingEntity entity, double scaledDifficulty, RandomSource random ) {
        if( ARMOR_MAPS.isEmpty() ) return;
        
        // Pick a random enabled armor tier
        List<Map<EquipmentSlot, List<ItemStack>>> availableTiers = getAllArmorTiersFor( scaledDifficulty );
        if( availableTiers.isEmpty() ) return;
        Map<EquipmentSlot, List<ItemStack>> armorTier = DataStructureUtils.getRandomListValue( random, availableTiers );
        if( armorTier == null ) return;
        
        // Try to equip armor for each slot
        for( EquipmentSlot slot : ARMOR_SLOTS ) {
            ItemStack equip = DataStructureUtils.getRandomListValue( random, armorTier.get( slot ) );
            if( equip != null && !equip.isEmpty() && entity.getItemBySlot( slot ).isEmpty() ) {
                entity.setItemSlot( slot, equip.copy() ); // Copy; the armor item stacks are only generated on config load
            }
        }
    }
    
    /** @return All armor tiers active for the difficulty level. */
    private static List<Map<EquipmentSlot, List<ItemStack>>> getAllArmorTiersFor( double scaledDifficulty ) {
        List<Map<EquipmentSlot, List<ItemStack>>> list = new ArrayList<>();
        ARMOR_MAPS.forEach( ( key, value ) -> {
            if( key.matches( scaledDifficulty ) ) list.add( value );
        } );
        return list;
    }
    
    /**
     * Called in the 'armor.tier_list' config field's callback
     * ({@link com.toast.apocalypse.common.core.config.MobBuffingConfig.Equipment}).
     */
    public static void refreshArmorMaps( ItemsByDifficultyMapField field ) {
        // Clear map of existing entries
        ARMOR_MAPS.clear();
        
        // Loop through all tiers
        for( FuzzyEntry<Double, List<FuzzyKey<ItemStack>>> entry : field.entries() ) {
            
            final Map<EquipmentSlot, List<ItemStack>> armorTier = new HashMap<>();
            for( EquipmentSlot armorSlot : ARMOR_SLOTS ) {
                armorTier.put( armorSlot, new ArrayList<>() );
            }
            
            boolean hasItems = false;
            for( ItemStack item : new ItemStackList( entry.get() ).entries() ) {
                if( item != null && !item.isEmpty() ) {
                    // If an item is not normally considered gear, put it on the entity's head :)
                    EquipmentSlot equipmentSlot = item.getItem() instanceof Equipable equipable ?
                            equipable.getEquipmentSlot() : EquipmentSlot.HEAD;
                    
                    armorTier.get( equipmentSlot ).add( item );
                    hasItems = true;
                }
            }
            if( hasItems ) ARMOR_MAPS.put( entry.wrappedKey(), armorTier );
        }
    }
}