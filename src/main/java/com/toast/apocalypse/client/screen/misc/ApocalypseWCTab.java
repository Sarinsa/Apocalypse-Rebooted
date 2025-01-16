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
 */
public class ApocalypseWCTab extends GridLayoutTab {

    private static final String TITLE = "apocalypse.createWorld.tab.more.title";

    private DoubleConfigTextField maxDifficultyField;
    private DoubleConfigTextField gracePeriodField;
    private InfoPoint maxDifficultyInfoPoint;
    private InfoPoint gracePeriodInfoPoint;


    public ApocalypseWCTab() {
        super(Component.translatable(TITLE));
        GridLayout.RowHelper rowHelper = layout
                .columnSpacing(10)
                .rowSpacing(30)
                .createRowHelper(2);

        maxDifficultyField = rowHelper.addChild(new DoubleConfigTextField(
                Minecraft.getInstance().font,
                ServerConfigHelper.DESIRED_DEFAULT_MAX_DIFFICULTY,
                0.0D,
                (double) (References.MAX_DIFFICULTY_HARD_LIMIT / References.DAY_LENGTH),
                0,
                0,
                Component.translatable(References.MAX_DIFFICULTY_CONFIG_FIELD),
                null)
        );
        maxDifficultyField.setResponder((parent) -> {
            ServerConfigHelper.updateModServerConfigValues(maxDifficultyField.getDoubleValue(), gracePeriodField.getDoubleValue());
        });

        maxDifficultyInfoPoint = rowHelper.addChild(new InfoPoint(
                0,
                0,
                Tooltip.create(Component.translatable(References.MAX_DIFFICULTY_CONFIG_FIELD_DESC)))
        );

        gracePeriodField = rowHelper.addChild(new DoubleConfigTextField(
                Minecraft.getInstance().font,
                ServerConfigHelper.DESIRED_DEFAULT_GRACE_PERIOD,
                0.0D,
                (double) (References.MAX_DIFFICULTY_HARD_LIMIT / References.DAY_LENGTH),
                0,
                0,
                Component.translatable(References.GRACE_PERIOD_CONFIG_FIELD),
                null)
        );
        gracePeriodField.setResponder((parent) -> {
            ServerConfigHelper.updateModServerConfigValues(maxDifficultyField.getDoubleValue(), gracePeriodField.getDoubleValue());
        });

        gracePeriodInfoPoint = rowHelper.addChild(new InfoPoint(
                0,
                0,
                Tooltip.create(Component.translatable(References.GRACE_PERIOD_CONFIG_FIELD_DESC)))
        );
    }

    @Override
    public void tick() {
        super.tick();

        maxDifficultyField.tick();
        gracePeriodField.tick();
    }
}
