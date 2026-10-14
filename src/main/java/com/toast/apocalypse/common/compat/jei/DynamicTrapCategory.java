package com.toast.apocalypse.common.compat.jei;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.api.trap.AbstractTrap;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.recipe.TrapRecipe;
import com.toast.apocalypse.datagen.lang.TranslationKey;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DynamicTrapCategory extends BaseRecipeCategory<TrapRecipe> {
    
    /** The Dynamic Trap category's title component. */
    public static final TranslationKey TRAP_CATEGORY_TITLE = Apocalypse.tk( "jei.recipe_category.apocalypse.title",
            "Trap Assembling" );
    
    /** The Dynamic Trap base GUI texture, which everything else is drawn on top of. */
    private static final ResourceLocation GUI_TEXTURE = Apocalypse.rl( "textures/gui/container/dynamic_trap.png" );
    /** The trap result slot texture. */
    private static final ResourceLocation RESULT_SLOT = Apocalypse.rl( "textures/gui/container/components/trap_result_slot.png" );
    
    /** The width and height of this category's background space. */
    private final int width = 140, height = 54;
    
    // Drawables
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final IDrawable resultSlotBackground;
    private final IDrawable progressBackground;
    private final IDrawableAnimated progress;
    
    
    public DynamicTrapCategory( IJeiHelpers jeiHelpers ) {
        super( jeiHelpers );
        background = guiHelper.createBlankDrawable( width, height );
        icon = guiHelper.createDrawableItemStack( new ItemStack( ApocalypseObjects.Blocks.DYNAMIC_TRAP.get() ) );
        slotBackground = guiHelper.getSlotDrawable();
        resultSlotBackground = guiHelper.drawableBuilder( RESULT_SLOT, 0, 0, 32, 32 ).setTextureSize( 32, 32 ).build();
        progressBackground = guiHelper.createDrawable( GUI_TEXTURE, 0, 183, 17, 16 );
        
        progress = guiHelper.createAnimatedDrawable(
                guiHelper.createDrawable( GUI_TEXTURE, 0, 166, 18, 16 ),
                100,
                IDrawableAnimated.StartDirection.LEFT,
                false
        );
    }
    
    
    /** @return the type of recipe that this category handles. */
    @Override
    public RecipeType<TrapRecipe> getRecipeType() {
        return ApocalypseJei.ModRecipeTypes.TRAP_ASSEMBLING;
    }
    
    /**
     * @return A text component representing the name of this recipe type.
     * Drawn at the top of the recipe GUI pages for this category.
     */
    @Override
    public Component getTitle() {
        return TRAP_CATEGORY_TITLE.get();
    }
    
    /** @return The drawable background for a single recipe in this category. */
    @Override
    public IDrawable getBackground() {
        return background;
    }
    
    /**
     * Icon for the category tab.
     * You can use {@link IGuiHelper#createDrawableIngredient(IIngredientType, Object)}
     * to create a drawable from an ingredient.
     *
     * @return icon to draw on the category tab, max size is 16x16 pixels.
     */
    @Override
    public IDrawable getIcon() {
        return icon;
    }
    
    /**
     * Sets all the recipe's ingredients by filling out an instance of {@link IRecipeLayoutBuilder}.
     * This is used by JEI for lookups, to figure out what ingredients are inputs and outputs for a recipe.
     */
    @Override
    public void setRecipe( IRecipeLayoutBuilder builder, TrapRecipe recipe, IFocusGroup focuses ) {
        builder.setShapeless();
        int slotIndex = 0;
        
        for( int x = 0; x < 3; x++ ) {
            for( int y = 0; y < 3; y++ ) {
                builder.addSlot( RecipeIngredientRole.INPUT, (y * 18) + 1, (x * 18) + 1 )
                        .addIngredients( recipe.getIngredients().get( slotIndex ) )
                        .setBackground( slotBackground, -1, -1 );
                slotIndex++;
            }
        }
    }
    
    /**
     * Draw extras or additional info about the recipe.
     * Use the mouse position for things like button highlights.
     * Tooltips are handled by {@link mezz.jei.api.recipe.category.IRecipeCategory#getTooltipStrings(Object, IRecipeSlotsView, double, double)}
     *
     * @param recipe          the current recipe being drawn.
     * @param recipeSlotsView a view of the current recipe slots being drawn.
     * @param guiGraphics     the current {@link GuiGraphics} for rendering.
     * @param mouseX          the X position of the mouse, relative to the recipe.
     * @param mouseY          the Y position of the mouse, relative to the recipe.
     * @see IDrawable for a simple class for drawing things.
     * @see IGuiHelper for useful functions.
     * @see IRecipeSlotsView for information about the ingredients that are currently being drawn.
     */
    @Override
    public void draw( TrapRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY ) {
        progressBackground.draw( guiGraphics, 64, 19 );
        progress.draw( guiGraphics, 64, 18 );
        resultSlotBackground.draw( guiGraphics, width - (width / 3) - 8, (height / 2) - 16 );
        guiGraphics.blit( recipe.getResultTrap().getIcon(), width - (width / 3), (height / 2) - 8, 0, 0, 15, 15, 16, 16 );
        
        drawPreparationTime( recipe.getPreparationTime(), guiGraphics, height - 10 );
    }
    
    /**
     * Get the tooltip for whatever is under the mouse.
     * Ingredient tooltips are already handled by JEI, this is for anything else.
     * <p>
     * To add to ingredient tooltips, see {@link IRecipeSlotBuilder#addTooltipCallback(IRecipeSlotTooltipCallback)}
     *
     * @param recipe          the current recipe being drawn.
     * @param recipeSlotsView a view of the current recipe slots being drawn.
     * @param mouseX          the X position of the mouse, relative to the recipe.
     * @param mouseY          the Y position of the mouse, relative to the recipe.
     * @return Tooltip strings. If there is no tooltip at this position, return an empty list.
     */
    @Override
    public List<Component> getTooltipStrings( TrapRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY ) {
        if( hoveringOverSlotAt( width - (width / 3), (height / 2) - 8, mouseX, mouseY ) ) {
            AbstractTrap trapAction = recipe.getResultTrap();
            return List.of(
                    Component.translatable( trapAction.getTranslationKey() ),
                    Component.literal( "" ),
                    trapAction.getDescription().copy().withStyle( ChatFormatting.GRAY ) );
        }
        return List.of();
    }
}
