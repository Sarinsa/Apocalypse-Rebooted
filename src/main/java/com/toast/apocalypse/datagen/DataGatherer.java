package com.toast.apocalypse.datagen;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGatherer {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator dataGenerator = event.getGenerator();

        if (event.includeServer()) {
            dataGenerator.addProvider(new ApocalypseRecipeProvider(dataGenerator));
            dataGenerator.addProvider(new ApocalypseLootTableProvider(dataGenerator));
            dataGenerator.addProvider(new ApocalypseAdvancementProvider(dataGenerator, new ApocalypseAdvancements()));
        }
    }
}
