package com.toast.apocalypse.datagen.loot;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

public class ApocalypseBlockLootTableProvider extends BlockLootSubProvider {
    
    private final Set<Block> knownBlocks = new HashSet<>();
    
    protected ApocalypseBlockLootTableProvider( Set<Item> set, FeatureFlagSet flagSet ) {
        super( set, flagSet );
    }
    
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return knownBlocks;
    }
    
    @Override
    protected void add( Block block, LootTable.Builder table ) {
        super.add( block, table );
        this.knownBlocks.add( block );
    }
    
    @Override
    protected void generate() {
        dropSelf( ApocalypseObjects.Blocks.LUNAR_PHASE_SENSOR.get() );
        dropSelf( ApocalypseObjects.Blocks.MIDNIGHT_STEEL_BLOCK.get() );
        dropSelf( ApocalypseObjects.Blocks.DYNAMIC_TRAP.get() );
        
        add( ApocalypseObjects.Blocks.DEAD_GRASS.get(), noDrop() );
        add( ApocalypseObjects.Blocks.DEAD_PLANT.get(), noDrop() );
        
        dropOther( ApocalypseObjects.Blocks.WET_TORCH.get(), Blocks.TORCH );
        dropOther( ApocalypseObjects.Blocks.WET_WALL_TORCH.get(), Blocks.TORCH );
    }
}
