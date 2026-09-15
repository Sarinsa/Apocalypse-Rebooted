package com.toast.apocalypse.common.loot_modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.ListCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.toast.apocalypse.api.lib.ApocalypseObjects;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

public class SimpleAddLootModifier extends LootModifier {
    
    private static final ListCodec<ResourceLocation> RL_LIST_CODEC = new ListCodec<>( ResourceLocation.CODEC );
    
    public final Item itemToAdd;
    public final double chance;
    public final int maxStackCount;
    public final int minStackCount;
    public final List<ResourceLocation> lootTables;
    
    
    public static final Supplier<Codec<SimpleAddLootModifier>> CODEC = () -> RecordCodecBuilder.create( inst -> LootModifier.codecStart( inst )
            .and( inst.group(
                    ForgeRegistries.ITEMS.getCodec()
                            .fieldOf( "item" ).forGetter( m -> m.itemToAdd ),
                    Codec.DOUBLE.fieldOf( "chance" ).forGetter( m -> m.chance ),
                    Codec.INT.fieldOf( "minCount" ).forGetter( m -> m.minStackCount ),
                    Codec.INT.fieldOf( "maxCount" ).forGetter( m -> m.maxStackCount ),
                    RL_LIST_CODEC.fieldOf( "lootTable" ).forGetter( m -> m.lootTables )
            ) )
            .apply( inst, SimpleAddLootModifier::new )
    );
    
    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    public SimpleAddLootModifier( LootItemCondition[] conditionsIn, Item itemToAdd, double chance, int minStackCount, int maxStackCount ) {
        this( conditionsIn, itemToAdd, chance, minStackCount, maxStackCount, List.of() );
    }
    
    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    public SimpleAddLootModifier( LootItemCondition[] conditionsIn, Item itemToAdd, double chance, int minStackCount, int maxStackCount, List<ResourceLocation> lootTables ) {
        super( conditionsIn );
        this.itemToAdd = itemToAdd;
        this.chance = chance;
        this.minStackCount = minStackCount;
        this.maxStackCount = maxStackCount;
        this.lootTables = lootTables;
    }
    
    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply( ObjectArrayList<ItemStack> generatedLoot, LootContext context ) {
        if( lootTables.isEmpty() || lootTables.contains( context.getQueriedLootTableId() ) ) {
            RandomSource random = context.getRandom();
            
            if( chance >= 1.0 || random.nextDouble() < chance ) {
                int delta = maxStackCount - minStackCount;
                ItemStack stack = new ItemStack( itemToAdd, delta <= 0 ? minStackCount :
                        minStackCount + random.nextInt( delta + 1 ) );
                generatedLoot.add( stack );
            }
        }
        return generatedLoot;
    }
    
    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return ApocalypseObjects.LootModSerializers.SIMPLE_ADD_LOOT_MOD.get();
    }
}