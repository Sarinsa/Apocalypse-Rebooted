package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BiomeEvents {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBiomeLoad(BiomeLoadingEvent event) {
        if (event.getName() == null)
            return;

        if (event.getCategory() != Biome.Category.THEEND && event.getCategory() != Biome.Category.NETHER)
            event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(
                    ApocalypseEntities.FEARWOLF.get(),
                    20,
                    1,
                    4
            ));
    }
}
