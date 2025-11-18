package com.toast.apocalypse.datagen.tag;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ApocalypseDamageTagProvider extends DamageTypeTagsProvider {
    
    public ApocalypseDamageTagProvider( DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper fileHelper ) {
        super( dataGenerator.getPackOutput(), provider, Apocalypse.MODID, fileHelper );
    }
    
    @Override
    protected void addTags( HolderLookup.Provider provider ) {
    
    }
}
