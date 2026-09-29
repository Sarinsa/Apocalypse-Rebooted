package com.toast.apocalypse.client.screen.misc;

import com.toast.apocalypse.client.screen.widget.config.DoubleConfigTextField;
import com.toast.apocalypse.client.screen.widget.config.InfoPoint;
import com.toast.apocalypse.common.capability.CapabilityHelper;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.util.ServerConfigHelper;
import com.toast.apocalypse.common.util.References;
import com.toast.apocalypse.datagen.lang.TranslationKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;

/**
 * Additional World Creation screen tab from Apocalypse.
 * <br><br>
 * This is the interface that allows the user to
 * specify Apocalypse difficulty properties before creating the world.
 *
 * @see ServerConfigHelper#updateModServerConfig()
 * @see com.toast.apocalypse.common.core.config.ApocalypseServerConfig
 */
public class ApocalypseWCTab extends GridLayoutTab {
    
    public static final TranslationKey MAX_DIFFICULTY_CONFIG_FIELD = Apocalypse.tk( "apocalypse.screen.text_field.create_world_config.max_difficulty",
            "Default Max Difficulty" );
    public static final TranslationKey GRACE_PERIOD_CONFIG_FIELD = Apocalypse.tk( "apocalypse.screen.text_field.create_world_config.grace_period",
            "Default Grace Period" );
    public static final TranslationKey MAX_DIFFICULTY_CONFIG_FIELD_DESC = Apocalypse.tk( "apocalypse.screen.text_field.desc.create_world_config.max_difficulty",
            "The default max difficulty level for this world/server. Your difficulty will stop increasing when this number is reached" );
    public static final TranslationKey GRACE_PERIOD_CONFIG_FIELD_DESC = Apocalypse.tk( "apocalypse.screen.text_field.desc.create_world_config.grace_period",
            "The default grace period for this world/server. This is the amount of time that must pass before your difficulty starts increasing. " +
                    "A value of 1.0 equals that of a whole Minecraft day (normally)." );
    private static final TranslationKey TITLE = Apocalypse.tk( "apocalypse.createWorld.tab.more.title",
            "Apocalypse" );
    
    private final DoubleConfigTextField maxDifficultyField;
    private final DoubleConfigTextField gracePeriodField;
    
    
    public ApocalypseWCTab() {
        super( TITLE.get() );
        GridLayout.RowHelper rowHelper = layout
                .columnSpacing( 10 )
                .rowSpacing( 30 )
                .createRowHelper( 2 );
        
        maxDifficultyField = rowHelper.addChild( new DoubleConfigTextField(
                Minecraft.getInstance().font,
                ServerConfigHelper.DESIRED_DEFAULT_MAX_DIFFICULTY,
                0.0D,
                (double) CapabilityHelper.divByDayLength( References.MAX_DIFFICULTY_HARD_LIMIT ),
                0, 0,
                60, 20,
                MAX_DIFFICULTY_CONFIG_FIELD.get() )
        );
        rowHelper.addChild( new InfoPoint(
                0,
                0,
                Tooltip.create( MAX_DIFFICULTY_CONFIG_FIELD_DESC.get() ) )
        );
        gracePeriodField = rowHelper.addChild( new DoubleConfigTextField(
                Minecraft.getInstance().font,
                ServerConfigHelper.DESIRED_DEFAULT_GRACE_PERIOD,
                0.0D,
                (double) CapabilityHelper.divByDayLength( References.MAX_DIFFICULTY_HARD_LIMIT ),
                0, 0,
                60, 20,
                GRACE_PERIOD_CONFIG_FIELD.get() )
        );
        rowHelper.addChild( new InfoPoint(
                0,
                0,
                Tooltip.create( GRACE_PERIOD_CONFIG_FIELD_DESC.get() ) )
        );
        maxDifficultyField.setResponder( ( parent )
                -> ServerConfigHelper.updateModServerConfigValues( maxDifficultyField.get(), gracePeriodField.get() ) );
        gracePeriodField.setResponder( ( parent )
                -> ServerConfigHelper.updateModServerConfigValues( maxDifficultyField.get(), gracePeriodField.get() ) );
    }
    
    /** Called each tick to update this tab. */
    @Override
    public void tick() {
        super.tick();
        maxDifficultyField.tick();
        gracePeriodField.tick();
    }
}
