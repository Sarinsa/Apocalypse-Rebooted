package com.toast.apocalypse.common.compat.jei;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.client.screen.DynamicTrapMenuScreen;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import com.toast.apocalypse.common.core.register.ApocalypseRecipeTypes;
import com.toast.apocalypse.common.menus.DynamicTrapMenu;
import com.toast.apocalypse.common.recipe.TrapRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ApocalypseJei implements IModPlugin {
    
    /** This plugin's ID. */
    private static final ResourceLocation ID = Apocalypse.rl( "apocalypse_jei" );
    
    
    /** Contains the recipe types added by Apocalypse. */
    interface ModRecipeTypes {
        RecipeType<TrapRecipe> TRAP_ASSEMBLING = RecipeType.create( Apocalypse.MOD_ID, "trap_assembling", TrapRecipe.class );
    }
    
    
    /** The unique ID for this mod plugin. The namespace should be your mod's modId. */
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }
    
    /**
     * Register the categories handled by this plugin.
     * These are registered before recipes so they can be checked for validity.
     */
    @Override
    public void registerCategories( IRecipeCategoryRegistration registration ) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        registration.addRecipeCategories( new DynamicTrapCategory( jeiHelpers ) );
    }
    
    /** Register modded recipes. */
    @Override
    @SuppressWarnings( "ConstantConditions" )
    public void registerRecipes( IRecipeRegistration registration ) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        registration.addRecipes( ModRecipeTypes.TRAP_ASSEMBLING, List.copyOf( recipeManager.byType( ApocalypseRecipeTypes.TRAP_ASSEMBLING.get() ).values() ) );
        armorAnvilRecipes( registration );
    }
    
    /**
     * Register recipe catalysts.
     * Recipe Catalysts are ingredients that are needed in order to craft other things.
     * Vanilla examples of Recipe Catalysts are the Crafting Table and Furnace.
     */
    @Override
    public void registerRecipeCatalysts( IRecipeCatalystRegistration registration ) {
        registration.addRecipeCatalyst( new ItemStack( ApocalypseObjects.Blocks.DYNAMIC_TRAP.get() ), ModRecipeTypes.TRAP_ASSEMBLING );
    }
    
    /**
     * Register various GUI-related things for your mod.
     * This includes adding clickable areas in your guis to open JEI,
     * and adding areas on the screen that JEI should avoid drawing.
     */
    @Override
    public void registerGuiHandlers( IGuiHandlerRegistration registration ) {
        registration.addRecipeClickArea( DynamicTrapMenuScreen.class, 118, 35, 16, 16, ModRecipeTypes.TRAP_ASSEMBLING );
    }
    
    /** Register recipe transfer handlers (move ingredients from the inventory into crafting GUIs). */
    @Override
    @SuppressWarnings( "unchecked" )
    public void registerRecipeTransferHandlers( IRecipeTransferRegistration registration ) {
        registration.addRecipeTransferHandler( DynamicTrapMenu.class, (MenuType<? super DynamicTrapMenu>) ApocalypseObjects.MenuTypes.DYNAMIC_TRAP.get(), ModRecipeTypes.TRAP_ASSEMBLING,
                0, 9, 9, 36 );
    }
    
    /** Registers anvil recipes for Apocalypse's armor and equipment items. */
    @SuppressWarnings( "ConstantConditions" )
    private void armorAnvilRecipes( IRecipeRegistration registration ) {
        final List<IJeiAnvilRecipe> recipes = new ArrayList<>();
        
        for( RegistryObject<Item> regObject : ApocalypseItems.REGISTRY.getEntries() ) {
            if( regObject.get() instanceof ArmorItem armorItem ) {
                Ingredient ingredient = armorItem.getMaterial().getRepairIngredient();
                ItemStack fullDamagePiece = new ItemStack( armorItem );
                ItemStack veryDamagedPiece = new ItemStack( armorItem );
                ItemStack halfDamagePiece = new ItemStack( armorItem );
                fullDamagePiece.setDamageValue( fullDamagePiece.getMaxDamage() );
                veryDamagedPiece.setDamageValue( (veryDamagedPiece.getMaxDamage() * 3 / 4) );
                halfDamagePiece.setDamageValue( halfDamagePiece.getMaxDamage() / 2 );
                
                IJeiAnvilRecipe samePieceRecipe = registration.getVanillaRecipeFactory().createAnvilRecipe(
                        veryDamagedPiece,
                        List.of( veryDamagedPiece ),
                        List.of( halfDamagePiece )
                );
                IJeiAnvilRecipe materialRecipe = registration.getVanillaRecipeFactory().createAnvilRecipe(
                        fullDamagePiece,
                        List.of( ingredient.getItems() ),
                        List.of( veryDamagedPiece )
                );
                if( samePieceRecipe != null ) recipes.add( samePieceRecipe );
                if( materialRecipe != null ) recipes.add( materialRecipe );
            }
        }
        registration.addRecipes( RecipeTypes.ANVIL, recipes );
    }
}
