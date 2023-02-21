package com.toast.apocalypse.datagen;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseBlocks;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import net.minecraft.data.*;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class ApocalypseRecipeProvider extends RecipeProvider {

    public ApocalypseRecipeProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {

        //----------------------- SHAPELESS ----------------------
        simpleShapelessRecipe(ApocalypseItems.MIDNIGHT_STEEL_INGOT.get(), 1, consumer, ApocalypseItems.FRAGMENTED_SOUL.get());
        simpleShapelessRecipe(ApocalypseItems.MIDNIGHT_STEEL_INGOT.get(), 9, consumer, "midnight_steel_ingots_from_midnight_steel_block", ApocalypseBlocks.MIDNIGHT_STEEL_BLOCK.get());
        simpleShapelessRecipe(ApocalypseItems.BUCKET_HELM.get(), 1, consumer, Items.BUCKET);


        //------------------------ SHAPED ------------------------
        ShapedRecipeBuilder.shaped(ApocalypseBlocks.LUNAR_PHASE_SENSOR.get().asItem(), 1)
                .pattern("###")
                .pattern("LLL")
                .pattern("SSS")
                .define('#', Tags.Items.GLASS)
                .define('L', ApocalypseItems.MIDNIGHT_STEEL_INGOT.get())
                .define('S', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_" + itemName(ApocalypseItems.MIDNIGHT_STEEL_INGOT.get()), has(ApocalypseItems.MIDNIGHT_STEEL_INGOT.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ApocalypseItems.LUNAR_CLOCK.get(), 1)
                .pattern(" # ")
                .pattern("#R#")
                .pattern(" # ")
                .define('#', ApocalypseItems.MIDNIGHT_STEEL_INGOT.get())
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_" + itemName(ApocalypseItems.MIDNIGHT_STEEL_INGOT.get()), has(ApocalypseItems.MIDNIGHT_STEEL_INGOT.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ApocalypseBlocks.MIDNIGHT_STEEL_BLOCK.get(), 1)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ApocalypseItems.MIDNIGHT_STEEL_INGOT.get())
                .unlockedBy("has_" + itemName(ApocalypseItems.MIDNIGHT_STEEL_INGOT.get()), has(ApocalypseItems.MIDNIGHT_STEEL_INGOT.get()))
                .save(consumer);
    }

    private void simpleShapelessRecipe(ItemLike result, int count, Consumer<FinishedRecipe> consumer, ItemLike... ingredients) {
        final ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(result, count);

        for (ItemLike ingredient : ingredients) {
            builder.requires(ingredient);
            String ingredientName = Objects.requireNonNull(itemName(ingredient));
            builder.unlockedBy("has_" + ingredientName, has(ingredient));
        }
        builder.save(consumer);
    }

    private void simpleShapelessRecipe(ItemLike result, int count, Consumer<FinishedRecipe> consumer, String recipeName, ItemLike... ingredients) {
        final ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(result, count);

        for (ItemLike ingredient : ingredients) {
            builder.requires(ingredient);
            String ingredientName = Objects.requireNonNull(itemName(ingredient));
            builder.unlockedBy("has_" + ingredientName, has(ingredient));
        }
        builder.save(consumer, Apocalypse.resourceLoc(recipeName));
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    private static String itemName(ItemLike itemLike) {
        Item item = itemLike.asItem();
        return ForgeRegistries.ITEMS.containsValue(item) ? ForgeRegistries.ITEMS.getKey(item).getPath() : null;
    }
}
