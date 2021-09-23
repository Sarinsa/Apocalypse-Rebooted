package com.toast.apocalypse.client.screen.widget.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public abstract class AbstractConfigTextField<T> extends TextFieldWidget {

    private final TranslationTextComponent descriptor;
    private final Button.ITooltip tooltip;
    private T currentValue;

    protected final T defaultValue;
    protected final T minValue;
    protected final T maxValue;

    public AbstractConfigTextField(FontRenderer fontRenderer, T defaultValue, T minValue, T maxValue, int x, int y, int width, int height, @Nullable TranslationTextComponent descriptor, @Nullable Button.ITooltip tooltip) {
        super(fontRenderer, x, y, width, height, StringTextComponent.EMPTY);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.setValue(String.valueOf(defaultValue));
        this.currentValue = defaultValue;
        this.descriptor = descriptor == null ? null : (TranslationTextComponent) descriptor.withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.GRAY);
        this.tooltip = tooltip;
    }

    @Override
    @SuppressWarnings("all")
    public void onValueChange(String value) {
        super.onValueChange(value);

        if (this.checkIsValidValue(value)) {
            this.setTextColor(TextFormatting.WHITE.getColor());
            this.setCurrentValue(value);
        }
        else {
            this.setTextColor(TextFormatting.RED.getColor());
        }
    }

    @Override
    public boolean charTyped(char character, int upperCase) {
        if (!this.canConsumeInput()) {
            return false;
        }
        else if (this.isValidCharacter(this.getValue(), character, this.getCursorPosition())) {
            if (this.isEditable() && this.getValue().length() < this.maxValueLength()) {
                this.insertText(Character.toString(character));
            }
            return true;
        }
        else {
            return false;
        }
    }

    protected abstract boolean checkIsValidValue(String value);

    public final T getCurrentValue() {
        return this.currentValue;
    }

    public final void setCurrentValue(T value) {
        this.currentValue = value;
    }

    protected abstract void setCurrentValue(String value);

    protected abstract boolean isValidCharacter(String value, char character, int cursorPosition);

    protected abstract int maxValueLength();

    @Nullable
    public ITextComponent getDescriptor() {
        return this.descriptor;
    }

    @Nullable
    public Button.ITooltip getTooltip() {
        return this.tooltip;
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTick) {
        super.render(matrixStack, x, y, partialTick);

        if (this.visible && this.descriptor != null) {
            Screen.drawCenteredString(matrixStack, Minecraft.getInstance().font, this.descriptor, this.x + this.width / 2, this.y - (this.height / 2) - 3, -1);
        }
    }
}
