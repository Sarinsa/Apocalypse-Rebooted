package com.toast.apocalypse.datagen;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGatherer {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator dataGenerator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        if (event.includeServer()) {
            dataGenerator.addProvider(new ApocalypseRecipeProvider(dataGenerator));
            dataGenerator.addProvider(new ApocalypseLootTableProvider(dataGenerator));
            dataGenerator.addProvider(new ApocalypseAdvancementProvider(dataGenerator, fileHelper));
            BlockTagsProvider blockTagProvider = new ApocalypseBlockTagProvider(dataGenerator, fileHelper);
            dataGenerator.addProvider(blockTagProvider);
            dataGenerator.addProvider(new ApocalypseItemTagProvider(dataGenerator, blockTagProvider, fileHelper));
            dataGenerator.addProvider(new ApocalypseLootModProvider(dataGenerator));
        }
    }
}
