package com.toast.apocalypse.client.config;

import com.toast.apocalypse.client.ClientUtil;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.api.config.common.value.CrustAnchor;
import org.joml.Vector3f;

public class ClientConfig extends AbstractConfigFile {
    
    public final DifficultyRenderer DIFFICULTY;
    public final Misc MISC;
    
    
    /** Builds the config spec that should be used for this config. */
    public ClientConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "This config contains various client-sided options."
        );
        DIFFICULTY = new DifficultyRenderer( this );
        MISC = new Misc( this );
    }
    
    public static class DifficultyRenderer extends AbstractConfigCategory<ClientConfig> {
        
        public final EnumField<CrustAnchor> difficultyRenderXAnchor;
        public final EnumField<CrustAnchor> difficultyRenderYAnchor;
        public final IntField difficultyRenderXOffset;
        public final IntField difficultyRenderYOffset;
        public final BooleanField offsetForBossBar;
        
        public final BooleanField renderDifficultyInCreative;
        public final BooleanField keybindOnly;
        
        
        DifficultyRenderer( ClientConfig parent ) {
            super( parent, "difficulty_renderer",
                    "Contains display settings for the difficulty counter shown in-game." );
            
            difficultyRenderXAnchor = SPEC.define( new EnumField<>( "x_anchor", CrustAnchor.CENTER, CrustAnchor.HORIZONTAL,
                    "Determines the base X position on the screen where the difficulty counter should render." ) );
            difficultyRenderYAnchor = SPEC.define( new EnumField<>( "y_anchor", CrustAnchor.TOP, CrustAnchor.VERTICAL,
                    "Determines the base Y position on the screen where the difficulty counter should render." ) );
            difficultyRenderXOffset = SPEC.define( new IntField( "x_offset", 0, IntField.Range.ANY,
                    "Additional X offset for where to render the difficulty counter." ) );
            difficultyRenderYOffset = SPEC.define( new IntField( "y_offset", 2, IntField.Range.ANY,
                    "Additional Y offset for where to render the difficulty counter." ) );
            offsetForBossBar = SPEC.define( new BooleanField( "offset_boss_bar", true,
                    "If enabled and y_anchor is \"TOP\", a small Y offset is applied so we don't overlap with the boss bar when it is active." ) );
            
            SPEC.newLine();
            
            renderDifficultyInCreative = SPEC.define( new BooleanField( "display_in_creative", true,
                    "If disabled, difficulty counter will not be displayed in creative mode." ) );
            keybindOnly = SPEC.define( new BooleanField( "keybind_only", false,
                    "If enabled, difficulty counter will only be displayed if the Apocalypse difficulty keybind is held." ) );
            
            
            SPEC.newLine();
        }
    }
    
    public static class Misc extends AbstractConfigCategory<ClientConfig> {
        
        
        public final InjectionWrapperField<ColorIntField> rainColor;
        public final BooleanField renderAcidRain;
        
        
        Misc( ClientConfig parent ) {
            super( parent, "misc",
                    "Some misc settings." );
            
            rainColor = SPEC.define( new InjectionWrapperField<>( new ColorIntField( "acid_rain_color", 0xACFF75, false,
                    "Decides the color of Apocalypse's acid rain." ),
                    ( color ) -> {
                        ClientUtil.RAIN_COLOR = new Vector3f( color.getRed(), color.getGreen(), color.getBlue() );
                    } )
            );
            renderAcidRain = SPEC.define( new BooleanField( "render_acid_rain", true,
                    "If enabled, Apocalypse's custom weather renderer is used when it rains acid.",
                    "Might break if other mods are installed that changes weather rendering." ) );
            
            SPEC.newLine();
        }
    }
}
