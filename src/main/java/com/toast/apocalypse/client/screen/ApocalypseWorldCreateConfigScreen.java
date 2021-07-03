package com.toast.apocalypse.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.toast.apocalypse.client.screen.widget.config.DoubleConfigTextField;
import com.toast.apocalypse.client.screen.widget.config.InfoPoint;
import com.toast.apocalypse.common.core.config.ServerConfigHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

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
        super(new TranslationTextComponent(References.APOCALYPSE_WORLD_CREATE_CONFIG_TITLE));
        this.parent = parent;
    }

    @Override
    public void tick() {
        this.maxDifficultyField.tick();
        this.gracePeriodField.tick();
    }

    @Override
    public void init() {
        this.maxDifficultyField = new DoubleConfigTextField(
                this.font,
                ServerConfigHelper.DESIRED_DEFAULT_MAX_DIFFICULTY,
                0.0D,
                (double) (References.MAX_DIFFICULTY_HARD_LIMIT / References.DAY_LENGTH),
                this.width / 2 - 30,
                this.height / 2 - 60,
                new TranslationTextComponent(References.MAX_DIFFICULTY_CONFIG_FIELD),
                null);

        this.maxDifficultyInfoPoint = new InfoPoint(
                this.width / 2 + 35,
                this.height / 2 - 60,
                (button, matrixStack, mouseX, mouseY) -> {
                    this.renderTooltip(matrixStack, new TranslationTextComponent(References.MAX_DIFFICULTY_CONFIG_FIELD_DESC), mouseX, mouseY);
                },
                new TranslationTextComponent(References.MAX_DIFFICULTY_CONFIG_FIELD_DESC));

        this.gracePeriodField = new DoubleConfigTextField(
                this.font,
                ServerConfigHelper.DESIRED_DEFAULT_GRACE_PERIOD,
                0.0D,
                (double) (References.MAX_DIFFICULTY_HARD_LIMIT / References.DAY_LENGTH),
                this.width / 2 - 30,
                this.height / 2 - 10,
                new TranslationTextComponent(References.GRACE_PERIOD_CONFIG_FIELD),
                null);

        this.gracePeriodInfoPoint = new InfoPoint(
                this.width / 2 + 35,
                this.height / 2 - 10,
                (button, matrixStack, mouseX, mouseY) -> {
                    this.renderTooltip(matrixStack, new TranslationTextComponent(References.GRACE_PERIOD_CONFIG_FIELD_DESC), mouseX, mouseY);
                },
                new TranslationTextComponent(References.GRACE_PERIOD_CONFIG_FIELD_DESC));

        // Done button
        this.addButton(new Button(this.width / 2 - 75, this.height / 2 + 40, 70, 20, new TranslationTextComponent("gui.done"), (button) -> {
            this.minecraft.setScreen(this.parent);
            ServerConfigHelper.updateModServerConfigValues(this.maxDifficultyField.getDoubleValue(), this.gracePeriodField.getDoubleValue());
        }));

        // Cancel button
        this.addButton(new Button(this.width / 2 + 5, this.height / 2 + 40, 70, 20, new TranslationTextComponent("gui.cancel"), (button) -> {
            this.minecraft.setScreen(this.parent);
        }));

        this.children.add(this.maxDifficultyField);
        this.children.add(this.gracePeriodField);
        this.children.add(this.maxDifficultyInfoPoint);
        this.children.add(this.gracePeriodInfoPoint);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, -1);

        this.maxDifficultyField.render(matrixStack, mouseX, mouseY, partialTicks);
        this.gracePeriodField.render(matrixStack, mouseX, mouseY, partialTicks);
        this.maxDifficultyInfoPoint.render(matrixStack, mouseX, mouseY, partialTicks);
        this.gracePeriodInfoPoint.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
