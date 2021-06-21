package com.toast.apocalypse.client.screen.widget.config;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class IntegerConfigTextField extends AbstractConfigTextField<Integer> {

    public IntegerConfigTextField(FontRenderer fontRenderer, Integer defaultValue, Integer minValue, Integer maxValue, int x, int y, int width, int height, @Nullable TranslationTextComponent descriptor, @Nullable Button.ITooltip tooltip) {
        super(fontRenderer, defaultValue, minValue, maxValue, x, y, width, height, descriptor, tooltip);
    }

    @Override
    protected boolean checkIsValidValue(String value) {
        try {
            int intValue = Integer.parseInt(value);

            if (intValue < this.minValue || intValue > this.maxValue) {
                return false;
            }
            else {
                return true;
            }
        }
        catch (Exception ignored) {
            return false;
        }
    }

    @Override
    protected void setCurrentValue(String value) {
        this.setCurrentValue(Integer.parseInt(value));
    }

    @Override
    protected boolean isValidCharacter(String value, char character, int index) {
        return Character.isDigit(character);
    }
}
