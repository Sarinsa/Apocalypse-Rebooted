package com.toast.apocalypse.client.screen.widget.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/** An unpressable button that displays a tooltip. */
public class InfoPoint extends Button {
    
    
    public InfoPoint( int x, int y, Tooltip tooltip ) {
        super( x, y, 20, 20, Component.literal( "?" ), ( button ) -> { }, Supplier::get );
        setTooltip( tooltip );
    }
    
    /**
     * Called when a mouse button is clicked.
     *
     * @param mouseKey The mouse key that was clicked (see {@link InputConstants.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseClicked( double mouseX, double mouseY, int mouseKey ) {
        return false;
    }
    
    /** Called when this button is either clicked or activated with a keystroke. */
    @Override
    public void onPress() { }
    
    /**
     * Called when a keyboard key is pressed.
     *
     * @param key      The keyboard key that was pressed (see {@link InputConstants.Type#KEYSYM}).
     * @param scancode The system-specific scancode of the key (see {@link InputConstants.Type#SCANCODE}).
     * @param mods     Bitfield describing which modifier keys were held down.
     * @return True if the event has been handled.
     * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
     */
    @Override
    public boolean keyPressed( int key, int scancode, int mods ) {
        return false;
    }
}
