package com.toast.apocalypse.client;

import com.toast.apocalypse.client.renderers.entity.ghost.GhostRenderer;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.register.ApocalypseEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegister {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEvents(event.getMinecraftSupplier().get()));

        registerEntityRenderers();
    }

    private static void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(ApocalypseEntities.GHOST.get(), GhostRenderer::new);
    }
}
