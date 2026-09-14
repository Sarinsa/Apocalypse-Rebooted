package com.toast.apocalypse.common.core.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.field.collection.BlockStateSetField;

public class MiscConfig extends AbstractConfigFile {
    
    public final VersionCheck VERSION_CHECK;
    public final TrapProperties TRAP_PROPERTIES;
    public final Events EVENTS;
    public final Other OTHER;
    
    /** Builds the config spec that should be used for this config. */
    public MiscConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName, false,
                "This config contains misc settings; a bit of this and a bit of that."
        );
        BlockStateSetField.describe( SPEC );
        
        VERSION_CHECK = new VersionCheck( this );
        TRAP_PROPERTIES = new TrapProperties( this );
        EVENTS = new Events( this );
        OTHER = new Other( this );
    }
    
    public static class VersionCheck extends AbstractConfigCategory<MiscConfig> {
        
        public final BooleanField sendUpdateMessage;
        
        VersionCheck( MiscConfig parent ) {
            super( parent, "version_check",
                    "Contains settings related to version checking (when Forge looks for updates of Apocalypse)." );
            
            sendUpdateMessage = SPEC.define( new BooleanField( "send_update_message", true,
                    "If enabled, the player will receive an in-game message when a new mod update is released." ) );
        }
    }
    
    
    public static class TrapProperties extends AbstractConfigCategory<MiscConfig> {
        
        public final IntField ghostFreezeRange;
        public final IntField armorShatterRange;
        
        TrapProperties( MiscConfig parent ) {
            super( parent, "trap_properties",
                    "Contains settings related to trap types used in the Dynamic Trap.",
                    "NOTE: AoE for traps depends on the facing of the Dynamic Trap! The bounding box is not centered on the Dynamic Trap block." );
            
            ghostFreezeRange = SPEC.define( new IntField( "ghost_freeze_range", 20, 1, 60,
                    "The range for the Dynamic Trap's AoE in which the Ghost Freeze trap will affect ghosts." ) );
            armorShatterRange = SPEC.define( new IntField( "armor_shatter_range", 20, 1, 60,
                    "The range for the Dynamic Trap's AoE in which the Armor Shatter trap will affect mobs." ) );
        }
    }
    
    
    public static class Events extends AbstractConfigCategory<MiscConfig> {
        
        // TODO allow changing the message to a chat message; also possibly we can slap this in each event's config
        public final BooleanField displayStartMessage;
        
        Events( MiscConfig parent ) {
            super( parent, "events",
                    "Contains settings shared by all events from Apocalypse." );
            
            displayStartMessage = SPEC.define( new BooleanField( "display_start_message", true,
                    "If enabled, Apocalypse events will display a short message to the player when they start up." ) );
        }
    }
    
    
    public static class Other extends AbstractConfigCategory<MiscConfig> {
        
        public final BooleanField rainFizzlesTorches;
        
        public final BooleanField pauseDaylightCycle;
        
        public final IntField lunarEquipmentUpdateTime;
        
        Other( MiscConfig parent ) {
            super( parent, "other",
                    "Some uncategorized settings. Very cool!" );
            
            rainFizzlesTorches = SPEC.define( new BooleanField( "rain_fizzles_torches", true,
                    "If enabled, torches exposed to rain will become wet and fizzle out.",
                    "Wet torches give off very little light, but will reignite on their own when the rain stops." ) );
            
            SPEC.newLine();
            
            pauseDaylightCycle = SPEC.define( new BooleanField( "pause_daylight_cycle", true,
                    "(Only relevant for multiplayer) If enabled, the day-night cycle will pause if no players are online.",
                    "Useful if you want your players to be unable to just skip through full moons by disconnecting." ) );
            
            SPEC.newLine();
            
            lunarEquipmentUpdateTime = SPEC.define( new IntField( "lunar_equipment_update_time", 60, 5, 3600,
                    "Some equipment added by Apocalypse (Midnight Steel armor) has abilities or attribute modifiers that halve on new moons or change over time when it is a full moon night.",
                    "This field's value is the amount of seconds that must pass before the equipment's stats change again during full moon nights." ) );
        }
    }
}