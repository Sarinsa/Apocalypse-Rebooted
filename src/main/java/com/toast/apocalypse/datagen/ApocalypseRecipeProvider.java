package com.toast.apocalypse.datagen;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseBlocks;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;

import java.util.Objects;
import java.util.function.Consumer;

public class ApocalypseRecipeProvider extends RecipeProvider {

    public ApocalypseRecipeProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {

        //----------------------- SHAPELESS ----------------------
        simpleShapelessRecipe(ApocalypseItems.LUNARIUM_INGOT.get(), 1, consumer, ApocalypseItems.SOUL_FRAGMENT.get());
        simpleShapelessRecipe(ApocalypseItems.LUNARIUM_INGOT.get(), 9, consumer, "lunarium_ingots_from_lunarium_block", ApocalypseBlocks.LUNARIUM_BLOCK.get());
        simpleShapelessRecipe(ApocalypseItems.BUCKET_HELM.get(), 1, consumer, Items.BUCKET);


        //------------------------ SHAPED ------------------------
        ShapedRecipeBuilder.shaped(ApocalypseBlocks.LUNAR_PHASE_SENSOR.get().asItem(), 1)
                .pattern("###")
                .pattern("LLL")
                .pattern("SSS")
                .define('#', Tags.Items.GLASS)
                .define('L', ApocalypseItems.LUNARIUM_INGOT.get())
                .define('S', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_" + ApocalypseItems.LUNARIUM_INGOT.get().getRegistryName().getPath(), has(ApocalypseItems.LUNARIUM_INGOT.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ApocalypseItems.LUNAR_CLOCK.get(), 1)
                .pattern(" # ")
                .pattern("#R#")
                .pattern(" # ")
                .define('#', ApocalypseItems.LUNARIUM_INGOT.get())
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_" + ApocalypseItems.LUNARIUM_INGOT.get().getRegistryName().getPath(), has(ApocalypseItems.LUNARIUM_INGOT.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ApocalypseBlocks.LUNARIUM_BLOCK.get(), 1)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ApocalypseItems.LUNARIUM_INGOT.get())
                .unlockedBy("has_" + ApocalypseItems.LUNARIUM_INGOT.get().getRegistryName().getPath(), has(ApocalypseItems.LUNARIUM_INGOT.get()))
                .save(consumer);
    }

    private void simpleShapelessRecipe(IItemProvider result, int count, Consumer<IFinishedRecipe> consumer, IItemProvider... ingredients) {
        final ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(result, count);

        for (IItemProvider ingredient : ingredients) {
            builder.requires(ingredient);
            String ingredientName = Objects.requireNonNull(ingredient.asItem().getRegistryName()).getPath();
            builder.unlockedBy("has_" + ingredientName, has(ingredient));
        }
        builder.save(consumer);
    }

    private void simpleShapelessRecipe(IItemProvider result, int count, Consumer<IFinishedRecipe> consumer, String recipeName, IItemProvider... ingredients) {
        final ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(result, count);

        for (IItemProvider ingredient : ingredients) {
            builder.requires(ingredient);
            String ingredientName = Objects.requireNonNull(ingredient.asItem().getRegistryName()).getPath();
            builder.unlockedBy("has_" + ingredientName, has(ingredient));
        }
        builder.save(consumer, Apocalypse.resourceLoc(recipeName));
    }
}
