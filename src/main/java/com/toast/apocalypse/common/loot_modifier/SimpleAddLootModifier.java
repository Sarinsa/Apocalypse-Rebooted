package com.toast.apocalypse.common.loot_modifier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public class SimpleAddLootModifier extends LootModifier {

    private final ResourceLocation[] targetLootTables;
    private final Supplier<Item> itemToAdd;
    private final double chance;
    private final int minAmount;
    private final int maxAmount;

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    public SimpleAddLootModifier(ILootCondition[] conditionsIn, ResourceLocation[] targetLootTables, Supplier<Item> itemToAdd, double chance, int minAmount, int maxAmount) {
        super(conditionsIn);

        if (minAmount < 1 || minAmount > maxAmount || chance > 1 || chance < 0) {
            throw new IllegalArgumentException("SimpleAddLootModifier does not support minAmount below 1 or minAmount being greater than maxAmount. Chance must also be between 0-1.");
        }
        this.targetLootTables = targetLootTables;
        this.itemToAdd = itemToAdd;
        this.chance = chance;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        for (ResourceLocation rl : targetLootTables) {
            if (context.getQueriedLootTableId().equals(rl)) {
                Random random = context.getRandom();

                if (random.nextDouble() <= chance) {
                    generatedLoot.add(new ItemStack(itemToAdd.get(), minAmount + random.nextInt(maxAmount - minAmount)));
                }
            }
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<SimpleAddLootModifier> {

        @Override
        public SimpleAddLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] lootConditions) {
            Item itemToAdd = ForgeRegistries.ITEMS.getValue(new ResourceLocation(JSONUtils.getAsString(object, "item")));
            int maxStackCount = JSONUtils.getAsInt(object, "maxCount");
            int minStackCount = JSONUtils.getAsInt(object, "minCount");
            double chance = JSONUtils.getAsFloat(object, "chance");

            List<ResourceLocation> lootTables = new ArrayList<>();
            JsonArray jsonArray = JSONUtils.getAsJsonArray(object, "lootTables");

            for (JsonElement element : jsonArray) {
                ResourceLocation rl = ResourceLocation.tryParse(element.getAsString());

                if (rl != null) {
                    lootTables.add(rl);
                }
            }
            return new SimpleAddLootModifier(lootConditions, lootTables.toArray(new ResourceLocation[0]), () -> itemToAdd, chance, minStackCount, maxStackCount);
        }

        @Override
        public JsonObject write(SimpleAddLootModifier instance) {
            final JsonObject json = this.makeConditions(instance.conditions);
            json.addProperty("item", Objects.requireNonNull(instance.itemToAdd.get().getRegistryName()).toString());
            json.addProperty("maxCount", instance.maxAmount);
            json.addProperty("minCount", instance.minAmount);
            json.addProperty("chance", instance.chance);

            JsonArray lootTables = new JsonArray();

            for (ResourceLocation rl : instance.targetLootTables) {
                lootTables.add(ResourceLocation.CODEC.encodeStart(JsonOps.INSTANCE, rl)
                        .getOrThrow(false, Apocalypse.LOGGER::error));
            }
            json.add("lootTables", lootTables);

            return json;
        }
    }
}
