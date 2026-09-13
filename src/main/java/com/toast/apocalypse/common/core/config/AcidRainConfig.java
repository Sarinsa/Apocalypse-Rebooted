package com.toast.apocalypse.common.core.config;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.config.value.UsageRestrictedKeyParser;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.field.RestartNote;
import fathertoast.crust.api.config.common.field.collection.BlockStateMapField;
import fathertoast.crust.api.config.common.field.collection.EntitySetField;
import fathertoast.crust.api.config.common.field.collection.ItemStackSetField;
import fathertoast.crust.api.config.common.value.collection.BlockStateMap;
import fathertoast.crust.api.config.common.value.collection.EntitySet;
import fathertoast.crust.api.config.common.value.collection.ItemStackSet;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import fathertoast.crust.api.config.common.value.collection.key.BlockStateKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.util.BlockStatePropertyMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public class AcidRainConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    public final WorldDegradation WORLD_DEGRADATION;
    
    /** Builds the config spec that should be used for this config. */
    public AcidRainConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName, false,
                "This config contains settings related to Apocalypse's acid rain event."
        );
        EntitySetField.describe( SPEC );
        BlockStateMapField.describe( SPEC );
        
        GENERAL = new General( this );
        WORLD_DEGRADATION = new WorldDegradation( this );
    }
    
    
    public static class General extends AbstractConfigCategory<AcidRainConfig> {
        
        public final DoubleField acidRainChance;
        public final DoubleField acidRainDurationMulti;
        
        public final IntField damageTicks;
        public final DoubleField healthDamage;
        public final ItemStackSetField nonProtectingItems;
        public final IntField durabilityDamage;
        public final BooleanField damageAllEquipment;
        public final ItemStackSetField rainImmuneItems;
        
        public final BooleanField damageMobs;
        public final EntitySetField mobBlacklist;
        
        public final BooleanField acidSnow;
        public final BooleanField acidSnowAccumulates;
        
        General( AcidRainConfig parent ) {
            super( parent, "general",
                    "General event settings." );
            
            acidRainChance = SPEC.define( new DoubleField( "acid_rain_chance", 0.15, DoubleField.Range.PERCENT,
                    "The chance of triggering an Acid Rain event when it starts raining. 1.0 = 100% chance, 0.5 = 50% etc.",
                    "Setting this to 0.0 effectively disables acid rain." ) );
            acidRainDurationMulti = SPEC.define( new DoubleField( "acid_rain_duration_multiplier", 0.3, DoubleField.Range.NON_NEGATIVE,
                    "When it starts raining acid, the rain's duration is multiplied by this value.",
                    "For reference, vanilla rain is 12000 to 24000 ticks (10 to 20 minutes) long." ) );
            
            SPEC.newLine();
            
            damageTicks = SPEC.define( new IntField( "damage_interval", 60, IntField.Range.POSITIVE,
                    "Determines the interval in which acid rain damage should be dealt, in ticks (20 ticks = 1 second).",
                    "For example, a value of 60 will inflict acid rain damage every 3 seconds." ) );
            healthDamage = SPEC.define( new DoubleField( "rain_health_damage", 1.0, DoubleField.Range.NON_NEGATIVE,
                    "The amount of health damage that should be inflicted by acid rain, in half-hearts.",
                    "Wearing a helmet or having the Aqua Affinity enchantment protects against this damage.",
                    "Setting this to 0.0 disables acid rain health damage." ) );
            nonProtectingItems = SPEC.define( new ItemStackSetField( "non_protecting_items", new ItemStackSet(),
                    "All items NOT in this set prevent all health damage from acid rain when worn on an " +
                            "entity's head." ), true );
            durabilityDamage = SPEC.define( new IntField( "rain_durability_damage", 1, IntField.Range.NON_NEGATIVE,
                    "The amount of durability damage that should be inflicted by acid rain.",
                    "Acid rain does not affect entities with the Aqua Affinity enchantment.",
                    "Setting this to 0 disables acid rain durability damage." ) );
            damageAllEquipment = SPEC.define( new BooleanField( "damage_all_equipment", false,
                    "If true, acid rain will damage chestplates, leggings, and boots instead of only helmets.",
                    "The helmet slot is still the only one that can prevent health damage." ) );
            rainImmuneItems = SPEC.define( new ItemStackSetField( "rain_immune_items", new ItemStackSet.Builder<>()
                    .add( ApocalypseObjects.Items.BUCKET_HELM ).add( Items.TURTLE_HELMET ).add( Items.ELYTRA )
                    .addWildcard( ResourceLocation.withDefaultNamespace( "golden_" ) )
                    .addWildcard( ResourceLocation.withDefaultNamespace( "diamond_" ) )
                    .build(),
                    "Set of items that are immune to durability damage from acid rain." ) );
            
            SPEC.newLine();
            
            damageMobs = SPEC.define( new BooleanField( "damage_mobs", false,
                    "If true, acid rain will damage all living things and not just players." ) );
            mobBlacklist = SPEC.define( new EntitySetField( "mob_blacklist",
                    new EntitySet.Builder<>()
                            .add( EntityType.VEX ).addExtends( EntityType.SLIME )
                            .add( EntityType.WARDEN ).addTag( Tags.EntityTypes.BOSSES )
                            .add( ApocalypseEntities.GHOST )
                            .build(),
                    "If 'damage_mobs' is true, this field acts as a blacklist for mobs that should be an exception and NOT take damage from acid rain." ) );
            
            SPEC.newLine();
            
            acidSnow = SPEC.define( new BooleanField( "acid_snow", false,
                            "If true, colder biomes/areas where it snows will have acid snow when the acid rain event triggers." ),
                    RestartNote.WORLD );
            acidSnowAccumulates = SPEC.define( new BooleanField( "acid_snow_accumulates", false,
                    "If acid snow is enabled, setting this to false will stop snow layers from being placed on the ground when it is " +
                            "snowing acid." ) );
        }
    }
    
    public static class WorldDegradation extends AbstractConfigCategory<AcidRainConfig> {
        
        public final BooleanField enableBlockDegradation;
        public final DoubleField degradationChance;
        public final BlockStateMapField<FuzzyKey<BlockState>> blockTransformations;
        
        
        WorldDegradation( AcidRainConfig parent ) {
            super( parent, "world_degradation",
                    "Settings related to block degradation in the world when it rains acid." );
            
            enableBlockDegradation = SPEC.define( new BooleanField( "enable_block_degradation", false,
                    "If enabled, acid rain will start \"corroding\" blocks it comes in contact with.",
                    "What blocks are affected and what they turn into can be configured in the below transformation list.",
                    "Note that block degradation by acid rain will not happen in snowy areas unless \"acidSnow\" is enabled in the general category." ) );
            degradationChance = SPEC.define( new DoubleField( "degradation_chance", 0.05, DoubleField.Range.PERCENT,
                    "The chance when ticking a chunk to corrode a random exposed block." ) );
            blockTransformations = SPEC.define( new BlockStateMapField<>( "block_transformations", defaultTransformList(),
                    "A list of input blocks states and what block states they turn into when exposed to acid rain.",
                    "Both the input and output block states can have state properties specified; input blocks will only " +
                            "be transformed when matching the state properties specified, while output blocks will copy the " +
                            "transforming block's state properties and overwrite any state properties specifically defined.",
                    "Consider this example entry:",
                    "\"minecraft:mossy_cobblestone_stairs minecraft:cobblestone_stairs\"",
                    "This will result in mossy cobble stairs turning into normal cobblestone stairs. The block state " +
                            "property values of the old stairs will be copied over to the new ones, so we retain the " +
                            "rotation of the stairs." ) );
        }
        
        private static BlockStateMap<FuzzyKey<BlockState>> defaultTransformList() {
            var builder = new BlockStateMap.Builder<>( UsageRestrictedKeyParser.of( BlockStateKey.PARSER, KeyUsage.POLL ) )
                    // Grass, plants, crops, and small flowers
                    .put( Blocks.GRASS_BLOCK, BlockStateKey.of( Blocks.DIRT,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.GRASS, BlockStateKey.of( ApocalypseObjects.Blocks.DEAD_GRASS,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.TALL_GRASS, BlockStateKey.of( Blocks.GRASS,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .putTag( BlockTags.SMALL_FLOWERS, BlockStateKey.of( ApocalypseObjects.Blocks.DEAD_PLANT,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .putTag( BlockTags.TALL_FLOWERS, BlockStateKey.of( Blocks.AIR,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.FERN, BlockStateKey.of( ApocalypseObjects.Blocks.DEAD_PLANT,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.LARGE_FERN, BlockStateKey.of( Blocks.FERN,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.MYCELIUM, BlockStateKey.of( Blocks.DIRT,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.VINE, BlockStateKey.of( Blocks.AIR,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.BROWN_MUSHROOM, BlockStateKey.of( Blocks.AIR,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.RED_MUSHROOM, BlockStateKey.of( Blocks.AIR,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .putTag( BlockTags.LEAVES, new BlockStatePropertyMap.Builder()
                                    .put( BlockStateProperties.WATERLOGGED, false ).build(),
                            BlockStateKey.of( Blocks.AIR,
                                    BlockStatePropertyMap.EMPTY, false ) )
                    .putTag( BlockTags.SAPLINGS, BlockStateKey.of( Blocks.DEAD_BUSH,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .putTag( BlockTags.CROPS, BlockStateKey.of( ApocalypseObjects.Blocks.DEAD_PLANT,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.SWEET_BERRY_BUSH, BlockStateKey.of( Blocks.DEAD_BUSH,
                            BlockStatePropertyMap.EMPTY, false ) )
                    
                    // Blocks with mossy qualities
                    .put( Blocks.MOSSY_COBBLESTONE, BlockStateKey.of( Blocks.COBBLESTONE,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.MOSSY_COBBLESTONE_SLAB, BlockStateKey.of( Blocks.COBBLESTONE_SLAB,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.MOSSY_COBBLESTONE_WALL, BlockStateKey.of( Blocks.COBBLESTONE_WALL,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.MOSSY_COBBLESTONE_STAIRS, BlockStateKey.of( Blocks.COBBLESTONE_STAIRS,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.MOSSY_STONE_BRICKS, BlockStateKey.of( Blocks.STONE_BRICKS,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.MOSSY_STONE_BRICK_SLAB, BlockStateKey.of( Blocks.STONE_BRICK_SLAB,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.MOSSY_STONE_BRICK_WALL, BlockStateKey.of( Blocks.STONE_BRICK_WALL,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.MOSSY_STONE_BRICK_STAIRS, BlockStateKey.of( Blocks.STONE_BRICK_STAIRS,
                            BlockStatePropertyMap.EMPTY, false ) )
                    .put( Blocks.INFESTED_MOSSY_STONE_BRICKS, BlockStateKey.of( Blocks.INFESTED_STONE_BRICKS,
                            BlockStatePropertyMap.EMPTY, false ) );
            
            for( Block block : ForgeRegistries.BLOCKS ) {
                // Weathering copper blocks
                if( block instanceof WeatheringCopper ) {
                    WeatheringCopper.getNext( block ).ifPresent( next -> builder
                            .put( block, BlockStateKey.of( next,
                                    BlockStatePropertyMap.EMPTY, false ) ) );
                }
            }
            return builder.build();
        }
    }
}