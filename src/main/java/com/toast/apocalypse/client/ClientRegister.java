package com.toast.apocalypse.client;

import com.toast.apocalypse.client.event.ClientEvents;
import com.toast.apocalypse.client.renderers.entity.NoRender;
import com.toast.apocalypse.client.renderers.entity.breecher.BreecherRenderer;
import com.toast.apocalypse.client.renderers.entity.destroyer.DestroyerRenderer;
import com.toast.apocalypse.client.renderers.entity.ghost.GhostRenderer;
import com.toast.apocalypse.client.renderers.entity.grump.GrumpRenderer;
import com.toast.apocalypse.client.renderers.entity.seeker.SeekerRenderer;
import com.toast.apocalypse.client.renderers.model.armor.BucketHelmetModel;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.item.BucketHelmetItem;
import com.toast.apocalypse.common.register.ApocalypseEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.system.CallbackI;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegister {

    public static final BucketHelmetModel BUCKET_HELMET_MODEL = new BucketHelmetModel();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEvents(event.getMinecraftSupplier().get()));
        registerEntityRenderers(event.getMinecraftSupplier());
    }

    private static void registerEntityRenderers(Supplier<Minecraft> minecraftSupplier) {
        RenderingRegistry.registerEntityRenderingHandler(ApocalypseEntities.GHOST.get(), GhostRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ApocalypseEntities.DESTROYER.get(), DestroyerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ApocalypseEntities.SEEKER.get(), SeekerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ApocalypseEntities.GRUMP.get(), GrumpRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ApocalypseEntities.BREECHER.get(), BreecherRenderer::new);

        registerSpriteRenderer(ApocalypseEntities.DESTROYER_FIREBALL.get(), minecraftSupplier, 3.0F, true);
        registerSpriteRenderer(ApocalypseEntities.SEEKER_FIREBALL.get(), minecraftSupplier, 1.5F, true);
        registerSpriteRenderer(ApocalypseEntities.MONSTER_FISH_HOOK.get(), minecraftSupplier);
    }

    private static <T extends Entity & IRendersAsItem> void registerSpriteRenderer(EntityType<T> entityType, Supplier<Minecraft> minecraftSupplier) {
        ItemRenderer itemRenderer = minecraftSupplier.get().getItemRenderer();
        RenderingRegistry.registerEntityRenderingHandler(entityType, (rendererManager) -> new SpriteRenderer<T>(rendererManager, itemRenderer));
    }

    private static <T extends Entity & IRendersAsItem> void registerSpriteRenderer(EntityType<T> entityType, Supplier<Minecraft> minecraftSupplier, float scale, boolean fullBright) {
        ItemRenderer itemRenderer = minecraftSupplier.get().getItemRenderer();
        RenderingRegistry.registerEntityRenderingHandler(entityType, (renderManager) -> new SpriteRenderer<>(renderManager, itemRenderer, scale, fullBright));
    }
}
