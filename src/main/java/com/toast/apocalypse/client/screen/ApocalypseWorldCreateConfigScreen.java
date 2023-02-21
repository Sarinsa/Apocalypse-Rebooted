package com.toast.apocalypse.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.toast.apocalypse.client.screen.widget.config.DoubleConfigTextField;
import com.toast.apocalypse.client.screen.widget.config.InfoPoint;
import com.toast.apocalypse.common.core.config.util.ServerConfigHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterNamedRenderTypesEvent;

import javax.annotation.Nonnull;

public class ApocalypseWorldCreateConfigScreen extends Screen {

    /**
     * The screen that was previously "active".
     * This should only ever be the world creation screen.
     */
    private final Screen parent;
    private final Minecraft minecraft = Minecraft.getInstance();

    private DoubleConfigTextField maxDifficultyField;
    private DoubleConfigTextField gracePeriodField;
    private InfoPoint maxDifficultyInfoPoint;
    private InfoPoint gracePeriodInfoPoint;

    public ApocalypseWorldCreateConfigScreen(Screen parent) {
        super(Component.translatable(References.APOCALYPSE_WORLD_CREATE_CONFIG_TITLE));
        this.parent = parent;
    }

    @Override
    public void tick() {
        maxDifficultyField.tick();
        gracePeriodField.tick();
    }

    @Override
    public void init() {
        maxDifficultyField = new DoubleConfigTextField(
                font,
                ServerConfigHelper.DESIRED_DEFAULT_MAX_DIFFICULTY,
                0.0D,
                (double) (References.MAX_DIFFICULTY_HARD_LIMIT / References.DAY_LENGTH),
                width / 2 - 30,
                height / 2 - 60,
                Component.translatable(References.MAX_DIFFICULTY_CONFIG_FIELD),
                null);

        maxDifficultyInfoPoint = new InfoPoint(
                width / 2 + 35,
                height / 2 - 60,
                (button, matrixStack, mouseX, mouseY) -> {
                    renderTooltip(matrixStack, Component.translatable(References.MAX_DIFFICULTY_CONFIG_FIELD_DESC), mouseX, mouseY);
                },
                Component.translatable(References.MAX_DIFFICULTY_CONFIG_FIELD_DESC));

        gracePeriodField = new DoubleConfigTextField(
                font,
                ServerConfigHelper.DESIRED_DEFAULT_GRACE_PERIOD,
                0.0D,
                (double) (References.MAX_DIFFICULTY_HARD_LIMIT / References.DAY_LENGTH),
                width / 2 - 30,
                height / 2 - 10,
                Component.translatable(References.GRACE_PERIOD_CONFIG_FIELD),
                null);

        gracePeriodInfoPoint = new InfoPoint(
                width / 2 + 35,
                height / 2 - 10,
                (button, matrixStack, mouseX, mouseY) -> {
                    renderTooltip(matrixStack, Component.translatable(References.GRACE_PERIOD_CONFIG_FIELD_DESC), mouseX, mouseY);
                },
                Component.translatable(References.GRACE_PERIOD_CONFIG_FIELD_DESC));

        addWidget(maxDifficultyField);
        addWidget(gracePeriodField);
        addWidget(maxDifficultyInfoPoint);
        addWidget(gracePeriodInfoPoint);

        // Done button
        addRenderableWidget(new Button(width / 2 - 75, height / 2 + 40, 70, 20, Component.translatable("gui.done"), (button) -> {
            minecraft.setScreen(parent);
            ServerConfigHelper.updateModServerConfigValues(maxDifficultyField.getDoubleValue(), gracePeriodField.getDoubleValue());
        }));

        // Cancel button
        addRenderableWidget(new Button(width / 2 + 5, height / 2 + 40, 70, 20, Component.translatable("gui.cancel"), (button) -> {
            minecraft.setScreen(parent);
        }));
    }


    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        drawCenteredString(poseStack, font, title, width / 2, 20, -1);

        maxDifficultyField.render(poseStack, mouseX, mouseY, partialTicks);
        gracePeriodField.render(poseStack, mouseX, mouseY, partialTicks);
        maxDifficultyInfoPoint.render(poseStack, mouseX, mouseY, partialTicks);
        gracePeriodInfoPoint.render(poseStack, mouseX, mouseY, partialTicks);

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
