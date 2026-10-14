package com.toast.apocalypse.client.screen;

import com.toast.apocalypse.api.trap.AbstractTrap;
import com.toast.apocalypse.common.blockentity.DynamicTrapBlockEntity;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.menus.DynamicTrapMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class DynamicTrapMenuScreen extends AbstractContainerScreen<DynamicTrapMenu> implements MenuAccess<DynamicTrapMenu> {
    
    /** The dynamic trap GUI texture. */
    private static final ResourceLocation TEXTURE = Apocalypse.rl( "textures/gui/container/dynamic_trap.png" );
    /** A screen rectangle representing the space taken up by the trap crafting result slot. */
    private ScreenRectangle trapSlotArea;
    
    
    /**
     * Constructs a new menu screen.
     *
     * @param container The dynamic trap container.
     * @param inventory The player's inventory (usually).
     * @param title     This menu's title component.
     */
    public DynamicTrapMenuScreen( DynamicTrapMenu container, Inventory inventory, Component title ) {
        super( container, inventory, title );
    }
    
    /** Called to initialize this container. */
    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width( title )) / 2;
        trapSlotArea = new ScreenRectangle( leftPos + 141, topPos + 35, 16, 16 );
    }
    
    /** Renders this menu screen. */
    @Override
    public void render( GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick ) {
        renderBackground( guiGraphics );
        super.render( guiGraphics, mouseX, mouseY, partialTick );
        renderTooltip( guiGraphics, mouseX, mouseY );
    }
    
    /** Handles tooltip rendering for this menu screen. */
    @Override
    protected void renderTooltip( GuiGraphics guiGraphics, int mouseX, int mouseY ) {
        super.renderTooltip( guiGraphics, mouseX, mouseY );
        if( menu.getTrapPos() == null || Minecraft.getInstance().level == null ) return;
        
        final BlockEntity blockEntity = Minecraft.getInstance().level.getExistingBlockEntity( menu.getTrapPos() );
        
        if( blockEntity instanceof DynamicTrapBlockEntity trap && trap.getCurrentTrap() != null ) {
            if( hoveringOver( trapSlotArea, mouseX, mouseY ) ) {
                final AbstractTrap trapType = trap.getCurrentTrap();
                final List<Component> components = new ArrayList<>();
                
                components.add( Component.translatable( trapType.getTranslationKey() ) );
                components.add( Component.literal( "" ) );
                components.add( trapType.getDescription().copy().withStyle( ChatFormatting.GRAY ) );
                guiGraphics.renderComponentTooltip( font, components, mouseX, mouseY );
            }
        }
    }
    
    /** Renders the background for this menu screen. */
    @Override
    protected void renderBg( GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY ) {
        guiGraphics.blit( TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight );
        guiGraphics.blit( TEXTURE, leftPos + 118, topPos + 35, 0, 183, 17, 16 );
        
        if( menu.getPreparationTime() > 0 && menu.getMaxPreparationTime() > 0 ) {
            double progress = (double) menu.getPreparationTime() / menu.getMaxPreparationTime();
            int arrowWidth = (int) (progress * 18);
            guiGraphics.blit( TEXTURE, leftPos + 118, topPos + 34, 0, 166, arrowWidth, 17 );
        }
        if( menu.getTrapPos() != null ) {
            if( Minecraft.getInstance().level != null
                    && Minecraft.getInstance().level.getExistingBlockEntity( menu.getTrapPos() ) instanceof DynamicTrapBlockEntity trap ) {
                
                if( trap.getCurrentTrap() != null ) {
                    ResourceLocation trapIcon = trap.getCurrentTrap().getIcon();
                    guiGraphics.blit( trapIcon, leftPos + 142, topPos + 35, 0, 0, 15, 15, 16, 16 );
                }
            }
        }
    }
    
    /** @return True if the mouse's current coordinates are inside the given screen rectangle. */
    private boolean hoveringOver( ScreenRectangle rectangle, double mouseX, double mouseY ) {
        int minX = rectangle.left();
        int maxX = minX + rectangle.width();
        int minY = rectangle.top();
        int maxY = minY + rectangle.height();
        return mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY;
    }
}
