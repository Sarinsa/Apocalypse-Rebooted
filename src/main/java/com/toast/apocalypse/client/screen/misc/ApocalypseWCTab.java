package com.toast.apocalypse.client.screen.misc;

import com.toast.apocalypse.client.screen.widget.config.DoubleConfigTextField;
import com.toast.apocalypse.client.screen.widget.config.InfoPoint;
import com.toast.apocalypse.common.core.config.util.ServerConfigHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;

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
    
    private static final String TITLE = "apocalypse.createWorld.tab.more.title";
    
    private final DoubleConfigTextField maxDifficultyField;
    private final DoubleConfigTextField gracePeriodField;
    
    
    public ApocalypseWCTab() {
        super( Component.translatable( TITLE ) );
        GridLayout.RowHelper rowHelper = layout
                .columnSpacing( 10 )
                .rowSpacing( 30 )
                .createRowHelper( 2 );
        
        maxDifficultyField = rowHelper.addChild( new DoubleConfigTextField(
                Minecraft.getInstance().font,
                ServerConfigHelper.DESIRED_DEFAULT_MAX_DIFFICULTY,
                0.0D,
                (double) (References.MAX_DIFFICULTY_HARD_LIMIT / References.DAY_LENGTH),
                0,
                0,
                Component.translatable( References.MAX_DIFFICULTY_CONFIG_FIELD ),
                null )
        );
        rowHelper.addChild( new InfoPoint(
                0,
                0,
                Tooltip.create( Component.translatable( References.MAX_DIFFICULTY_CONFIG_FIELD_DESC ) ) )
        );
        gracePeriodField = rowHelper.addChild( new DoubleConfigTextField(
                Minecraft.getInstance().font,
                ServerConfigHelper.DESIRED_DEFAULT_GRACE_PERIOD,
                0.0D,
                (double) (References.MAX_DIFFICULTY_HARD_LIMIT / References.DAY_LENGTH),
                0,
                0,
                Component.translatable( References.GRACE_PERIOD_CONFIG_FIELD ),
                null )
        );
        rowHelper.addChild( new InfoPoint(
                0,
                0,
                Tooltip.create( Component.translatable( References.GRACE_PERIOD_CONFIG_FIELD_DESC ) ) )
        );
        maxDifficultyField.setResponder( ( parent )
                -> ServerConfigHelper.updateModServerConfigValues( maxDifficultyField.getDoubleValue(), gracePeriodField.getDoubleValue() ) );
        gracePeriodField.setResponder( ( parent )
                -> ServerConfigHelper.updateModServerConfigValues( maxDifficultyField.getDoubleValue(), gracePeriodField.getDoubleValue() ) );
    }
    
    /** Called each tick to update this tab. */
    @Override
    public void tick() {
        super.tick();
        maxDifficultyField.tick();
        gracePeriodField.tick();
    }
}
