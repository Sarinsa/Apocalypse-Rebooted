package com.toast.apocalypse.common.compat.jei;

import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public abstract class BaseRecipeCategory<T> implements IRecipeCategory<T> {
    
    /** Access to JEI's base helper provider. */
    protected final IJeiHelpers jeiHelpers;
    /** Access to JEI's GUI helper. Makes animating things easier. */
    protected final IGuiHelper guiHelper;
    
    
    public BaseRecipeCategory( IJeiHelpers helpers ) {
        jeiHelpers = helpers;
        guiHelper = helpers.getGuiHelper();
    }
    
    
    /**
     * Helper method for drawing a string that displays how long
     * this recipe takes to complete, like with furnace smelting or brewing.
     */
    @SuppressWarnings( "SameParameterValue" )
    protected void drawPreparationTime( int preparationTime, GuiGraphics guiGraphics, int y ) {
        if( preparationTime > 0 ) {
            int cookTimeSeconds = preparationTime / 20;
            Component timeString = Component.translatable( "gui.jei.category.smelting.time.seconds", cookTimeSeconds );
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            int stringWidth = fontRenderer.width( timeString );
            guiGraphics.drawString( fontRenderer, timeString, getWidth() - stringWidth, y, 0xFF808080, false );
        }
    }
    
    /**
     * @return True if the given mouse coordinates are currently within a
     * 16x16 rectangle (default slot size) stretching out from the given X -and Y position.
     */
    @SuppressWarnings( "SameParameterValue" )
    protected boolean hoveringOverSlotAt( int posX, int posY, double mouseX, double mouseY ) {
        return mouseX >= posX && mouseX <= (posX + 16)
                && mouseY >= posY && mouseY <= posY + 16;
    }
}
