package com.toast.apocalypse.datagen;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.datagen.advancement.ApocalypseAdvancementProvider;
import com.toast.apocalypse.datagen.loot.ApocalypseLootModProvider;
import com.toast.apocalypse.datagen.loot.ApocalypseLootTableProvider;
import com.toast.apocalypse.datagen.model.ApocalypseBlockModelProvider;
import com.toast.apocalypse.datagen.model.ApocalypseItemModelProvider;
import com.toast.apocalypse.datagen.recipe.ApocalypseRecipeProvider;
import com.toast.apocalypse.datagen.tag.ApocalypseBlockTagProvider;
import com.toast.apocalypse.datagen.tag.ApocalypseDamageTagProvider;
import com.toast.apocalypse.datagen.tag.ApocalypseEntityTagProvider;
import com.toast.apocalypse.datagen.tag.ApocalypseItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber( modid = Apocalypse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class DataGatherer {
    
    @SubscribeEvent
    public static void onGatherData( GatherDataEvent event ) {
        DataGenerator dataGen = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        
        if( event.includeServer() ) {
            dataGen.addProvider( true, new ApocalypseRecipeProvider( dataGen ) );
            dataGen.addProvider( true, new ApocalypseLootTableProvider( dataGen ) );
            dataGen.addProvider( true, new ApocalypseAdvancementProvider( dataGen, lookupProvider, fileHelper ) );
            BlockTagsProvider blockTagProvider = new ApocalypseBlockTagProvider( dataGen, lookupProvider, fileHelper );
            dataGen.addProvider( true, blockTagProvider );
            dataGen.addProvider( true, new ApocalypseItemTagProvider( dataGen, lookupProvider, blockTagProvider.contentsGetter(), fileHelper ) );
            dataGen.addProvider( true, new ApocalypseEntityTagProvider( dataGen, lookupProvider, fileHelper ) );
            dataGen.addProvider( true, new ApocalypseDamageTagProvider( dataGen, lookupProvider, fileHelper ) );
            dataGen.addProvider( true, new ApocalypseLootModProvider( dataGen ) );
        }
        if( event.includeClient() ) {
            dataGen.addProvider( true, new ApocalypseBlockModelProvider( dataGen, fileHelper ) );
            dataGen.addProvider( true, new ApocalypseItemModelProvider( dataGen, fileHelper ) );
        }
    }
}
