package com.toast.apocalypse.client.screen.widget.config;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;

/** An integer value type text box field. */
@SuppressWarnings( "unused" )
public class IntegerConfigTextField extends AbstractConfigTextField<Integer> {
    
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
    public IntegerConfigTextField( Font font, Integer defaultValue, Integer minValue, Integer maxValue, int x, int y,
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
    protected Integer parseString( String string ) {
        try {
            final int intValue = Integer.parseInt( string );
            return intValue >= minValue && intValue <= maxValue ? intValue : null;
        }
        catch( Exception ignored ) {
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
        return Character.isDigit( character );
    }
    
    /** @return The maximum string length allowed for this edit box's value. */
    @Override
    protected int maxValueLength() {
        return 8;
    }
}
