package com.toast.apocalypse.client;

import com.toast.apocalypse.client.event.ClientEvents;
import com.toast.apocalypse.client.event.KeyInputListener;
import com.toast.apocalypse.client.mobwiki.MobEntries;
import com.toast.apocalypse.client.particle.LunarDespawnSmokeParticle;
import com.toast.apocalypse.client.renderer.entity.living.breecher.BreecherRenderer;
import com.toast.apocalypse.client.renderer.entity.living.destroyer.DestroyerRenderer;
import com.toast.apocalypse.client.renderer.entity.living.fearwolf.FearwolfRenderer;
import com.toast.apocalypse.client.renderer.entity.living.ghost.GhostRenderer;
import com.toast.apocalypse.client.renderer.entity.living.grump.GrumpRenderer;
import com.toast.apocalypse.client.renderer.entity.living.seeker.SeekerRenderer;
import com.toast.apocalypse.client.renderer.entity.projectile.monsterhook.MonsterHookRenderer;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.core.register.ApocalypseParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegister {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        Minecraft mc = event.getMinecraftSupplier().get();

        MinecraftForge.EVENT_BUS.register(new ClientEvents(mc));
        MinecraftForge.EVENT_BUS.register(new KeyInputListener(mc));

        registerEntityRenderers(event.getMinecraftSupplier());
        ApocalypseKeyBindings.registerKeyBindings();
        ItemModelProps.register();

        MobEntries.init();
    }

    @SubscribeEvent
    public static void registerParticles(ParticleFactoryRegisterEvent event) {
        ParticleManager particleManager = Minecraft.getInstance().particleEngine;

        particleManager.register(ApocalypseParticles.LUNAR_DESPAWN_SMOKE.get(), LunarDespawnSmokeParticle.Factory::new);
    }

    private static void registerEntityRenderers(Supplier<Minecraft> minecraftSupplier) {
        RenderingRegistry.registerEntityRenderingHandler(ApocalypseEntities.GHOST.get(), GhostRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ApocalypseEntities.DESTROYER.get(), DestroyerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ApocalypseEntities.SEEKER.get(), SeekerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ApocalypseEntities.GRUMP.get(), GrumpRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ApocalypseEntities.BREECHER.get(), BreecherRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ApocalypseEntities.MONSTER_FISH_HOOK.get(), MonsterHookRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ApocalypseEntities.FEARWOLF.get(), FearwolfRenderer::new);

        registerSpriteRenderer(ApocalypseEntities.DESTROYER_FIREBALL.get(), minecraftSupplier, 3.0F, true);
        registerSpriteRenderer(ApocalypseEntities.SEEKER_FIREBALL.get(), minecraftSupplier, 1.5F, true);
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
