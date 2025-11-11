package com.toast.apocalypse.common.core.config;

import com.toast.apocalypse.common.core.config.field.DifficultyRegistryEntryListField;
import com.toast.apocalypse.common.core.config.value.DifficultyRegListEntry;
import com.toast.apocalypse.common.core.config.value.DifficultyRegistryEntryList;
import com.toast.apocalypse.common.core.difficulty.MobEquipmentHandler;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.api.config.common.value.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class MobBuffingConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    public final Attributes ATTRIBUTES;
    public final Equipment EQUIPMENT;
    public final PotionEffects POTION_EFFECTS;
    
    
    /** Builds the config spec that should be used for this config. */
    public MobBuffingConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "This config contains options related to difficulty-based mob buffs, including equipment, attribute boosts, " +
                        "potion effects and more."
        );
        SPEC.fileOnlyNewLine();
        SPEC.fileOnlyComment( DifficultyRegistryEntryListField.verboseDescription() );
        SPEC.fileOnlyNewLine();
        SPEC.describeRegistryEntryList();
        SPEC.fileOnlyNewLine();
        
        GENERAL = new General( this );
        ATTRIBUTES = new Attributes( this );
        EQUIPMENT = new Equipment( this );
        POTION_EFFECTS = new PotionEffects( this );
    }
    
    //
    //   GENERAL
    //
    public static class General extends AbstractConfigCategory<MobBuffingConfig> {
        
        public final BooleanField enemiesOnly;
        
        
        General( MobBuffingConfig parent ) {
            super( parent, "general",
                    "General settings related to mob buffs." );
            
            enemiesOnly = SPEC.define( new BooleanField( "enemies_only", true,
                    "If enabled, only mobs that are considered hostile will receive potion buffs, attribute boosts and equipment." ) );
            
            SPEC.newLine();
        }
    }
    
    
    //
    //   ATTRIBUTE BOOSTS
    //
    public static class Attributes extends AbstractConfigCategory<MobBuffingConfig> {
        
        public final LazyRegistryEntryListField<EntityType<?>> maxHealthBlacklist;
        public final DoubleField healthLunarFlatBonus;
        public final DoubleField healthLunarMultBonus;
        public final DoubleField healthDifficultySpan;
        public final DoubleField healthFlatBonus;
        public final DoubleField healthFlatBonusMax;
        public final DoubleField healthMultBonus;
        public final DoubleField healthMultBonusMax;
        
        public final LazyRegistryEntryListField<EntityType<?>> attackDamageBlacklist;
        public final DoubleField damageLunarFlatBonus;
        public final DoubleField damageLunarMultBonus;
        public final DoubleField damageDifficultySpan;
        public final DoubleField damageFlatBonus;
        public final DoubleField damageFlatBonusMax;
        public final DoubleField damageMultBonus;
        public final DoubleField damageMultBonusMax;
        
        public final LazyRegistryEntryListField<EntityType<?>> moveSpeedBlacklist;
        public final DoubleField speedLunarMultBonus;
        public final DoubleField speedDifficultySpan;
        public final DoubleField speedMultBonus;
        public final DoubleField speedMultBonusMax;
        
        public final LazyRegistryEntryListField<EntityType<?>> knockbackResBlacklist;
        public final DoubleField knockbackResLunarFlatBonus;
        public final DoubleField knockbackResDifficultySpan;
        public final DoubleField knockbackResFlatBonus;
        public final DoubleField knockbackResFlatBonusMax;
        
        
        Attributes( MobBuffingConfig parent ) {
            super( parent, "attribute_boost",
                    "Settings related to attribute boosts." );
            
            SPEC.titledComment( "Max Health", "Settings for max health boost." );
            maxHealthBlacklist = SPEC.define( new LazyRegistryEntryListField<>( "max_health_blacklist", new LazyRegistryEntryList<>(
                    ForgeRegistries.ENTITY_TYPES
            ), "A list of entities that should not receive max health boost." ) );
            healthLunarFlatBonus = SPEC.define( new DoubleField( "health_lunar_flat_bonus", 5.0, DoubleField.Range.NON_NEGATIVE,
                    "The flat bonus gained from a full moon. Default is 10.0 (+10 hearts on full moons)." ) );
            healthLunarMultBonus = SPEC.define( new DoubleField( "health_lunar_mult_bonus", 0.5, DoubleField.Range.NON_NEGATIVE,
                    "The multiplier bonus gained from a full moon in percentage. Default is 0.5 (+50% on full moons)" ) );
            healthDifficultySpan = SPEC.define( new DoubleField( "health_difficulty_span", 10.0, 0.1, Double.POSITIVE_INFINITY,
                    "The difficulty value for each application of the below values." ) );
            healthFlatBonus = SPEC.define( new DoubleField( "health_flat_bonus", 0.5, DoubleField.Range.NON_NEGATIVE,
                    "The flat bonus given each levels of difficulty specified in 'health_difficulty_span'." ) );
            healthFlatBonusMax = SPEC.define( new DoubleField( "health_max_flat_bonus", -1.0, -1.0, Double.POSITIVE_INFINITY,
                    "The maximum flat bonus that can be given over time. Negative value equals no limit." ) );
            healthMultBonus = SPEC.define( new DoubleField( "health_mult_bonus", 0.1D, DoubleField.Range.NON_NEGATIVE,
                    "The multiplier bonus given each levels of difficulty specified in 'health_difficulty_span'. Default is 0.2 (+20%)." ) );
            healthMultBonusMax = SPEC.define( new DoubleField( "health_max_mult_bonus", -1.0, -1.0, Double.POSITIVE_INFINITY,
                    "The maximum multiplier bonus that can be given over time. Negative value equals no limit." ) );
            
            SPEC.newLine();
            
            SPEC.titledComment( "Attack Damage", "Settings for attack damage boost." );
            attackDamageBlacklist = SPEC.define( new LazyRegistryEntryListField<>( "attack_damage_blacklist", new LazyRegistryEntryList<>(
                    ForgeRegistries.ENTITY_TYPES
            ), "A list of entities that should not receive attack damage boost." ) );
            damageLunarFlatBonus = SPEC.define( new DoubleField( "damage_lunar_flat_bonus", 1.0, DoubleField.Range.NON_NEGATIVE,
                    "The flat bonus gained from a full moon. Default is 1.0 (+half a heart of damage on full moons)." ) );
            damageLunarMultBonus = SPEC.define( new DoubleField( "damage_lunar_mult_bonus", 0.5, DoubleField.Range.NON_NEGATIVE,
                    "The multiplier bonus gained from a full moon in percentage. Default is 0.5 (+50% on full moons)" ) );
            damageDifficultySpan = SPEC.define( new DoubleField( "damage_difficulty_span", 10.0, 0.1, Double.POSITIVE_INFINITY,
                    "The difficulty value for each application of the below values." ) );
            damageFlatBonus = SPEC.define( new DoubleField( "damage_flat_bonus", 0.5, DoubleField.Range.NON_NEGATIVE,
                    "The flat bonus given each levels of difficulty specified in 'damage_difficulty_span'." ) );
            damageFlatBonusMax = SPEC.define( new DoubleField( "damage_max_flat_bonus", -1.0, -1.0, Double.POSITIVE_INFINITY,
                    "The maximum flat bonus that can be given over time. Negative value equals no limit." ) );
            damageMultBonus = SPEC.define( new DoubleField( "damage_mult_bonus", 0.2D, DoubleField.Range.NON_NEGATIVE,
                    "The multiplier bonus given each levels of difficulty specified in 'damage_difficulty_span'. Default is 0.2 (+20%)." ) );
            damageMultBonusMax = SPEC.define( new DoubleField( "damage_max_mult_bonus", -1.0, -1.0, Double.POSITIVE_INFINITY,
                    "The maximum multiplier bonus that can be given over time. Negative value equals no limit." ) );
            
            SPEC.newLine();
            
            SPEC.titledComment( "Movement Speed", "Settings for movement speed boost." );
            moveSpeedBlacklist = SPEC.define( new LazyRegistryEntryListField<>( "movement_speed_blacklist", new LazyRegistryEntryList<>(
                    ForgeRegistries.ENTITY_TYPES, false,
                    ApocalypseEntities.GRUMP,
                    ApocalypseEntities.GHOST,
                    ApocalypseEntities.SEEKER,
                    ApocalypseEntities.DESTROYER
            ), "A list of entities that should not receive movement speed boost." ) );
            speedLunarMultBonus = SPEC.define( new DoubleField( "speed_lunar_mult_bonus", 0.1, DoubleField.Range.NON_NEGATIVE,
                    "The multiplier bonus gained from a full moon in percentage. Default is 0.1 (+10% on full moons)" ) );
            speedDifficultySpan = SPEC.define( new DoubleField( "speed_difficulty_span", 20.0, 0.1, Double.POSITIVE_INFINITY,
                    "The difficulty value for each application of the below values." ) );
            speedMultBonus = SPEC.define( new DoubleField( "speed_mult_bonus", 0.025, DoubleField.Range.NON_NEGATIVE,
                    "The multiplier bonus given each levels of difficulty specified in 'speed_difficulty_span'. Default is 0.025 (+2.5%)." ) );
            speedMultBonusMax = SPEC.define( new DoubleField( "speed_max_mult_bonus", 0.25, -1.0, Double.POSITIVE_INFINITY,
                    "The maximum multiplier bonus that can be given over time. Negative value equals no limit." ) );
            
            SPEC.newLine();
            
            SPEC.titledComment( "Knockback Resistance", "Settings for knockback resistance boost." );
            knockbackResBlacklist = SPEC.define( new LazyRegistryEntryListField<>( "knockback_resistance_blacklist", new LazyRegistryEntryList<>(
                    ForgeRegistries.ENTITY_TYPES
            ), "A list of entities that should not receive knockback resistance boost." ) );
            knockbackResLunarFlatBonus = SPEC.define( new DoubleField( "knockback_res_lunar_flat_bonus", 0.2, DoubleField.Range.NON_NEGATIVE,
                    "The flat bonus gained from a full moon in percentage. Default is 0.2 (+20% on full moons)" ) );
            knockbackResDifficultySpan = SPEC.define( new DoubleField( "knockback_res_difficulty_span", 20.0, 0.1, Double.POSITIVE_INFINITY,
                    "The difficulty value for each application of the below values." ) );
            knockbackResFlatBonus = SPEC.define( new DoubleField( "knockback_res_flat_bonus", 0.025, DoubleField.Range.NON_NEGATIVE,
                    "The multiplier bonus given each levels of difficulty specified in 'knockback_res_difficulty_span'. Default is 0.025 (+2.5%)." ) );
            knockbackResFlatBonusMax = SPEC.define( new DoubleField( "knockback_res_max_flat_bonus", 0.4, -1.0, Double.POSITIVE_INFINITY,
                    "The maximum multiplier bonus that can be given over time. Negative value equals no limit." ) );
            
            SPEC.newLine();
        }
    }
    
    //
    //   EQUIPMENT
    //
    public static class Equipment extends AbstractConfigCategory<MobBuffingConfig> {
        
        public final DifficultyRegistryEntryListField<Item> weaponTierList;
        public final RegistryEntryListField<EntityType<?>> canReceiveWeapons;
        public final DoubleField weaponsDifficultySpan;
        public final DoubleField weaponsChance;
        public final DoubleField weaponsLunarChance;
        public final DoubleField weaponsMaxChance;
        public final BooleanField currentWeaponTierOnly;
        
        public final DifficultyRegistryEntryListField<Item> armorTierList;
        public final RegistryEntryListField<EntityType<?>> canReceiveArmor;
        public final DoubleField armorDifficultySpan;
        public final DoubleField armorChance;
        public final DoubleField armorLunarChance;
        public final DoubleField armorMaxChance;
        public final BooleanField currentArmorTierOnly;
        
        public final RegistryEntryListField<EntityType<?>> canGetEnchantments;
        public final DoubleField enchantDifficultySpan;
        public final DoubleField enchantChance;
        public final DoubleField enchantLunarChance;
        public final DoubleField enchantMaxChance;
        public final IntField.RandomRange enchantLevelRange;
        
        
        Equipment( MobBuffingConfig parent ) {
            super( parent, "equipment",
                    "Settings related to equipment given to mobs." );
            
            weaponTierList = SPEC.define( new DifficultyRegistryEntryListField<>( "weapon_tier_list", new DifficultyRegistryEntryList<>( ForgeRegistries.ITEMS,
                    new DifficultyRegListEntry<>( Equipment.this.weaponTierList, 10,
                            ForgeRegistries.ITEMS.getKey( Items.WOODEN_SWORD ),
                            ForgeRegistries.ITEMS.getKey( Items.WOODEN_AXE ),
                            ForgeRegistries.ITEMS.getKey( Items.WOODEN_SHOVEL ),
                            ForgeRegistries.ITEMS.getKey( Items.WOODEN_PICKAXE )
                    ),
                    new DifficultyRegListEntry<>( Equipment.this.weaponTierList, 20,
                            ForgeRegistries.ITEMS.getKey( Items.STONE_SWORD ),
                            ForgeRegistries.ITEMS.getKey( Items.STONE_AXE ),
                            ForgeRegistries.ITEMS.getKey( Items.STONE_SHOVEL ),
                            ForgeRegistries.ITEMS.getKey( Items.STONE_PICKAXE )
                    ),
                    new DifficultyRegListEntry<>( Equipment.this.weaponTierList, 40,
                            ForgeRegistries.ITEMS.getKey( Items.GOLDEN_SWORD ),
                            ForgeRegistries.ITEMS.getKey( Items.GOLDEN_AXE ),
                            ForgeRegistries.ITEMS.getKey( Items.GOLDEN_SHOVEL ),
                            ForgeRegistries.ITEMS.getKey( Items.GOLDEN_PICKAXE )
                    ),
                    new DifficultyRegListEntry<>( Equipment.this.weaponTierList, 60,
                            ForgeRegistries.ITEMS.getKey( Items.IRON_SWORD ),
                            ForgeRegistries.ITEMS.getKey( Items.IRON_AXE ),
                            ForgeRegistries.ITEMS.getKey( Items.IRON_SHOVEL ),
                            ForgeRegistries.ITEMS.getKey( Items.IRON_PICKAXE )
                    ),
                    new DifficultyRegListEntry<>( Equipment.this.weaponTierList, 100,
                            ForgeRegistries.ITEMS.getKey( Items.DIAMOND_SWORD ),
                            ForgeRegistries.ITEMS.getKey( Items.DIAMOND_AXE ),
                            ForgeRegistries.ITEMS.getKey( Items.DIAMOND_SHOVEL ),
                            ForgeRegistries.ITEMS.getKey( Items.DIAMOND_PICKAXE )
                    ),
                    new DifficultyRegListEntry<>( Equipment.this.weaponTierList, 150,
                            ForgeRegistries.ITEMS.getKey( Items.NETHERITE_SWORD ),
                            ForgeRegistries.ITEMS.getKey( Items.NETHERITE_AXE ),
                            ForgeRegistries.ITEMS.getKey( Items.NETHERITE_SHOVEL ),
                            ForgeRegistries.ITEMS.getKey( Items.NETHERITE_PICKAXE )
                    ) ),
                    "A list of weapon items that mobs can spawn with, divided into tiers by difficulty level.",
                    "When a mob spawns, Apocalypse will try to give it a weapon from the tier with the difficulty closest to the difficulty of the nearest player.",
                    "Only mobs that have their entity type listed in 'can_receive_weapons' will be attempted given a weapon."
            ) );
            
            SPEC.newLine();
            
            canReceiveWeapons = SPEC.define( new RegistryEntryListField<>( "can_receive_weapons",
                    new RegistryEntryList<>( ForgeRegistries.ENTITY_TYPES,
                            List.of(), List.of(),
                            EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.DROWNED, EntityType.HUSK,
                            EntityType.WITHER_SKELETON, EntityType.SKELETON, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE,
                            EntityType.VINDICATOR
                    ),
                    "A list of entity types that can be given weapons from the above weapon tier list." ) );
            
            SPEC.newLine();
            
            weaponsDifficultySpan = SPEC.define( new DoubleField( "weapons_difficulty_span", 30.0, DoubleField.Range.NON_NEGATIVE,
                    "The difficulty level for each application of the fields below." ) );
            weaponsChance = SPEC.define( new DoubleField( "weapons_chance", 0.05, DoubleField.Range.PERCENT,
                    "The chance that a mob will be given a weapon when it spawns. Default is 0.05 (5% chance).",
                    "This value increases in accordance to 'weapons_difficulty_spawn'." ) );
            weaponsLunarChance = SPEC.define( new DoubleField( "weapons_lunar_chance", 0.2, DoubleField.Range.PERCENT,
                    "The additional chance gained from a full moon. Default is 0.2 (+20% chance on full moon)." ) );
            weaponsMaxChance = SPEC.define( new DoubleField( "weapons_max_chance", 0.95, DoubleField.Range.PERCENT,
                    "The maximum weapon chance that can be given over time. Default is 0.95 (95% chance)." ) );
            currentWeaponTierOnly = SPEC.define( new BooleanField( "current_weapon_tier_only", false,
                    "If enabled, only weapons from the highest difficulty tier available will be given to mobs.",
                    "When disabled, weapons will be picked randomly from all tiers." ) );
            
            SPEC.newLine();
            
            armorTierList = SPEC.define( new InjectionWrapperField<>( new DifficultyRegistryEntryListField<>( "armor_tier_list", new DifficultyRegistryEntryList<>( ForgeRegistries.ITEMS,
                    new DifficultyRegListEntry<>( Equipment.this.weaponTierList, 10,
                            ForgeRegistries.ITEMS.getKey( Items.CARVED_PUMPKIN ),
                            ForgeRegistries.ITEMS.getKey( Items.LEATHER_HELMET ),
                            ForgeRegistries.ITEMS.getKey( Items.LEATHER_CHESTPLATE ),
                            ForgeRegistries.ITEMS.getKey( Items.LEATHER_LEGGINGS ),
                            ForgeRegistries.ITEMS.getKey( Items.LEATHER_BOOTS )
                    ),
                    new DifficultyRegListEntry<>( Equipment.this.weaponTierList, 25,
                            ApocalypseItems.BUCKET_HELM.getId(),
                            ForgeRegistries.ITEMS.getKey( Items.CHAINMAIL_HELMET ),
                            ForgeRegistries.ITEMS.getKey( Items.CHAINMAIL_CHESTPLATE ),
                            ForgeRegistries.ITEMS.getKey( Items.CHAINMAIL_LEGGINGS ),
                            ForgeRegistries.ITEMS.getKey( Items.CHAINMAIL_BOOTS )
                    ),
                    new DifficultyRegListEntry<>( Equipment.this.weaponTierList, 40,
                            ForgeRegistries.ITEMS.getKey( Items.TURTLE_HELMET ),
                            ForgeRegistries.ITEMS.getKey( Items.GOLDEN_HELMET ),
                            ForgeRegistries.ITEMS.getKey( Items.GOLDEN_CHESTPLATE ),
                            ForgeRegistries.ITEMS.getKey( Items.GOLDEN_LEGGINGS ),
                            ForgeRegistries.ITEMS.getKey( Items.GOLDEN_BOOTS )
                    ),
                    new DifficultyRegListEntry<>( Equipment.this.weaponTierList, 60,
                            ForgeRegistries.ITEMS.getKey( Items.IRON_HELMET ),
                            ForgeRegistries.ITEMS.getKey( Items.IRON_CHESTPLATE ),
                            ForgeRegistries.ITEMS.getKey( Items.IRON_LEGGINGS ),
                            ForgeRegistries.ITEMS.getKey( Items.IRON_BOOTS )
                    ),
                    new DifficultyRegListEntry<>( Equipment.this.weaponTierList, 100,
                            ForgeRegistries.ITEMS.getKey( Items.DIAMOND_HELMET ),
                            ForgeRegistries.ITEMS.getKey( Items.DIAMOND_CHESTPLATE ),
                            ForgeRegistries.ITEMS.getKey( Items.DIAMOND_LEGGINGS ),
                            ForgeRegistries.ITEMS.getKey( Items.DIAMOND_BOOTS )
                    ),
                    new DifficultyRegListEntry<>( Equipment.this.weaponTierList, 150,
                            ForgeRegistries.ITEMS.getKey( Items.NETHERITE_HELMET ),
                            ForgeRegistries.ITEMS.getKey( Items.NETHERITE_CHESTPLATE ),
                            ForgeRegistries.ITEMS.getKey( Items.NETHERITE_LEGGINGS ),
                            ForgeRegistries.ITEMS.getKey( Items.NETHERITE_BOOTS )
                    ) ),
                    "A list of armor/equippable items that mobs can spawn with, divided into tiers by difficulty level.",
                    "When a mob spawns, Apocalypse will try to give it an armor piece for each equipment slot, picking items from the chosen armor tier.",
                    "Each tier can contain multiple armor pieces per equipment slot. Non-equippable items CAN be used here (will automatically be equipped on helmet/head slot).",
                    "Only mobs that have their entity type listed in 'can_receive_armor' will be attempted given armor."
            ), MobEquipmentHandler::refreshArmorMaps )
                    .field() );
            
            SPEC.newLine();
            
            canReceiveArmor = SPEC.define( new RegistryEntryListField<>( "can_receive_armor",
                    new RegistryEntryList<>( ForgeRegistries.ENTITY_TYPES,
                            List.of(), List.of(),
                            EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.DROWNED, EntityType.HUSK,
                            EntityType.WITHER_SKELETON, EntityType.SKELETON, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE
                    ),
                    "A list of entity types that can be given armor from the above armor tier list." ) );
            
            SPEC.newLine();
            
            armorDifficultySpan = SPEC.define( new DoubleField( "armor_difficulty_span", 30.0, DoubleField.Range.NON_NEGATIVE,
                    "The difficulty level for each application of the fields below." ) );
            armorChance = SPEC.define( new DoubleField( "armor_chance", 0.05, DoubleField.Range.PERCENT,
                    "The chance that a mob will be given armor when it spawns. Default is 0.05 (5% chance).",
                    "This value increases in accordance to 'armor_difficulty_span'." ) );
            armorLunarChance = SPEC.define( new DoubleField( "armor_lunar_chance", 0.2, DoubleField.Range.PERCENT,
                    "The additional chance gained from a full moon. Default is 0.2 (+20% chance on full moon)." ) );
            armorMaxChance = SPEC.define( new DoubleField( "armor_max_chance", 0.95, DoubleField.Range.PERCENT,
                    "The maximum armor chance that can be given over time. Default is 0.95 (95% chance)." ) );
            currentArmorTierOnly = SPEC.define( new BooleanField( "current_armor_tier_only", false,
                    "If enabled, only armor from the most recently unlocked armor tier will be given to mobs.",
                    "When disabled, random armor pieces will be picked from all unlocked tiers." ) );
            
            SPEC.newLine();
            
            canGetEnchantments = SPEC.define( new RegistryEntryListField<>( "can_get_enchantments",
                    new RegistryEntryList<>( ForgeRegistries.ENTITY_TYPES,
                            List.of(), List.of(),
                            EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.DROWNED, EntityType.HUSK,
                            EntityType.WITHER_SKELETON, EntityType.SKELETON, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE
                    ),
                    "A list of entity types that can get their equipment enchanted when spawning." ) );
            
            SPEC.newLine();
            
            enchantDifficultySpan = SPEC.define( new DoubleField( "enchant_difficulty_span", 10.0, DoubleField.Range.NON_NEGATIVE,
                    "The difficulty level for each application of the fields below." ) );
            enchantChance = SPEC.define( new DoubleField( "enchant_chance", 0.05, DoubleField.Range.PERCENT,
                    "The chance that a mob will get their equipment enchanted. Default is 0.05 (5% chance).",
                    "This value increases in accordance to 'enchant_difficulty_span'.",
                    "Note that this chance is rolled one time for each piece of equipment; held weapon, helmet, leggings etc." ) );
            enchantLunarChance = SPEC.define( new DoubleField( "enchant_lunar_chance", 0.25, DoubleField.Range.PERCENT,
                    "The additional chance gained from a full moon. Default is 0.25 (+25% chance on full moon)." ) );
            enchantMaxChance = SPEC.define( new DoubleField( "enchant_max_chance", 0.90, DoubleField.Range.PERCENT,
                    "The maximum enchant chance that can be given over time. Default is 0.90 (90% chance)." ) );
            enchantLevelRange = new IntField.RandomRange( SPEC, "enchant_level_range", 4, 27, 0, 30,
                    "The lowest and highest value possible when picking the level when enchanting a piece of equipment." );
        }
    }
    
    //
    //   POTION EFFECTS
    //
    public static class PotionEffects extends AbstractConfigCategory<MobBuffingConfig> {
        
        public final DifficultyRegistryEntryListField<MobEffect> potionEffectList;
        public final EntityListField entityBlacklist;
        
        public final DoubleField potionEffectDifficultySpan;
        public final DoubleField potionEffectChance;
        public final DoubleField potionEffectLunarChance;
        public final DoubleField potionEffectMaxChance;
        
        
        PotionEffects( MobBuffingConfig parent ) {
            super( parent, "potion_effects",
                    "Settings related to potion effects applied to mobs." );
            
            potionEffectList = SPEC.define( new DifficultyRegistryEntryListField<>( "potion_effect_list", new DifficultyRegistryEntryList<>( ForgeRegistries.MOB_EFFECTS,
                    new DifficultyRegListEntry<>( PotionEffects.this.potionEffectList, 15,
                            ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.FIRE_RESISTANCE )
                    ),
                    new DifficultyRegListEntry<>( PotionEffects.this.potionEffectList, 30,
                            ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.REGENERATION )
                    ),
                    new DifficultyRegListEntry<>( PotionEffects.this.potionEffectList, 45,
                            ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.WATER_BREATHING ),
                            ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.JUMP )
                    ),
                    new DifficultyRegListEntry<>( PotionEffects.this.potionEffectList, 60,
                            ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.DAMAGE_BOOST )
                    ),
                    new DifficultyRegListEntry<>( PotionEffects.this.potionEffectList, 100,
                            ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.DAMAGE_RESISTANCE )
                    )
            ), "A list of potion effects that can be given to mobs when they spawn, divided into tiers by difficulty level.",
                    "When Apocalypse tries to apply a potion effect to a mob, it picks a random one from a random tier in this list, " +
                            "as long as said tier is unlocked." ) );
            
            SPEC.newLine();
            
            entityBlacklist = SPEC.define( new EntityListField( "entity_blacklist", new EntityList(
                    null, new EntityEntry( EntityType.WARDEN )
            ).addTagEntries( List.of( new EntityTagEntry( Tags.EntityTypes.BOSSES ) ) )
                    .setNoValues(),
                    "A list of entities that should not be given potion effects upon spawning." ) );
            
            SPEC.newLine();
            
            potionEffectDifficultySpan = SPEC.define( new DoubleField( "potion_effect_difficulty_span", 5.0, DoubleField.Range.NON_NEGATIVE,
                    "The difficulty value for each application of the fields below." ) );
            potionEffectChance = SPEC.define( new DoubleField( "potion_effect_chance", 0.015, DoubleField.Range.PERCENT,
                    "The chance that a mob will be given a potion effect when it spawns. Default is 0.015 (1.5% chance).",
                    "This value increases in accordance with 'potion_effect_difficulty_span'." ) );
            potionEffectLunarChance = SPEC.define( new DoubleField( "potion_effect_lunar_chance", 0.2, DoubleField.Range.PERCENT,
                    "The additional chance gained from a full moon. Default is 0.2 (+20% chance on full moon)." ) );
            potionEffectMaxChance = SPEC.define( new DoubleField( "potion_effect_max_chance", 0.95, DoubleField.Range.PERCENT,
                    "The maximum potion effect chance that can be given over time. Default is 0.95 (95% chance)." ) );
        }
    }
}
