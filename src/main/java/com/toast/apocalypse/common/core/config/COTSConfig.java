package com.toast.apocalypse.common.core.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.IntField;

public class COTSConfig extends AbstractConfigFile {
    
    public final COTSConfig.General GENERAL;
    
    
    /** Builds the config spec that should be used for this config. */
    public COTSConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "This config contains settings related to Apocalypse's 'Call of The Shadows event'."
        );
        GENERAL = new COTSConfig.General( this );
    }
    
    
    public static class General extends AbstractConfigCategory<COTSConfig> {
        
        public final BooleanField enabled;
        public final IntField blockLightLevel;
        public final IntField skyLightLevel;
        
        General( COTSConfig parent ) {
            super( parent, "general",
                    "General event settings" );
            
            enabled = SPEC.define( new BooleanField( "enabled", true,
                    "If true, this event is enabled and will trigger if the circumstances are correct. See below settings." ) );
            blockLightLevel = SPEC.define( new IntField( "block_light_level", 0, 0, 14,
                    "The maximum required block light level for this event to start.",
                    "Block light is the light that comes from sources that are not skylight, such as torches or glowstone.",
                    "NOTE: If RyoamicLights is installed, all dynamic light sources will be considered block light for this event." ) );
            skyLightLevel = SPEC.define( new IntField( "sky_light_level", 0, 0, 14,
                    "The maximum required sky light level for this event to start.",
                    "Sky light is the light that comes from the sky (no way!). Take note that sky light does not decrease if it is night, it only decreases" +
                            " when a solid blocks obstruct view to the sky." ) );
            
            SPEC.newLine();
        }
    }
}
