package com.toast.apocalypse.common.core.config;

import com.toast.apocalypse.common.core.difficulty.ReductionType;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.util.References;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.EnumField;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.field.collection.EntityMapField;
import fathertoast.crust.api.config.common.value.collection.EntityMap;
import fathertoast.crust.api.config.common.value.collection.value.DoubleValueCodec;
import fathertoast.crust.api.config.common.value.environment.EnvironmentList;

public class DifficultyConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    public DifficultyConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName, false,
                "This config contains settings related to Apocalypse difficulty."
        );
        EntityMapField.describe( SPEC );
        
        GENERAL = new General( this );
        
        SPEC.fileOnlyNewLine();
        EnvironmentListField.describe1of2( SPEC );
        EnvironmentListField.describe2of2( SPEC );
    }
    
    
    public static class General extends AbstractConfigCategory<DifficultyConfig> {
        
        public final DoubleField multiplayerMultiplier;
        public final DoubleField sleepPenaltyMultiplier;
        
        public final EnumField<ReductionType> reductionType;
        public final DoubleField reductionPercentage;
        public final DoubleField reductionLevel;
        
        public final EnvironmentListField<Double> dimensionPenaltyList;
        
        public final EntityMapField<Double> mobSpawnDifficulties;
        
        General( DifficultyConfig parent ) {
            super( parent, "general",
                    "General difficulty settings." );
            
            multiplayerMultiplier = SPEC.define( new DoubleField( "multiplayer_multiplier", 1.0, 1.0, 10.0,
                    "This is a difficulty multiplier used for when more than one player is online.",
                    "For every additional player online, the rate at which difficulty increases for everyone is multiplied by this value.",
                    "Setting this to 1.0 essentially disables this feature." ) );
            sleepPenaltyMultiplier = SPEC.define( new DoubleField( "sleep_penalty_multiplier", 2.0, DoubleField.Range.NON_NEGATIVE,
                    "This is a difficulty multiplier used for punishing the player for sleeping through the night.",
                    "When this value is greater than 1.0, it causes players who sleep through the night to have their difficulty go higher " +
                            "than if they had just stayed up the whole night.",
                    "For example, a value of 1.5 would equal a 50% increase." ) );
            
            SPEC.newLine();
            
            reductionType = SPEC.define( new EnumField<>( "difficulty_reduction_type", ReductionType.NONE,
                    "Determines how and if the player's difficulty should be reduced upon death.",
                    "  none: No difficulty reduction when the player dies.",
                    "  level: Difficulty is reduced by the number of levels specified by 'reduction_levels' upon death.",
                    "  percentage: Difficulty is reduced by the percentage specified by 'reduction_percentage' upon death.",
                    "  both: Difficulty is reduced by the number of levels specified by 'reduction_levels', then by " +
                            "the percentage specified by 'reduction_percentage' upon death." ) );
            reductionPercentage = SPEC.define( new DoubleField( "reduction_percentage", 0.25, DoubleField.Range.PERCENT,
                    "Works in conjunction with 'difficulty_reduction_type'.",
                    "If this is set to 1 (i.e., 100% reduction) and reduction type is 'percentage' or 'both', player " +
                            "difficulty will be completely reset upon death." ) );
            reductionLevel = SPEC.define( new DoubleField( "reduction_levels", References.toDays( 1 ), DoubleField.Range.NON_NEGATIVE,
                    "Works in conjunction with 'difficulty_reduction_type'." ) );
            
            SPEC.newLine();
            
            dimensionPenaltyList = SPEC.define( new EnvironmentListField<>( "dimension_penalty_list",
                    EnvironmentList.builder( DoubleValueCodec.NON_NEGATIVE )
                            .entryBuilder( 1.5 ).notInNaturalDimension().build()
                            .build(),
                    "A list of difficulty multipliers linked to dimension types.",
                    "Used for dimensions that should make the player's difficulty rise 'quicker' than normal.",
                    "Additional conditions other than dimension type can be appended here, but intended use is " +
                            "dimension type check only." ) );
            
            SPEC.newLine();
            
            mobSpawnDifficulties = SPEC.define( new EntityMapField<>( "mob_spawn_difficulty_levels",
                    new EntityMap.Builder<>( DoubleValueCodec.NON_NEGATIVE )
                            .put( ApocalypseEntities.FEARWOLF, References.toDays( 4.0 ) )
                            .build(),
                    "A list of entity types linked with a difficulty value, representing the difficulty " +
                            "level required for them to start spawning naturally in the world.",
                    "Used when checking the difficulty of the player closest to the attempted mob spawn." ) );
        }
    }
}