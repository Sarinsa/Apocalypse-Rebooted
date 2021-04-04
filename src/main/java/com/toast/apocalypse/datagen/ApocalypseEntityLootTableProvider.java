package com.toast.apocalypse.datagen;

import com.toast.apocalypse.common.register.ApocalypseEntities;
import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.functions.LootingEnchantBonus;
import net.minecraft.loot.functions.SetCount;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ApocalypseEntityLootTableProvider extends EntityLootTables {

    private final Set<EntityType<?>> knownEntities = new HashSet<>();

    @Override
    protected Iterable<EntityType<?>> getKnownEntities() {
        return this.knownEntities;
    }

    @Override
    protected void add(EntityType<?> type, LootTable.Builder table) {
        super.add(type, table);
        this.knownEntities.add(type);
    }

    @Override
    protected void addTables() {
        this.add(ApocalypseEntities.GHOST.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantRange.exactly(1))
                        .add(ItemLootEntry.lootTableItem(Items.EXPERIENCE_BOTTLE)
                                .apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F)))
                                .apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 2.0F)))))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantRange.exactly(1))
                        .add(ItemLootEntry.lootTableItem(Items.SLIME_BALL)
                                .apply(SetCount.setCount(RandomValueRange.between(0.0F, 2.0F)))
                                .apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 2.0F))))));

        this.add(ApocalypseEntities.GRUMP.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantRange.exactly(1))
                        .add(ItemLootEntry.lootTableItem(Items.COOKIE)
                                .apply(SetCount.setCount(RandomValueRange.between(0.0F, 3.0F)))
                                .apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 2.0F))))
                        .add(ItemLootEntry.lootTableItem(Items.EXPERIENCE_BOTTLE)
                                .apply(SetCount.setCount(RandomValueRange.between(0.0F, 0.2F)))
                                .apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 2.0F))))));
    }
}
