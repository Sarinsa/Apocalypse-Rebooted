package com.toast.apocalypse.common.compat.jei;

import com.toast.apocalypse.client.screen.DynamicTrapMenuScreen;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseBlocks;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import com.toast.apocalypse.common.core.register.ApocalypseMenus;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Note: dang the JEI api is clean
@JeiPlugin
public class ApocalypseJei implements IModPlugin {


    private static final ResourceLocation ID = Apocalypse.resourceLoc("apocalypse_jei");

    public static final RecipeType<TrapRecipe> TRAP_ASSEMBLING =
            RecipeType.create(Apocalypse.MODID, "trap_assembling", TrapRecipe.class);


    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }


    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();

        registration.addRecipeCategories(new TrapCategory(jeiHelpers));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        registration.addRecipes(TRAP_ASSEMBLING,
                List.copyOf(recipeManager.byType(ApocalypseRecipeTypes.TRAP_ASSEMBLING.get()).values()));

        armorAnvilRecipes(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ApocalypseBlocks.DYNAMIC_TRAP.get()), TRAP_ASSEMBLING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(DynamicTrapMenuScreen.class, 118, 35, 16, 16, TRAP_ASSEMBLING);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(DynamicTrapMenu.class, ApocalypseMenus.DYNAMIC_TRAP.get(), TRAP_ASSEMBLING, 0, 9, 9, 36);
    }

    /** Registers anvil recipes for the armor items in Apocalypse. */
    private void armorAnvilRecipes(IRecipeRegistration registration) {
        List<IJeiAnvilRecipe> recipes = new ArrayList<>();

        for (RegistryObject<Item> regObject : ApocalypseItems.ITEMS.getEntries()) {
            if (regObject.get() instanceof ArmorItem armorItem) {
                Ingredient ingredient = armorItem.getMaterial().getRepairIngredient();
                ItemStack fullDamagePiece = new ItemStack(armorItem);
                ItemStack veryDamagedPiece = new ItemStack(armorItem);
                ItemStack halfDamagePiece = new ItemStack(armorItem);
                fullDamagePiece.setDamageValue(fullDamagePiece.getMaxDamage());
                veryDamagedPiece.setDamageValue((veryDamagedPiece.getMaxDamage() * 3 / 4));
                halfDamagePiece.setDamageValue(halfDamagePiece.getMaxDamage() / 2);

                IJeiAnvilRecipe samePieceRecipe = registration.getVanillaRecipeFactory().createAnvilRecipe(
                        veryDamagedPiece,
                        List.of(veryDamagedPiece),
                        List.of(halfDamagePiece)
                );
                IJeiAnvilRecipe materialRecipe = registration.getVanillaRecipeFactory().createAnvilRecipe(
                        fullDamagePiece,
                        List.of(ingredient.getItems()),
                        List.of(veryDamagedPiece)
                );

                // noinspection ConstantConditions
                if (samePieceRecipe != null)
                    recipes.add(samePieceRecipe);
                // noinspection ConstantConditions
                if (materialRecipe != null)
                    recipes.add(materialRecipe);
            }
        }
        registration.addRecipes(RecipeTypes.ANVIL, recipes);
    }
}
