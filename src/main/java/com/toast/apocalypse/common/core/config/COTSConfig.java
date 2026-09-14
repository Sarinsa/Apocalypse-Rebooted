package com.toast.apocalypse.common.core.config;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.field.collection.RegistryWeightedListField;
import fathertoast.crust.api.config.common.value.collection.RegistryWeightedList;
import fathertoast.crust.api.config.common.value.collection.value.BooleanValueCodec;
import fathertoast.crust.api.config.common.value.environment.EnvironmentList;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class COTSConfig extends AbstractConfigFile {
    
    public final COTSConfig.General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    public COTSConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName, false,
                "This config contains settings related to Apocalypse's 'Call of The Shadows' event."
        );
        GENERAL = new COTSConfig.General( this );
        
        SPEC.fileOnlyNewLine();
        EnvironmentListField.describe1of2( SPEC );
        EnvironmentListField.describe2of2( SPEC );
    }
    
    
    public static class General extends AbstractConfigCategory<COTSConfig> {
        
        public final BooleanField enabled;
        public final IntField.RandomRange blindnessTicks;
        
        public final EnvironmentListField<Boolean> conditions;
        public final RegistryWeightedListField<EntityType<?>> spawnList;
        
        General( COTSConfig parent ) {
            super( parent, "general",
                    "General event settings" );
            
            enabled = SPEC.define( new BooleanField( "enabled", true,
                    "If true, this event is enabled and will trigger if the circumstances are correct. See below settings." ) );
            
            SPEC.newLine();
            
            blindnessTicks = new IntField.RandomRange( SPEC, "blindness_ticks", 40, 60, IntField.Range.TOKEN_NEGATIVE,
                    "The min and max (inclusive) duration in ticks for the blindness potion effect that is applied to players when this event spawns a mob." );
            
            SPEC.newLine();
            
            conditions = SPEC.define( new EnvironmentListField<>( "conditions", createDefaultConditions(),
                    "A list of environment conditions that must be met for this event to start.",
                    "By default, the event only triggers in complete darkness, and not in The End or Nether.",
                    "If setting the brightness higher, it may be wise to raise the Shadefiend's light tolerance in the entities config." ) );
            spawnList = SPEC.define( new RegistryWeightedListField<>( "spawn_list", createDefaultSpawnList(),
                    "A weighted list of mobs that can be spawned by this event.",
                    "Leaving this list empty will effectively disable this event." ) );
        }
        
        private static EnvironmentList<Boolean> createDefaultConditions() {
            return EnvironmentList.builder( BooleanValueCodec.DEFAULT_FALSE )
                    .entryBuilder( true )
                    .belowBrightness( 1 ).and()
                    .notInTheEnd().and()
                    .notInNether()
                    .build().build();
        }
        
        private static RegistryWeightedList<EntityType<?>> createDefaultSpawnList() {
            return new RegistryWeightedList.Builder<>( ForgeRegistries.ENTITY_TYPES )
                    .add( 100, ApocalypseObjects.EntityTypes.SHADEFIEND )
                    .add( 10, ApocalypseObjects.EntityTypes.GHOST )
                    .add( 10, EntityType.SKELETON )
                    .build();
        }
    }
}