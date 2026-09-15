package com.toast.apocalypse.datagen.tag;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.misc.ApocalypseDamageSources;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ApocalypseDamageTagProvider extends DamageTypeTagsProvider {
    
    public ApocalypseDamageTagProvider( DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper fileHelper ) {
        super( dataGenerator.getPackOutput(), provider, Apocalypse.MOD_ID, fileHelper );
    }
    
    @Override
    protected void addTags( HolderLookup.Provider provider ) {
        tag( DamageTypeTags.BYPASSES_ARMOR )
                .addOptional( ApocalypseDamageSources.ACID_RAIN.location() )
                .addOptional( ApocalypseDamageSources.LIGHT_INTOLERANCE.location() );
        tag( DamageTypeTags.BYPASSES_SHIELD )
                .addOptional( ApocalypseDamageSources.ACID_RAIN.location() );
        tag( DamageTypeTags.BYPASSES_EFFECTS )
                .addOptional( ApocalypseDamageSources.LIGHT_INTOLERANCE.location() );
    }
}