package com.toast.apocalypse.common.loot_modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.ListCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.toast.apocalypse.common.core.register.ApocalypseLootMods;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class SimpleAddLootModifier extends LootModifier {

    private static final ListCodec<ResourceLocation> RL_LIST_CODEC = new ListCodec<>(ResourceLocation.CODEC);

    public final Item itemToAdd;
    public final double chance;
    public final int maxStackCount;
    public final int minStackCount;
    public final List<ResourceLocation> lootTables;


    public static final Supplier<Codec<SimpleAddLootModifier>> CODEC = () -> RecordCodecBuilder.create(inst -> LootModifier.codecStart(inst)
            .and(inst.group(
                            ForgeRegistries.ITEMS.getCodec()
                                    .fieldOf("item")
                                    .forGetter(m -> m.itemToAdd),
                            Codec.DOUBLE.fieldOf("chance")
                                    .forGetter(m -> m.chance),
                            Codec.INT.fieldOf("maxCount")
                                    .forGetter(m -> m.maxStackCount),
                            Codec.INT.fieldOf("minCount")
                                    .forGetter(m -> m.minStackCount),
                            RL_LIST_CODEC
                                    .fieldOf("lootTable")
                                    .forGetter(m -> m.lootTables)
                    )
            )
            .apply(inst, SimpleAddLootModifier::new)
    );

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    public SimpleAddLootModifier(LootItemCondition[] conditionsIn, Item itemToAdd, double chance, int maxStackCount, int minStackCount, List<ResourceLocation> lootTables) {
        super(conditionsIn);
        this.itemToAdd = itemToAdd;
        this.chance = chance;
        this.maxStackCount = maxStackCount;
        this.minStackCount = minStackCount;
        this.lootTables = lootTables;
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (lootTables.contains(context.getQueriedLootTableId())) {
            Random random = new Random();

            if (random.nextDouble() <= chance) {
                ItemStack stack = new ItemStack(this.itemToAdd, random.nextInt(this.maxStackCount + 1));
                generatedLoot.add(stack);
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return ApocalypseLootMods.SIMPLE_ADD_LOOT_MOD.get();
    }
}
