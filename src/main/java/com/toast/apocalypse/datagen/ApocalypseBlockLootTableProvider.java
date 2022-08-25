package com.toast.apocalypse.datagen;

import com.toast.apocalypse.common.core.register.ApocalypseBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

public class ApocalypseBlockLootTableProvider extends BlockLootTables {

    private final Set<Block> knownBlocks = new HashSet<>();

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return this.knownBlocks;
    }

    @Override
    protected void add(Block block, LootTable.Builder table) {
        super.add(block, table);
        this.knownBlocks.add(block);
    }

    @Override
    protected void addTables() {
        this.dropSelf(ApocalypseBlocks.LUNAR_PHASE_SENSOR.get());
        this.dropSelf(ApocalypseBlocks.LUNARIUM_BLOCK.get());
    }
}
