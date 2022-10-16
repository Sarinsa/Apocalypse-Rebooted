package com.toast.apocalypse.datagen;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseBlocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class ApocalypseBlockTagProvider extends BlockTagsProvider {

    public ApocalypseBlockTagProvider(DataGenerator dataGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, Apocalypse.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(BlockTags.BEACON_BASE_BLOCKS)
                .add(ApocalypseBlocks.LUNARIUM_BLOCK.get());
    }
}
