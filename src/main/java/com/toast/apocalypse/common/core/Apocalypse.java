package com.toast.apocalypse.common.core;

import com.toast.apocalypse.common.register.ApocalypseEntities;
import com.toast.apocalypse.common.register.ApocalypseItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Apocalypse.MODID)
public class Apocalypse {

    public static final String MODID = "apocalypse";
    // A logger instance using the modid as prefix/identifier
    public static final Logger LOGGER = LogManager.getLogger(MODID);


    public Apocalypse() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::onCommonSetup);

        ApocalypseItems.ITEMS.register(eventBus);
        ApocalypseEntities.ENTITIES.register(eventBus);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {

    }
}
