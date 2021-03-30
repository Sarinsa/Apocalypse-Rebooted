package com.toast.apocalypse.common.core;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Apocalypse.MODID)
public class Apocalypse {

    public static final String MODID = "apocalypse";


    public Apocalypse() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();


    }
}
