package com.toast.apocalypse.client.config;

import com.toast.apocalypse.client.ClientUtil;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.api.config.common.value.CrustAnchor;
import org.joml.Vector3f;

public class ClientConfig extends AbstractConfigFile {
    
    public final DifficultyOverlay DIFFICULTY_OVERLAY;
    public final Misc MISC;
    
    /** Builds the config spec that should be used for this config. */
    public ClientConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName, true,
                "This config contains various client-sided options."
        );
        DIFFICULTY_OVERLAY = new DifficultyOverlay( this );
        MISC = new Misc( this );
    }
    
    
    public static class DifficultyOverlay extends AbstractConfigCategory<ClientConfig> {
        
        public final EnumField<CrustAnchor> xAnchor;
        public final EnumField<CrustAnchor> yAnchor;
        public final IntField xOffset;
        public final IntField yOffset;
        
        public final IntField bossBarXOffset;
        public final IntField bossBarYOffset;
        public final BooleanField stackBossBarXOffset;
        public final BooleanField stackBossBarYOffset;
        
        public final BooleanField renderDifficultyInCreative;
        public final BooleanField keybindOnly;
        
        DifficultyOverlay( ClientConfig parent ) {
            super( parent, "difficulty_overlay",
                    "Contains display settings for the difficulty overlay displayed in-game." );
            
            xAnchor = SPEC.define( new EnumField<>( "x_anchor", CrustAnchor.CENTER, CrustAnchor.HORIZONTAL,
                    "Determines the base X position on the screen where the difficulty counter should render." ) );
            yAnchor = SPEC.define( new EnumField<>( "y_anchor", CrustAnchor.TOP, CrustAnchor.VERTICAL,
                    "Determines the base Y position on the screen where the difficulty counter should render." ) );
            xOffset = SPEC.define( new IntField( "x_offset", 0, IntField.Range.ANY,
                    "The base X offset to apply to the difficulty counter's position." ) );
            yOffset = SPEC.define( new IntField( "y_offset", 2, IntField.Range.ANY,
                    "The base Y offset to apply to the difficulty counter's position." ) );
            
            SPEC.newLine();
            
            bossBarXOffset = SPEC.define( new IntField( "boss_bar.x_offset", 0, IntField.Range.ANY,
                    "The X offset to apply if a boss bar is currently visible." ) );
            bossBarYOffset = SPEC.define( new IntField( "boss_bar.y_offset", 20, IntField.Range.ANY,
                    "The Y offset to apply if a boss bar is currently visible." ) );
            stackBossBarXOffset = SPEC.define( new BooleanField( "boss_bar.stack_x_offset", false,
                    "If enabled, the boss bar X offset will be applied once per visible boss bar." ) );
            stackBossBarYOffset = SPEC.define( new BooleanField( "boss_bar.stack_y_offset", true,
                    "If enabled, the boss bar Y offset will be applied once per visible boss bar." ) );
            
            SPEC.newLine();
            
            renderDifficultyInCreative = SPEC.define( new BooleanField( "display_in_creative", true,
                    "If disabled, difficulty counter will not be displayed in creative mode." ) );
            keybindOnly = SPEC.define( new BooleanField( "keybind_only", false,
                    "If enabled, difficulty counter will only be displayed if the Apocalypse difficulty keybind is held." ) );
        }
    }
    
    public static class Misc extends AbstractConfigCategory<ClientConfig> {
        
        public final InjectionWrapperField<Integer, ColorIntField> rainColor;
        public final BooleanField renderAcidRain;
        
        Misc( ClientConfig parent ) {
            super( parent, "misc",
                    "Some misc settings." );
            
            rainColor = SPEC.define( new InjectionWrapperField<>( new ColorIntField( "acid_rain_color", 0xACFF75, false,
                    "Decides the color of Apocalypse's acid rain." ),
                    ( color ) -> ClientUtil.RAIN_COLOR = new Vector3f( color.getRed(), color.getGreen(), color.getBlue() ) )
            );
            renderAcidRain = SPEC.define( new BooleanField( "render_acid_rain", true,
                    "If enabled, Apocalypse's custom weather renderer is used when it rains acid.",
                    "Might break if other mods are installed that changes weather rendering." ) );
        }
    }
}