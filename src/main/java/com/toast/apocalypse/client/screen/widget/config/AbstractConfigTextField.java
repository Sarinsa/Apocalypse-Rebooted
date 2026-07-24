package com.toast.apocalypse.client.screen.widget.config;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;

/** A simple typed edit box implementation. */
public abstract class AbstractConfigTextField<T> extends EditBox {
    
    /** The current typed value of this field. */
    @Nullable
    private T currentValue;
    
    /** The default typed value of this field. */
    protected final T defaultValue;
    /** The minimum value allowed by this field. */
    protected final T minValue;
    /** The maximum value allowed by this field. */
    protected final T maxValue;
    
    /** This field's descriptor. */
    private final MutableComponent descriptor;
    
    
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
    public AbstractConfigTextField( Font font, T defaultValue, T minValue, T maxValue, int x, int y,
                                    int width, int height, @Nullable MutableComponent desc ) {
        super( font, x, y, width, height, Component.empty() );
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = defaultValue;
        
        descriptor = desc == null ? null : desc.withStyle( ChatFormatting.ITALIC ).withStyle( ChatFormatting.GRAY );
        setValue( String.valueOf( defaultValue ) );
    }
    
    /**
     * Called when the string value of this edit box changes.
     *
     * @param value The new string value in the edit box.
     */
    @Override
    @SuppressWarnings( "all" )
    public void onValueChange( String value ) {
        final T parsedValue = parseString( value );
        
        if( parsedValue != null ) {
            setTextColor( ChatFormatting.WHITE.getColor() );
            currentValue = parsedValue;
        }
        else {
            setTextColor( ChatFormatting.RED.getColor() );
        }
        super.onValueChange( value );
    }
    
    /**
     * Called when a character is typed on the keyboard.
     *
     * @param character The character that was typed.
     * @param upperCase True if the character is in upper case.
     */
    @Override
    public boolean charTyped( char character, int upperCase ) {
        if( getValue().length() >= maxValueLength() ) return false;
        return super.charTyped( character, upperCase );
    }
    
    /** @return The current parsed value of this field. */
    public final T get() {
        return currentValue == null ? defaultValue : currentValue;
    }
    
    /**
     * Attempts to parse the given string as the same value type used by this field.
     *
     * @return The given string parsed as a value as the same type as {@link T}.
     * Should return null if parsing fails.
     */
    @Nullable
    protected abstract T parseString( String string );
    
    /**
     * @param value     The current string value in this edit box.
     * @param character The character to validate.
     * @param cursorPos The cursor's index in the edit box.
     * @return True if the specified character is allowed in this edit box's value.
     */
    protected abstract boolean isValidCharacter( String value, char character, int cursorPos );
    
    /** @return The maximum string length allowed for this edit box's value. */
    protected abstract int maxValueLength();
    
    /** Called each frame to draw this component. */
    @Override
    public void render( GuiGraphics guiGraphics, int x, int y, float partialTick ) {
        super.render( guiGraphics, x, y, partialTick );
        
        // Draw descriptor text above field, if present
        if( visible && descriptor != null ) {
            guiGraphics.drawCenteredString( Minecraft.getInstance().font, descriptor, getX() + width / 2, getY() - (height / 2) - 3, -1 );
        }
    }
}
