package com.toast.apocalypse.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.toast.apocalypse.client.screen.widget.DoubleConfigTextField;
import com.toast.apocalypse.common.network.NetworkHelper;
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
                200.0D,
                0.0D,
                (double) (References.MAX_DIFFICULTY_HARD_LIMIT / References.DAY_LENGTH),
                this.width / 2,
                this.height / 2 - 60,
                new TranslationTextComponent(References.MAX_DIFFICULTY_CONFIG_FIELD));

        this.gracePeriodField = new DoubleConfigTextField(
                this.font,
                1.0D,
                0.0D,
                (double) (References.MAX_DIFFICULTY_HARD_LIMIT / References.DAY_LENGTH),
                this.width / 2,
                this.height / 2 - 20,
                new TranslationTextComponent(References.GRACE_PERIOD_CONFIG_FIELD));

        this.addButton(new Button(this.width / 2 - 40, this.height / 2 + 40, 70, 20, new TranslationTextComponent("gui.done"), (button) -> {
            this.minecraft.setScreen(this.parent);
            NetworkHelper.sendServerConfigUpdate(this.maxDifficultyField.getDoubleValue(), this.gracePeriodField.getDoubleValue());
        }));
        this.addButton(new Button(this.width / 2 + 40, this.height / 2 + 40, 70, 20, new TranslationTextComponent("gui.cancel"), (button) -> {
            this.minecraft.setScreen(this.parent);
        }));
        this.children.add(this.maxDifficultyField);
        this.children.add(this.gracePeriodField);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, -1);

        this.maxDifficultyField.render(matrixStack, mouseX, mouseY, partialTicks);
        this.gracePeriodField.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
