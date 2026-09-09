package com.toast.apocalypse.common.core.config;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.config.field.ItemsByDifficultyMapField;
import com.toast.apocalypse.common.core.config.field.MobEffectsByDifficultyMapField;
import com.toast.apocalypse.common.core.config.value.ItemsByDifficultyMap;
import com.toast.apocalypse.common.core.config.value.MobEffectsByDifficultyMap;
import com.toast.apocalypse.common.core.difficulty.MobEquipmentHandler;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.util.References;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.InjectionWrapperField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.field.collection.EntitySetField;
import fathertoast.crust.api.config.common.value.collection.EntitySet;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

public class MobBuffingConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    public final Attributes ATTRIBUTES;
    public final Equipment EQUIPMENT;
    public final PotionEffects POTION_EFFECTS;
    
    /** Builds the config spec that should be used for this config. */
    public MobBuffingConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName, false,
                "This config contains options related to difficulty-based mob buffs, including equipment, attribute boosts, " +
                        "potion effects and more."
        );
        EntitySetField.describe( SPEC );
        //ItemsByDifficultyMapField.describe( SPEC ); // TODO maybe we should make these
        //MobEffectsByDifficultyMapField.describe( SPEC );
        
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
        public final EntitySetField buffBlacklist;
        
        General( MobBuffingConfig parent ) {
            super( parent, "general",
                    "General settings related to mob buffs." );
            
            enemiesOnly = SPEC.define( new BooleanField( "enemies_only", true,
                    "If enabled, only mobs that are considered hostile will receive potion buffs, " +
                            "attribute boosts, and added equipment." ) );
            buffBlacklist = SPEC.define( new EntitySetField( "buff_blacklist",
                    new EntitySet.Builder<>().add( EntityType.ENDER_DRAGON ).build(),
                    "A set of entities that should not receive ANY buffs from this config." ) );
        }
    }
    
    //
    //   ATTRIBUTE BOOSTS
    //
    public static class Attributes extends AbstractConfigCategory<MobBuffingConfig> {
        
        public final EntitySetField healthBlacklist;
        public final DoubleField healthLunarFlatBonus;
        public final DoubleField healthLunarMultBonus;
        public final DoubleField healthDifficultySpan;
        public final DoubleField healthFlatBonus;
        public final DoubleField healthFlatBonusMax;
        public final DoubleField healthMultBonus;
        public final DoubleField healthMultBonusMax;
        
        public final EntitySetField damageBlacklist;
        public final DoubleField damageLunarFlatBonus;
        public final DoubleField damageLunarMultBonus;
        public final DoubleField damageDifficultySpan;
        public final DoubleField damageFlatBonus;
        public final DoubleField damageFlatBonusMax;
        public final DoubleField damageMultBonus;
        public final DoubleField damageMultBonusMax;
        
        public final EntitySetField speedBlacklist;
        public final DoubleField speedLunarMultBonus;
        public final DoubleField speedDifficultySpan;
        public final DoubleField speedMultBonus;
        public final DoubleField speedMultBonusMax;
        
        public final EntitySetField knockbackResBlacklist;
        public final DoubleField knockbackResLunarFlatBonus;
        public final DoubleField knockbackResDifficultySpan;
        public final DoubleField knockbackResFlatBonus;
        public final DoubleField knockbackResFlatBonusMax;
        
        Attributes( MobBuffingConfig parent ) {
            super( parent, "attribute_boost",
                    "Settings related to attribute boosts." );
            
            SPEC.titledComment( "Max Health", "Settings for maximum health boosts." );
            SPEC.increaseIndent();
            healthBlacklist = SPEC.define( new EntitySetField( "health.entity_blacklist", new EntitySet(),
                    "A list of entities that should not receive any health boosts." ) );
            healthLunarFlatBonus = SPEC.define( new DoubleField( "health.lunar_flat_bonus", 10.0, DoubleField.Range.NON_NEGATIVE,
                    "The flat health bonus (in half-hearts) gained during a full moon. Default is 10.0 (+5 hearts during full moons)." ) );
            healthLunarMultBonus = SPEC.define( new DoubleField( "health.lunar_mult_bonus", 0.0, DoubleField.Range.NON_NEGATIVE,
                    "The health multiplier bonus gained during a full moon. Default is none." ) );
            healthDifficultySpan = SPEC.define( new DoubleField( "health.difficulty_span",
                    References.toDays( 1 ), 1.0, Double.POSITIVE_INFINITY,
                    "The difficulty per application of the below values. Default is 8.0 (8 days of difficulty per application)." ) );
            healthFlatBonus = SPEC.define( new DoubleField( "health.flat_bonus", 0.0, DoubleField.Range.NON_NEGATIVE,
                    "Flat health bonus (in half-hearts) per difficulty span. Default is none." ) );
            healthFlatBonusMax = SPEC.define( new DoubleField( "health.max_flat_bonus", -1.0, DoubleField.Range.ANY,
                    "The maximum flat health bonus from difficulty. Negative value means no limit." ) );
            healthMultBonus = SPEC.define( new DoubleField( "health.mult_bonus", 0.05, DoubleField.Range.NON_NEGATIVE,
                    "Health multiplier bonus given per difficulty span. Default is 0.05 (+5% health per X difficulty)." ) );
            healthMultBonusMax = SPEC.define( new DoubleField( "health.max_mult_bonus", -1.0, DoubleField.Range.ANY,
                    "The maximum health multiplier bonus from difficulty. Negative value means no limit." ) );
            SPEC.decreaseIndent();
            
            SPEC.newLine();
            
            SPEC.titledComment( "Attack Damage", "Settings for attack damage boosts." );
            SPEC.increaseIndent();
            damageBlacklist = SPEC.define( new EntitySetField( "damage.entity_blacklist", new EntitySet(),
                    "A list of entities that should not receive any damage boosts." ) );
            damageLunarFlatBonus = SPEC.define( new DoubleField( "damage.lunar_flat_bonus", 2.0, DoubleField.Range.NON_NEGATIVE,
                    "The flat damage bonus (in half-hearts) gained during a full moon. Default is 2.0 (+1 heart of damage during full moons)." ) );
            damageLunarMultBonus = SPEC.define( new DoubleField( "damage.lunar_mult_bonus", 0.0, DoubleField.Range.NON_NEGATIVE,
                    "The damage multiplier bonus gained during a full moon. Default is none." ) );
            damageDifficultySpan = SPEC.define( new DoubleField( "damage.difficulty_span",
                    References.toDays( 1 ), 1.0, Double.POSITIVE_INFINITY,
                    "The difficulty per application of the below values. Default is 8.0 (8 days of difficulty per application)." ) );
            damageFlatBonus = SPEC.define( new DoubleField( "damage.flat_bonus", 0.0, DoubleField.Range.NON_NEGATIVE,
                    "Flat damage bonus (in half-hearts) per difficulty span. Default is none." ) );
            damageFlatBonusMax = SPEC.define( new DoubleField( "damage.max_flat_bonus", -1.0, DoubleField.Range.ANY,
                    "The maximum flat damage bonus from difficulty. Negative value means no limit." ) );
            damageMultBonus = SPEC.define( new DoubleField( "damage.mult_bonus", 0.025, DoubleField.Range.NON_NEGATIVE,
                    "Damage multiplier bonus given per difficulty span. Default is 0.025 (+2.5% damage per X difficulty)." ) );
            damageMultBonusMax = SPEC.define( new DoubleField( "damage.max_mult_bonus", 0.5, DoubleField.Range.ANY,
                    "The maximum damage multiplier bonus from difficulty. Default is 0.5 (+50% damage). Negative value means no limit." ) );
            SPEC.decreaseIndent();
            
            SPEC.newLine();
            
            SPEC.titledComment( "Movement Speed", "Settings for movement speed boosts.",
                    "WARNING: Raising movement speed too high breaks entity pathfinding in a way that causes " +
                            "mobs to look like they are ice skating!" );
            SPEC.increaseIndent();
            speedBlacklist = SPEC.define( new EntitySetField( "movement_speed.entity_blacklist", new EntitySet.Builder<>()
                    .add( ApocalypseEntities.GHOST ).addExtends( EntityType.GHAST, 1 ).build(),
                    "A list of entities that should not receive any movement speed boosts." ) );
            speedLunarMultBonus = SPEC.define( new DoubleField( "movement_speed.lunar_mult_bonus", 0.25, DoubleField.Range.NON_NEGATIVE,
                    "The movement speed multiplier bonus gained during a full moon. Default is 0.25 (+25% speed during full moons)." ) );
            speedDifficultySpan = SPEC.define( new DoubleField( "movement_speed.difficulty_span",
                    References.toDays( 1 ), 1.0, Double.POSITIVE_INFINITY,
                    "The difficulty per application of the below values. Default is 8.0 (8 days of difficulty per application)." ) );
            speedMultBonus = SPEC.define( new DoubleField( "movement_speed.mult_bonus", 0.0, DoubleField.Range.NON_NEGATIVE,
                    "Movement speed multiplier bonus given per difficulty span. Default is none." ) );
            speedMultBonusMax = SPEC.define( new DoubleField( "movement_speed.max_mult_bonus", 0.5, DoubleField.Range.ANY,
                    "The maximum movement speed multiplier bonus from difficulty. Negative value means no limit." ) );
            SPEC.decreaseIndent();
            
            SPEC.newLine();
            
            SPEC.titledComment( "Knockback Resistance", "Settings for knockback resistance boosts." );
            SPEC.increaseIndent();
            knockbackResBlacklist = SPEC.define( new EntitySetField( "knockback_resistance.entity_blacklist", new EntitySet(),
                    "A list of entities that should not receive any knockback resistance boosts." ) );
            knockbackResLunarFlatBonus = SPEC.define( new DoubleField( "knockback_resistance.lunar_flat_bonus", 0.2, DoubleField.Range.NON_NEGATIVE,
                    "The flat knockback resistance bonus gained during a full moon. Default is 0.2 (+20% chance to ignore knockback during full moons)." ) );
            knockbackResDifficultySpan = SPEC.define( new DoubleField( "knockback_resistance.difficulty_span",
                    References.toDays( 1 ), 1.0, Double.POSITIVE_INFINITY,
                    "The difficulty per application of the below values. Default is 8.0 (8 days of difficulty per application)." ) );
            knockbackResFlatBonus = SPEC.define( new DoubleField( "knockback_resistance.flat_bonus", 0.0, DoubleField.Range.NON_NEGATIVE,
                    "Flat knockback resistance bonus per difficulty span. Default is 0.01 (+1% chance to ignore knockback per X difficulty)" ) );
            knockbackResFlatBonusMax = SPEC.define( new DoubleField( "knockback_resistance.max_flat_bonus", 0.2, 0.0, 1.0,
                    "The maximum flat knockback resistance bonus from difficulty. Default is 0.2 (+20% chance to ignore knockback)" ) );
            SPEC.decreaseIndent();
        }
    }
    
    //
    //   EQUIPMENT
    //
    public static class Equipment extends AbstractConfigCategory<MobBuffingConfig> {
        
        public final ItemsByDifficultyMapField weaponTierList;
        public final EntitySetField weaponWhitelist;
        public final DoubleField weaponsLunarChance;
        public final DoubleField weaponsDifficultySpan;
        public final DoubleField weaponsChance;
        public final DoubleField weaponsMaxChance;
        
        //public final ItemsByDifficultyMapField armorTierList;
        public final EntitySetField armorWhitelist;
        public final DoubleField armorLunarChance;
        public final DoubleField armorDifficultySpan;
        public final DoubleField armorChance;
        public final DoubleField armorMaxChance;
        
        public final EntitySetField enchantWhitelist;
        public final DoubleField enchantLunarChance;
        public final DoubleField enchantDifficultySpan;
        public final DoubleField enchantChance;
        public final DoubleField enchantMaxChance;
        public final IntField.RandomRange enchantLevelRange;
        
        Equipment( MobBuffingConfig parent ) {
            super( parent, "equipment",
                    "Settings related to equipment given to mobs." );
            
            SPEC.titledComment( "Weapons", "Settings for equipping weapons." );
            SPEC.increaseIndent();
            weaponTierList = SPEC.define( new ItemsByDifficultyMapField( "weapons.tier_list", makeDefaultWeaponTiers(),
                    "A list of weapon items that mobs can spawn with, divided into tiers by difficulty level.",
                    "When a mob spawns, Apocalypse will try to give it a weapon, picking a random item from all active tiers.",
                    "Only mobs listed in 'weapons.entity_whitelist' can be given a weapon." ) );
            weaponWhitelist = SPEC.define( new EntitySetField( "weapons.entity_whitelist", new EntitySet.Builder<>()
                    .addExtends( EntityType.ZOMBIE )
                    .addExtends( EntityType.SKELETON, 1 )
                    .addExtends( EntityType.PIGLIN, 1 )
                    .add( EntityType.VINDICATOR )
                    .build(),
                    "A list of entity types that can be given weapons from the above weapon tier list." ) );
            weaponsLunarChance = SPEC.define( new DoubleField( "weapons.lunar_chance", 0.2, DoubleField.Range.PERCENT,
                    "The weapon chance bonus gained during a full moon. Default is 0.2 (+20% chance during full moons)." ) );
            weaponsDifficultySpan = SPEC.define( new DoubleField( "weapons.difficulty_span",
                    References.toDays( 1 ), 1.0, Double.POSITIVE_INFINITY,
                    "The difficulty per application of the below values. Default is 8.0 (8 days of difficulty per application)." ) );
            weaponsChance = SPEC.define( new DoubleField( "weapons.chance", 0.05, DoubleField.Range.PERCENT,
                    "Chance per difficulty span that a mob will be given a weapon when it spawns. Default is 0.05 (5% chance per X difficulty)." ) );
            weaponsMaxChance = SPEC.define( new DoubleField( "weapons.max_chance", 0.8, DoubleField.Range.PERCENT,
                    "The maximum weapon chance from difficulty. Default is 0.8 (80% chance)." ) );
            SPEC.decreaseIndent();
            
            SPEC.newLine();
            
            SPEC.titledComment( "Armor", "Settings for equipping armor." );
            SPEC.increaseIndent();
            SPEC.define( new InjectionWrapperField<>(
                    new ItemsByDifficultyMapField( "armor.tier_list", makeDefaultArmorTiers(),
                            "A list of armor/equippable items that mobs can spawn with, divided into tiers by difficulty level.",
                            "When a mob spawns, Apocalypse will try to give it an armor piece for each equipment slot, " +
                                    "picking items from a single randomly selected active tier.",
                            "Each tier can contain multiple armor pieces per equipment slot; if so, a random one will be " +
                                    "selected when equipping that armor tier. Non-equippable items CAN be used here and " +
                                    "will be equipped on helmet/head slot if selected (funnie for block heads).",
                            "Only mobs listed in 'armor.entity_whitelist' can be given armor."
                    ), MobEquipmentHandler::refreshArmorMaps ) );
            armorWhitelist = SPEC.define( new EntitySetField( "armor.entity_whitelist", new EntitySet.Builder<>()
                    .addExtends( EntityType.ZOMBIE )
                    .addExtends( EntityType.SKELETON, 1 )
                    .addExtends( EntityType.PIGLIN, 1 )
                    .build(),
                    "A list of entity types that can be given armor from the above armor tier list." ) );
            armorLunarChance = SPEC.define( new DoubleField( "armor.lunar_chance", 0.2, DoubleField.Range.PERCENT,
                    "The armor chance bonus gained during a full moon. Default is 0.2 (+20% chance during full moons)." ) );
            armorDifficultySpan = SPEC.define( new DoubleField( "armor.difficulty_span",
                    References.toDays( 1 ), 1.0, Double.POSITIVE_INFINITY,
                    "The difficulty per application of the below values. Default is 8.0 (8 days of difficulty per application)." ) );
            armorChance = SPEC.define( new DoubleField( "armor.chance", 0.05, DoubleField.Range.PERCENT,
                    "Chance per difficulty span that a mob will be given armor when it spawns. " +
                            "Default is 0.05 (5% chance per X difficulty)." ) );
            armorMaxChance = SPEC.define( new DoubleField( "armor.max_chance", 0.8, DoubleField.Range.PERCENT,
                    "The maximum armor chance from difficulty. Default is 0.8 (80% chance)." ) );
            SPEC.decreaseIndent();
            
            SPEC.newLine();
            
            SPEC.titledComment( "Enchantments", "Settings for equipment enchantment.",
                    "Enchantment chance is rolled individually for each piece of equipment." );
            SPEC.increaseIndent();
            enchantWhitelist = SPEC.define( new EntitySetField( "enchant.entity_whitelist", new EntitySet.Builder<>()
                    .addExtends( EntityType.ZOMBIE )
                    .addExtends( EntityType.SKELETON, 1 )
                    .addExtends( EntityType.PIGLIN, 1 )
                    .build(),
                    "A list of entity types that can get their equipment enchanted when spawning." ) );
            enchantLunarChance = SPEC.define( new DoubleField( "enchant.lunar_chance", 0.2, DoubleField.Range.PERCENT,
                    "The enchant chance bonus gained during a full moon. Default is 0.2 (+20% chance during full moons)." ) );
            enchantDifficultySpan = SPEC.define( new DoubleField( "enchant.difficulty_span",
                    References.toDays( 1 ), 1.0, Double.POSITIVE_INFINITY,
                    "The difficulty per application of the below values. Default is 8.0 (8 days of difficulty per application)." ) );
            enchantChance = SPEC.define( new DoubleField( "enchant.chance", 0.01, DoubleField.Range.PERCENT,
                    "Chance per difficulty span that a mob will have its equipment enchanted when it spawns. " +
                            "Default is 0.01 (1% chance per X difficulty).",
                    "Note this chance is rolled once for each piece of equipment; weapon, helmet, leggings etc." ) );
            enchantMaxChance = SPEC.define( new DoubleField( "enchant.max_chance", 0.8, DoubleField.Range.PERCENT,
                    "The maximum enchant chance from difficulty. Default is 0.8 (80% chance)." ) );
            enchantLevelRange = new IntField.RandomRange( SPEC, "enchant.level_range", 5, 30, 0, 30,
                    "The lowest and highest value possible when picking the level when enchanting a piece of equipment." );
            //TODO make enchant level scale with diff
            SPEC.decreaseIndent();
        }
        
        private ItemsByDifficultyMap makeDefaultWeaponTiers() {
            return new ItemsByDifficultyMap.Builder<>()
                    .greaterThan( References.toDays( 1 ) )
                    .add( Items.WOODEN_SWORD ).add( Items.WOODEN_AXE )
                    .add( Items.WOODEN_SHOVEL ).add( Items.WOODEN_PICKAXE )
                    .buildSub()
                    .greaterThan( References.toDays( 5 ) )
                    .add( Items.STONE_SWORD ).add( Items.STONE_AXE )
                    .add( Items.STONE_SHOVEL ).add( Items.STONE_PICKAXE )
                    .buildSub()
                    .greaterThan( References.toDays( 7 ) )
                    .add( Items.GOLDEN_SWORD ).add( Items.GOLDEN_AXE )
                    .add( Items.GOLDEN_SHOVEL ).add( Items.GOLDEN_PICKAXE )
                    .buildSub()
                    .greaterThan( References.toDays( 9 ) )
                    .add( Items.IRON_SWORD ).add( Items.IRON_AXE )
                    .add( Items.IRON_SHOVEL ).add( Items.IRON_PICKAXE )
                    .buildSub()
                    .greaterThan( References.toDays( 13 ) )
                    .add( Items.DIAMOND_SWORD ).add( Items.DIAMOND_AXE )
                    .add( Items.DIAMOND_SHOVEL ).add( Items.DIAMOND_PICKAXE )
                    .buildSub()
                    .greaterThan( References.toDays( 18 ) )
                    .add( Items.NETHERITE_SWORD ).add( Items.NETHERITE_AXE )
                    .add( Items.NETHERITE_SHOVEL ).add( Items.NETHERITE_PICKAXE )
                    .buildSub()
                    .build();
        }
        
        private ItemsByDifficultyMap makeDefaultArmorTiers() {
            return new ItemsByDifficultyMap.Builder<>()
                    .greaterThan( References.toDays( 0 ) )
                    .add( ApocalypseObjects.Items.BUCKET_HELM )
                    .buildSub()
                    .greaterThan( References.toDays( 2 ) )
                    .add( Items.CARVED_PUMPKIN )
                    .add( Items.LEATHER_HELMET ).add( Items.LEATHER_CHESTPLATE )
                    .add( Items.LEATHER_LEGGINGS ).add( Items.LEATHER_BOOTS )
                    .buildSub()
                    .greaterThan( References.toDays( 4 ) )
                    .add( ApocalypseObjects.Items.BUCKET_HELM )
                    .add( Items.CHAINMAIL_HELMET ).add( Items.CHAINMAIL_CHESTPLATE )
                    .add( Items.CHAINMAIL_LEGGINGS ).add( Items.CHAINMAIL_BOOTS )
                    .buildSub()
                    .greaterThan( References.toDays( 6 ) )
                    .add( Items.TURTLE_HELMET )
                    .add( Items.GOLDEN_HELMET ).add( Items.GOLDEN_CHESTPLATE )
                    .add( Items.GOLDEN_LEGGINGS ).add( Items.GOLDEN_BOOTS )
                    .buildSub()
                    .greaterThan( References.toDays( 8 ) )
                    .add( Items.IRON_HELMET ).add( Items.IRON_CHESTPLATE )
                    .add( Items.IRON_LEGGINGS ).add( Items.IRON_BOOTS )
                    .buildSub()
                    .greaterThan( References.toDays( 11 ) )
                    .add( Items.DIAMOND_HELMET ).add( Items.DIAMOND_CHESTPLATE )
                    .add( Items.DIAMOND_LEGGINGS ).add( Items.DIAMOND_BOOTS )
                    .buildSub()
                    .greaterThan( References.toDays( 15 ) )
                    .add( Items.NETHERITE_HELMET ).add( Items.NETHERITE_CHESTPLATE )
                    .add( Items.NETHERITE_LEGGINGS ).add( Items.NETHERITE_BOOTS )
                    .buildSub()
                    .build();
        }
    }
    
    //
    //   POTION EFFECTS
    //
    public static class PotionEffects extends AbstractConfigCategory<MobBuffingConfig> {
        
        public final MobEffectsByDifficultyMapField mobEffectTierList;
        public final EntitySetField mobEffectBlacklist;
        public final DoubleField mobEffectLunarChance;
        public final DoubleField mobEffectDifficultySpan;
        public final DoubleField mobEffectChance;
        public final DoubleField mobEffectMaxChance;
        
        PotionEffects( MobBuffingConfig parent ) {
            super( parent, "potion_effects",
                    "Settings related to permanent potion effects applied to mobs." );
            
            mobEffectTierList = SPEC.define( new MobEffectsByDifficultyMapField( "tier_list", makeDefaultMobEffectTiers(),
                    "A list of potion effect-amplifier pairs that mobs can spawn with, divided into tiers by difficulty level.",
                    "When a mob spawns, Apocalypse will try to give it a permanent potion effect, picking a random effect-amplifier pair from all active tiers." ) );
            mobEffectBlacklist = SPEC.define( new EntitySetField( "entity_blacklist", new EntitySet.Builder<>()
                    .add( EntityType.WARDEN ).addTag( Tags.EntityTypes.BOSSES )
                    .build(),
                    "A list of entities that should not receive any permanent potion effects." ) );
            mobEffectLunarChance = SPEC.define( new DoubleField( "lunar_chance", 0.2, DoubleField.Range.PERCENT,
                    "The potion effect chance bonus gained during a full moon. Default is 0.2 (+20% chance during full moons)." ) );
            mobEffectDifficultySpan = SPEC.define( new DoubleField( "difficulty_span",
                    References.toDays( 1 ), 1.0, Double.POSITIVE_INFINITY,
                    "The difficulty per application of the below values. Default is 8.0 (8 days of difficulty per application)." ) );
            mobEffectChance = SPEC.define( new DoubleField( "chance", 0.015, DoubleField.Range.PERCENT,
                    "Chance per difficulty span that a mob will spawn with a permanent potion effect. " +
                            "Default is 0.015 (1.5% chance per X difficulty)." ) );
            mobEffectMaxChance = SPEC.define( new DoubleField( "max_chance", 0.8, DoubleField.Range.PERCENT,
                    "The maximum potion effect chance from difficulty. Default is 0.8 (80% chance)." ) );
        }
        
        private MobEffectsByDifficultyMap makeDefaultMobEffectTiers() {
            return new MobEffectsByDifficultyMap.Builder<>()
                    .greaterThan( References.toDays( 1 ) )
                    .put( MobEffects.WATER_BREATHING, 0 )
                    .put( MobEffects.DAMAGE_RESISTANCE, 0 )
                    .put( MobEffects.JUMP, 1 )
                    .buildSub()
                    .greaterThan( References.toDays( 3 ) )
                    .put( MobEffects.SLOW_FALLING, 0 )
                    .put( MobEffects.ABSORPTION, 0 )
                    .buildSub()
                    .greaterThan( References.toDays( 5 ) )
                    .put( MobEffects.REGENERATION, 0 )
                    .put( MobEffects.DOLPHINS_GRACE, 0 )
                    .buildSub()
                    .greaterThan( References.toDays( 7 ) )
                    .put( MobEffects.FIRE_RESISTANCE, 0 )
                    .put( MobEffects.MOVEMENT_SPEED, 0 )
                    .buildSub()
                    .greaterThan( References.toDays( 9 ) )
                    .put( MobEffects.DAMAGE_RESISTANCE, 1 )
                    .put( MobEffects.ABSORPTION, 1 )
                    .buildSub()
                    .greaterThan( References.toDays( 11 ) )
                    .put( MobEffects.DAMAGE_BOOST, 0 )
                    .buildSub()
                    .greaterThan( References.toDays( 13 ) )
                    .put( MobEffects.REGENERATION, 1 )
                    .put( MobEffects.ABSORPTION, 2 )
                    .buildSub()
                    .build();
        }
    }
}