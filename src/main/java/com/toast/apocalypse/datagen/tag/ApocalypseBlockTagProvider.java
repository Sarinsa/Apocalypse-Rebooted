package com.toast.apocalypse.datagen.tag;

import com.toast.apocalypse.api.util.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ApocalypseBlockTagProvider extends BlockTagsProvider {
    
    public ApocalypseBlockTagProvider( DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                       @Nullable ExistingFileHelper existingFileHelper ) {
        super( dataGenerator.getPackOutput(), lookupProvider, Apocalypse.MOD_ID, existingFileHelper );
    }
    
    @Override
    public void addTags( HolderLookup.Provider lookupProvider ) {
        tag( BlockTags.BEACON_BASE_BLOCKS )
                .add( ApocalypseObjects.Blocks.MIDNIGHT_STEEL_BLOCK.get() );
        
        tag( BlockTags.MINEABLE_WITH_PICKAXE )
                .add( ApocalypseObjects.Blocks.LUNAR_PHASE_SENSOR.get(),
                        ApocalypseObjects.Blocks.MIDNIGHT_STEEL_BLOCK.get(),
                        ApocalypseObjects.Blocks.DYNAMIC_TRAP.get() );
        
        tag( BlockTags.REPLACEABLE )
                .add( ApocalypseObjects.Blocks.DEAD_GRASS.get(),
                        ApocalypseObjects.Blocks.DEAD_PLANT.get() );
    }
}
