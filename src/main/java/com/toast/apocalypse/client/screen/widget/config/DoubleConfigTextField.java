package com.toast.apocalypse.client.screen.widget.config;


import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;

/** A double value type text box field. */
public class DoubleConfigTextField extends AbstractConfigTextField<Double> {
    
    /**
     * Creates a new instance.
     *
     * @param font         The font renderer to use for drawing text.
     * @param defaultValue This field's default value.
     * @param minValue     The minimum value this field allows.
     * @param maxValue     The maximum value this field allows.
     * @param x            The X position of this field.
     * @param y            The Y position of this field.
     * @param width        The width of this field.
     * @param height       The height of this field.
     * @param desc         This field's descriptor, which is displayed above this field by default.
     */
    public DoubleConfigTextField( Font font, double defaultValue, double minValue, double maxValue, int x, int y,
                                  int width, int height, @Nullable MutableComponent desc ) {
        super( font, defaultValue, minValue, maxValue, x, y, width, height, desc );
    }
    
    
    /**
     * Attempts to parse the given string as a value of the same type as this field.
     *
     * @param string The string value to try and parse.
     * @return The value that was parsed from the specified string,
     * or null if parsing failed.
     */
    @Override
    @Nullable
    protected Double parseString( String string ) {
        try {
            final double doubleValue = Double.parseDouble( string );
            return doubleValue >= minValue && doubleValue <= maxValue ? doubleValue : null;
        }
        catch( NumberFormatException ignored ) {
            return null;
        }
    }
    
    /**
     * @param value     The current string value in this edit box.
     * @param character The character to validate.
     * @param cursorPos The cursor's index in the edit box.
     * @return True if the specified character is allowed in this edit box's value.
     */
    @Override
    protected boolean isValidCharacter( String value, char character, int cursorPos ) {
        if( Character.isDigit( character ) ) {
            return true;
        }
        else if( character == '.' ) {
            return !value.contains( "." ) && cursorPos != 0;
        }
        else if( character == '-' ) {
            return !value.contains( "-" ) && cursorPos == 0;
        }
        return false;
    }
    
    /** @return The maximum string length allowed for this edit box's value. */
    @Override
    protected int maxValueLength() {
        return 8;
    }
}
