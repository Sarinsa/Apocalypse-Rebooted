package com.toast.apocalypse.datagen;

import com.toast.apocalypse.common.register.ApocalypseItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class ApocalypseRecipeProvider extends RecipeProvider {

    public ApocalypseRecipeProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(ApocalypseItems.BUCKET_HELM.get(), 1)
                .requires(Items.BUCKET)
                .unlockedBy("has_bucket", has(Items.BUCKET))
                .save(consumer);
    }
}
