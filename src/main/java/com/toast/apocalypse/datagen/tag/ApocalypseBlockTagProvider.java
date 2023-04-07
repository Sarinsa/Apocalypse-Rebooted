package com.toast.apocalypse.datagen.tag;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseBlocks;
import com.toast.apocalypse.common.tag.ApocalypseBlockTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class ApocalypseBlockTagProvider extends BlockTagsProvider {

    public ApocalypseBlockTagProvider(DataGenerator dataGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, Apocalypse.MODID, existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags() {
        tag(BlockTags.BEACON_BASE_BLOCKS)
                .add(ApocalypseBlocks.MIDNIGHT_STEEL_BLOCK.get());

        tag(ApocalypseBlockTags.BREECHER_TARGETS)
                .addTags(BlockTags.BEDS, BlockTags.DOORS, Tags.Blocks.CHESTS, Tags.Blocks.BARRELS, Tags.Blocks.FENCE_GATES);
    }
}
