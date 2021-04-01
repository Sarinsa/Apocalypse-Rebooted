package com.toast.apocalypse.datagen;

import com.toast.apocalypse.common.register.ApocalypseItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;

import java.util.Objects;
import java.util.function.Consumer;

public class ApocalypseRecipeProvider extends RecipeProvider {

    public ApocalypseRecipeProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        this.simpleShapelessRecipe(ApocalypseItems.BUCKET_HELM.get(), 1, consumer, Items.BUCKET);
    }

    private void simpleShapelessRecipe(IItemProvider result, int count, Consumer<IFinishedRecipe> consumer, IItemProvider... ingredients) {
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(result, count);

        for (IItemProvider ingredient : ingredients) {
            builder.requires(ingredient);
            String ingredientName = Objects.requireNonNull(ingredient.asItem().getRegistryName()).getPath();
            builder.unlockedBy("has_" + ingredientName, has(ingredient));
        }
        builder.save(consumer);
    }
}
