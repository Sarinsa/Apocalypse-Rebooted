package com.toast.apocalypse.common.core.config;

import com.toast.apocalypse.common.core.config.value.SiegeSpawnStats;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.util.References;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.field.collection.RegistryValueListField;
import fathertoast.crust.api.config.common.value.collection.RegistryValueList;
import fathertoast.crust.api.config.common.value.collection.value.DoubleValueCodec;
import fathertoast.crust.api.config.common.value.environment.EnvironmentList;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class LunarSiegeConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    public final SiegeMobProperties SIEGE_MOB_PROPS;
    
    
    /** Builds the config spec that should be used for this config. */
    public LunarSiegeConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName, false,
                "This config contains settings related to Apocalypse's Lunar Sieges / Full moon events."
        );
        RegistryValueListField.describe( SPEC );
        
        GENERAL = new General( this );
        SIEGE_MOB_PROPS = new SiegeMobProperties( this );
        
        SPEC.fileOnlyNewLine();
        EnvironmentListField.describe1of2( SPEC );
        EnvironmentListField.describe2of2( SPEC );
    }
    
    public static class General extends AbstractConfigCategory<LunarSiegeConfig> {
        
        public final BooleanField enableLunarSieges;
        public final BooleanField denySleep;
        public final BooleanField despawnMobsOnDeath;
        public final EnvironmentListField<Double> siegeSpawningConditions;
        
        General( LunarSiegeConfig parent ) {
            super( parent, "general",
                    "General event settings." );
            
            enableLunarSieges = SPEC.define( new BooleanField( "enable_lunar_sieges", true,
                    "If enabled, every full moon night a 'lunar siege' will start, spawning stronger monsters and full moon monsters.",
                    "More settings related to this can be found further down in this config." ) );
            
            denySleep = SPEC.define( new BooleanField( "deny_sleep", true,
                    "If enabled, players cannot sleep through full moon nights." ) );
            
            despawnMobsOnDeath = SPEC.define( new BooleanField( "despawn_mobs_on_death", true,
                    "If enabled, any mobs that are still alive that were summoned by X player's Lunar Siege event will despawn if their target player dies.",
                    "Can help prevent horrible spawn-camping" ) );
            
            siegeSpawningConditions = SPEC.define( new EnvironmentListField<>( "siege_spawning_chance",
                    EnvironmentList.builder( DoubleValueCodec.PERCENT )
                            .entryBuilder( 0.0 ).inTheEnd().build()
                            .build(),
                    "This is a list of environment condition entries that can be used to control under what " +
                            "circumstances lunar siege mobs can spawn. This chance is rolled for each mob spawn attempt.",
                    "If a set of conditions should PREVENT spawning, its value should be 0.0.",
                    "If a set of conditions should ALLOW spawning, its value should be 1.0.",
                    "By default, we simply prevent siege mobs from spawning in the End." ) );
        }
    }
    
    public static class SiegeMobProperties extends AbstractConfigCategory<LunarSiegeConfig> {
        
        public final DoubleField difficultyPerIncrease;
        
        public final RegistryValueListField<EntityType<?>, SiegeSpawnStats> mobSpawnSettings;
        
        SiegeMobProperties( LunarSiegeConfig parent ) {
            super( parent, "siege_mob_properties",
                    "Various properties of full moon siege mobs, such as which entities spawn, " +
                            "how many spawn, and during what difficulty." );
            
            difficultyPerIncrease = SPEC.define( new DoubleField( "difficulty_per_increase",
                    References.toDays( 1 ), 1.0, Double.POSITIVE_INFINITY,
                    "The amount of difficulty levels the player must pass for additional full moon mob counts to increase " +
                            "(this refers to the 5th value of entries in the 'mob_spawns' list below).",
                    "For example, a value of 8.0 means that for every time the player passes another 8.0 levels of difficulty " +
                            "(a typical lunar cycle), the amount of full moon mobs increases by the last number in their spawn settings below." ) );
            
            SPEC.newLine();
            
            //TODO experiment with adding some vanilla mobs here, perchance
            mobSpawnSettings = SPEC.define( new RegistryValueListField<>( "mob_spawns",
                    new RegistryValueList.Builder<>( ForgeRegistries.ENTITY_TYPES, SiegeSpawnStats.CODEC )
                            .put( ApocalypseEntities.GHOST,
                                    SiegeSpawnStats.greaterThan( References.toDays( 0 ), 4, 24, 0.5 ) )
                            .put( ApocalypseEntities.BREECHER,
                                    SiegeSpawnStats.greaterThan( References.toDays( 2 ), 2, 16, 0.25 ) )
                            .put( ApocalypseEntities.GRUMP,
                                    SiegeSpawnStats.greaterThan( References.toDays( 4 ), 2, 16, 0.5 ) )
                            .put( ApocalypseEntities.SEEKER,
                                    SiegeSpawnStats.greaterThan( References.toDays( 8 ), 1, 8, 0.25 ) )
                            .put( ApocalypseEntities.DESTROYER,
                                    SiegeSpawnStats.greaterThan( References.toDays( 16 ), 1, 8, 0.125 ) )
                            .build(),
                    "List of mobs to spawn during full moon sieges and the settings for how they scale with difficulty.",
                    "The arguments for each list entry are:",
                    "  1st - The entity type to spawn (any mob is allowed, not restricted to just Apocalypse mobs).",
                    "  2nd - The difficulty level(s) at which this entry will spawn (e.g., '>16' or '8~32').",
                    "  3rd - The number of mobs this entry will spawn in a siege at its minimum enabled difficulty level.",
                    "  4th - The maximum number of mobs this entry can spawn in a siege.",
                    "  5th - Additional mobs this entry will spawn in a siege per increase (see 'difficulty_per_increase')." ) );
        }
    }
}