package com.toast.apocalypse.client.screen.widget;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class DoubleConfigTextField extends TextFieldWidget {

    private double currentValue;

    private final double defaultValue;
    private final double minValue;
    private final double maxValue;

    public DoubleConfigTextField(FontRenderer fontRenderer, double defaultValue, double minValue, double maxValue, int x, int y, ITextComponent message) {
        super(fontRenderer, x, y, 60, 20, message);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.setValue(String.valueOf(defaultValue));
        this.currentValue = defaultValue;
    }

    @Override
    public void onValueChange(String value) {
        super.onValueChange(value);

        if (this.checkIsValidValue(value)) {
            this.setDoubleValue(Double.parseDouble(value));
        }
    }

    @SuppressWarnings("all")
    private boolean checkIsValidValue(String value) {
        try {
            double doubleValue = Double.parseDouble(value);

            if (doubleValue < this.minValue || doubleValue > this.maxValue) {
                this.setTextColor(TextFormatting.RED.getColor().intValue());
                return false;
            }
            else {
                this.setTextColor(TextFormatting.WHITE.getColor().intValue());
                return true;
            }
        }
        catch (Exception ignored) {
            this.setTextColor(TextFormatting.RED.getColor().intValue());
            return false;
        }
    }

    public double getDoubleValue() {
        return this.currentValue;
    }

    private void setDoubleValue(double value) {
        this.currentValue = value;
    }
}
