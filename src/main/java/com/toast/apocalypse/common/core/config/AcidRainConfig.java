package com.toast.apocalypse.common.core.config;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.config.field.BlockTransformListField;
import com.toast.apocalypse.common.core.config.value.BlockTransformList;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.field.RestartNote;
import fathertoast.crust.api.config.common.value.EntityEntry;
import fathertoast.crust.api.config.common.value.EntityList;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;

public class AcidRainConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    public final WorldDegradation WORLD_DEGRADATION;
    
    /** Builds the config spec that should be used for this config. */
    public AcidRainConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName, false,
                "This config contains settings related to Apocalypse's acid rain event."
        );
        SPEC.fileOnlyNewLine();
        SPEC.describeEntityList();
        SPEC.fileOnlyNewLine();
        SPEC.fileOnlyComment( BlockTransformListField.verboseDescription() );
        SPEC.fileOnlyNewLine();
        
        GENERAL = new General( this );
        WORLD_DEGRADATION = new WorldDegradation( this );
    }
    
    
    public static class General extends AbstractConfigCategory<AcidRainConfig> {
        
        public final DoubleField acidRainChance;
        
        public final IntField damageRate;
        public final IntField rainDamage;
        
        public final BooleanField damageMobs;
        public final EntityListField mobBlacklist;
        
        public final BooleanField acidSnow;
        public final BooleanField acidSnowAccumulates;
        
        
        General( AcidRainConfig parent ) {
            super( parent, "general",
                    "General event settings" );
            
            acidRainChance = SPEC.define( new DoubleField( "acid_rain_chance", 0.5, DoubleField.Range.PERCENT,
                    "The chance of triggering an Acid Rain event when it starts raining. 1.0 = 100% chance, 0.5 = 50% etc.",
                    "Setting this to 0.0 effectively disables acid rain." ) );
            
            SPEC.newLine();
            
            damageRate = SPEC.define( new IntField( "damage_rate", 3, 1, 1000,
                    "Determines the interval in which acid rain damage should be dealt in seconds.",
                    "A value of 2 will inflict acid rain damage on players every 2 seconds." ) );
            rainDamage = SPEC.define( new IntField( "rain_damage", 1, IntField.Range.NON_NEGATIVE,
                    "The amount of damage that should be dealt to players on acid rain tick.",
                    "Setting this to 0 disables acid rain damage." ) );
            
            SPEC.newLine();
            
            damageMobs = SPEC.define( new BooleanField( "damage_mobs", false,
                    "If true, acid rain will damage all living things and not just players." ) );
            mobBlacklist = SPEC.define( new EntityListField( "mob_blacklist",
                    new EntityList(
                            null,
                            new EntityEntry( EntityType.VEX ),
                            new EntityEntry( EntityType.SLIME ),
                            new EntityEntry( EntityType.WARDEN ),
                            new EntityEntry( ApocalypseEntities.GHOST.get() )
                    ).setNoValues(),
                    "If 'damage_mobs' is true, this field acts as a blacklist for mobs that should be an exception and NOT take damage from acid rain." ) );
            
            SPEC.newLine();
            
            acidSnow = SPEC.define( new BooleanField( "acid_snow", false,
                            "If true, colder biomes/areas where it snows will have acid snow when the acid rain event triggers." ),
                    RestartNote.WORLD );
            
            acidSnowAccumulates = SPEC.define( new BooleanField( "acid_snow_accumulates", false,
                    "If acid snow is enabled, setting this to false will stop snow layers from being placed on the ground when it is " +
                            "snowing acid." ) );
            
            SPEC.newLine();
        }
    }
    
    public static class WorldDegradation extends AbstractConfigCategory<AcidRainConfig> {
        
        public final BooleanField enableBlockDegradation;
        
        public final BlockTransformListField blockTransformations;
        
        
        WorldDegradation( AcidRainConfig parent ) {
            super( parent, "world_degradation",
                    "Settings related to block degradation in the world when it rains acid" );
            
            enableBlockDegradation = SPEC.define( new BooleanField( "enable_block_degradation", false,
                    "If enabled, acid rain will start \"corroding\" blocks it comes in contact with.",
                    "What blocks are affected and what they turn into can be configured in the below transformation list.",
                    "Note that block degradation by acid rain will not happen in snowy areas unless \"acidSnow\" is enabled in the general category." ) );
            
            blockTransformations = SPEC.define( new BlockTransformListField( "block_transformations", defaultTransformList(),
                    "A list of input blocks and what block state they turn into when exposed to acid rain.",
                    "Consider this example entry:",
                    "\"minecraft:mossy_cobblestone_stairs minecraft:cobblestone_stairs[] true\"",
                    "This will result in mossy cobble stairs turning into normal cobblestone stairs, and since the \"copy\" properties flag is set to true, " +
                            "the block state property values of the old stairs will be copied over to the new ones so we retain the rotation of the stairs." ) );
            
            SPEC.newLine();
        }
        
        private static BlockTransformList defaultTransformList() {
            return new BlockTransformList(
                    // Grass, plants, crops and small flowers
                    new BlockTransformList.Entry( Blocks.GRASS_BLOCK, null, BlockTransformList.StateProperties.Builder
                            .builder( Blocks.DIRT )
                            .build(),
                            false ),
                    new BlockTransformList.Entry( Blocks.GRASS, null, BlockTransformList.StateProperties.Builder
                            .builder( ApocalypseObjects.Blocks.DEAD_GRASS.get() )
                            .build(),
                            false ),
                    new BlockTransformList.Entry( Blocks.TALL_GRASS, null, BlockTransformList.StateProperties.Builder
                            .builder( Blocks.GRASS )
                            .build(),
                            false ),
                    new BlockTransformList.Entry( null, BlockTags.SMALL_FLOWERS, BlockTransformList.StateProperties.Builder
                            .builder( ApocalypseObjects.Blocks.DEAD_PLANT.get() )
                            .build(),
                            false ),
                    new BlockTransformList.Entry( Blocks.FERN, null, BlockTransformList.StateProperties.Builder
                            .builder( ApocalypseObjects.Blocks.DEAD_PLANT.get() )
                            .build(),
                            false ),
                    new BlockTransformList.Entry( Blocks.LARGE_FERN, null, BlockTransformList.StateProperties.Builder
                            .builder( Blocks.FERN )
                            .build(),
                            false ),
                    new BlockTransformList.Entry( Blocks.BROWN_MUSHROOM, null, BlockTransformList.StateProperties.Builder
                            .builder( Blocks.AIR )
                            .build(),
                            false ),
                    new BlockTransformList.Entry( null, BlockTags.SAPLINGS, BlockTransformList.StateProperties.Builder
                            .builder( Blocks.DEAD_BUSH )
                            .build(),
                            false ),
                    new BlockTransformList.Entry( null, BlockTags.CROPS, BlockTransformList.StateProperties.Builder
                            .builder( ApocalypseObjects.Blocks.DEAD_PLANT.get() )
                            .build(),
                            false ),
                    new BlockTransformList.Entry( Blocks.SWEET_BERRY_BUSH, null, BlockTransformList.StateProperties.Builder
                            .builder( Blocks.DEAD_BUSH )
                            .build(),
                            false ),
                    
                    // Blocks with mossy qualities
                    new BlockTransformList.Entry( Blocks.MOSSY_COBBLESTONE, null, BlockTransformList.StateProperties.Builder
                            .builder( Blocks.COBBLESTONE )
                            .build(),
                            false ),
                    new BlockTransformList.Entry( Blocks.MOSSY_COBBLESTONE_SLAB, null, BlockTransformList.StateProperties.Builder
                            .builder( Blocks.COBBLESTONE_SLAB )
                            .build(),
                            true ),
                    new BlockTransformList.Entry( Blocks.MOSSY_COBBLESTONE_WALL, null, BlockTransformList.StateProperties.Builder
                            .builder( Blocks.COBBLESTONE_WALL )
                            .build(),
                            true ),
                    new BlockTransformList.Entry( Blocks.MOSSY_COBBLESTONE_STAIRS, null, BlockTransformList.StateProperties.Builder
                            .builder( Blocks.COBBLESTONE_STAIRS )
                            .build(),
                            true ),
                    new BlockTransformList.Entry( Blocks.MOSSY_STONE_BRICKS, null, BlockTransformList.StateProperties.Builder
                            .builder( Blocks.STONE_BRICKS )
                            .build(),
                            false ),
                    new BlockTransformList.Entry( Blocks.MOSSY_STONE_BRICK_SLAB, null, BlockTransformList.StateProperties.Builder
                            .builder( Blocks.STONE_BRICK_SLAB )
                            .build(),
                            true ),
                    new BlockTransformList.Entry( Blocks.MOSSY_STONE_BRICK_WALL, null, BlockTransformList.StateProperties.Builder
                            .builder( Blocks.STONE_BRICK_WALL )
                            .build(),
                            true ),
                    new BlockTransformList.Entry( Blocks.MOSSY_STONE_BRICK_STAIRS, null, BlockTransformList.StateProperties.Builder
                            .builder( Blocks.STONE_BRICK_STAIRS )
                            .build(),
                            true )
            );
        }
    }
}
