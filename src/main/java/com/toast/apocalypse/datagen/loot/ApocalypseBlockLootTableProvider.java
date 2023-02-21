package com.toast.apocalypse.datagen.loot;

import com.toast.apocalypse.common.core.register.ApocalypseBlocks;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

public class ApocalypseBlockLootTableProvider extends BlockLoot {

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
        this.dropSelf(ApocalypseBlocks.MIDNIGHT_STEEL_BLOCK.get());
    }
}
