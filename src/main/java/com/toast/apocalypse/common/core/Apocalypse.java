package com.toast.apocalypse.common.core;

import com.toast.apocalypse.common.event.EntityEvents;
import com.toast.apocalypse.common.register.ApocalypseEffects;
import com.toast.apocalypse.common.register.ApocalypseEntities;
import com.toast.apocalypse.common.register.ApocalypseItems;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Apocalypse.MODID)
public class Apocalypse {

    /** The mod's ID **/
    public static final String MODID = "apocalypse";
    /** A logger instance using the modid as prefix/identifier **/
    public static final Logger LOGGER = LogManager.getLogger(MODID);


    public Apocalypse() {
        ApocalypseEntities.initTypes();

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::onCommonSetup);
        eventBus.addListener(ApocalypseEntities::createEntityAttributes);

        MinecraftForge.EVENT_BUS.register(new EntityEvents());

        ApocalypseItems.ITEMS.register(eventBus);
        ApocalypseEffects.EFFECTS.register(eventBus);
        ApocalypseEntities.ENTITIES.register(eventBus);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

        });
    }

    public static ResourceLocation resourceLoc(String path) {
        return new ResourceLocation(MODID, path);
    }
}
