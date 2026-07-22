package com.toast.apocalypse.datagen.tag;

import com.toast.apocalypse.api.util.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.tag.ApocalypseItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ApocalypseItemTagProvider extends ItemTagsProvider {
    
    public ApocalypseItemTagProvider( DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper ) {
        super( dataGenerator.getPackOutput(), lookupProvider, blockTagsProvider, Apocalypse.MOD_ID, existingFileHelper );
    }
    
    @Override
    protected void addTags( HolderLookup.Provider lookupProvider ) {
        tag( ApocalypseItemTags.COOKIES )
                .add( Items.COOKIE );
        
        tag( Tags.Items.INGOTS )
                .add( ApocalypseObjects.Items.MIDNIGHT_STEEL_INGOT.get() );
        
        tag( ItemTags.BEACON_PAYMENT_ITEMS )
                .add( ApocalypseObjects.Items.MIDNIGHT_STEEL_INGOT.get() );
        
        tag( Tags.Items.ARMORS_HELMETS )
                .add( ApocalypseObjects.Items.BUCKET_HELM.get() )
                .add( ApocalypseObjects.Items.MIDNIGHT_STEEL_HELMET.get() );
        
        tag( Tags.Items.ARMORS_CHESTPLATES )
                .add( ApocalypseObjects.Items.MIDNIGHT_STEEL_CHESTPLTAE.get() );
        
        tag( Tags.Items.ARMORS_LEGGINGS )
                .add( ApocalypseObjects.Items.MIDNIGHT_STEEL_LEGGINGS.get() );
        
        tag( Tags.Items.ARMORS_BOOTS )
                .add( ApocalypseObjects.Items.MIDNIGHT_STEEL_BOOTS.get() );
    }
}
