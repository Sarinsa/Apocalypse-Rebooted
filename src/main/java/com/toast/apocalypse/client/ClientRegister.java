package com.toast.apocalypse.client;

import com.toast.apocalypse.client.config.ClientConfig;
import com.toast.apocalypse.client.event.ClientEvents;
import com.toast.apocalypse.client.renderer.DifficultyOverlayRenderHandler;
import com.toast.apocalypse.client.event.KeyInputListener;
import com.toast.apocalypse.client.mobwiki.MobEntries;
import com.toast.apocalypse.client.particle.LunarDespawnSmokeParticle;
import com.toast.apocalypse.client.renderer.entity.living.breecher.BreecherRenderer;
import com.toast.apocalypse.client.renderer.entity.living.destroyer.DestroyerRenderer;
import com.toast.apocalypse.client.renderer.entity.living.fearwolf.FearwolfModel;
import com.toast.apocalypse.client.renderer.entity.living.fearwolf.FearwolfRenderer;
import com.toast.apocalypse.client.renderer.entity.living.ghost.GhostModel;
import com.toast.apocalypse.client.renderer.entity.living.ghost.GhostRenderer;
import com.toast.apocalypse.client.renderer.entity.living.grump.GrumpRenderer;
import com.toast.apocalypse.client.renderer.entity.living.seeker.SeekerRenderer;
import com.toast.apocalypse.client.renderer.entity.living.shadefiend.ShadefiendModel;
import com.toast.apocalypse.client.renderer.entity.living.shadefiend.ShadefiendRenderer;
import com.toast.apocalypse.client.renderer.entity.projectile.monsterhook.MonsterHookRenderer;
import com.toast.apocalypse.client.renderer.model.armor.BucketHelmetModel;
import com.toast.apocalypse.client.renderer.model.armor.GrumpBucketHelmetModel;
import com.toast.apocalypse.client.screen.DynamicTrapMenuScreen;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.core.register.ApocalypseMenus;
import com.toast.apocalypse.common.core.register.ApocalypseParticles;
import com.toast.apocalypse.common.entity.living.Shadefiend;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.client.ClientConfigUtil;
import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.compress.archivers.sevenz.CLI;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegister {

    // Client config
    public static final ClientConfig CLIENT_CONFIG = new ClientConfig(
            ConfigManager.getRequired(Apocalypse.MODID), "client_settings");


    public static final IGuiOverlay DIFFICULTY_OVERLAY = (forgeGui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        if (!forgeGui.getMinecraft().options.hideGui) {
            DifficultyOverlayRenderHandler.renderDifficulty(forgeGui, guiGraphics, partialTick, screenWidth, screenHeight);
        }
    };


    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        MinecraftForge.EVENT_BUS.register(new KeyInputListener());

        // Config loading
        CLIENT_CONFIG.SPEC.initialize();

        // Tell Forge to open the config editor when our mod's "Config" button is clicked in the Mods screen
        ClientConfigUtil.registerConfigButtonAsEditScreen();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(ClientUtil::onAddLayer);

        registerMenuScreens();
        MobEntries.init();

        event.enqueueWork(ItemModelProps::register);
    }

    @SubscribeEvent
    public static void onGuiOverlayRegister(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id(), "difficulty_overlay", DIFFICULTY_OVERLAY);
    }

    private static void registerMenuScreens() {
        MenuScreens.register(ApocalypseMenus.DYNAMIC_TRAP.get(), DynamicTrapMenuScreen::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ApocalypseParticles.LUNAR_DESPAWN_SMOKE.get(), LunarDespawnSmokeParticle.Factory::new);
    }

    @SubscribeEvent
    public static void registerLayerDefs(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ApocalypseModelLayers.GHOST, GhostModel::createBodyLayer);
        event.registerLayerDefinition(ApocalypseModelLayers.DESTROYER, GhastModel::createBodyLayer);
        event.registerLayerDefinition(ApocalypseModelLayers.SEEKER, GhastModel::createBodyLayer);
        event.registerLayerDefinition(ApocalypseModelLayers.GRUMP, GhastModel::createBodyLayer);
        event.registerLayerDefinition(ApocalypseModelLayers.BREECHER, () -> CreeperModel.createBodyLayer(CubeDeformation.NONE));
        event.registerLayerDefinition(ApocalypseModelLayers.FEARWOLF, FearwolfModel::createBodyLayer);
        event.registerLayerDefinition(ApocalypseModelLayers.SHADEFIEND, ShadefiendModel::createBodyLayer);

        event.registerLayerDefinition(ApocalypseModelLayers.BUCKET_HELMET, BucketHelmetModel::createBodyLayer);
        event.registerLayerDefinition(ApocalypseModelLayers.GRUMP_BUCKET_HELMET, GrumpBucketHelmetModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ApocalypseEntities.GHOST.get(), GhostRenderer::new);
        event.registerEntityRenderer(ApocalypseEntities.DESTROYER.get(), DestroyerRenderer::new);
        event.registerEntityRenderer(ApocalypseEntities.SEEKER.get(), SeekerRenderer::new);
        event.registerEntityRenderer(ApocalypseEntities.GRUMP.get(), GrumpRenderer::new);
        event.registerEntityRenderer(ApocalypseEntities.BREECHER.get(), BreecherRenderer::new);
        event.registerEntityRenderer(ApocalypseEntities.FEARWOLF.get(), FearwolfRenderer::new);
        event.registerEntityRenderer(ApocalypseEntities.SHADEFIEND.get(), ShadefiendRenderer::new);

        event.registerEntityRenderer(ApocalypseEntities.MONSTER_FISH_HOOK.get(), MonsterHookRenderer::new);
        event.registerEntityRenderer(ApocalypseEntities.DESTROYER_FIREBALL.get(), (context) -> new ThrownItemRenderer<>(context, 3.0F, true));
        event.registerEntityRenderer(ApocalypseEntities.SEEKER_FIREBALL.get(), (context) -> new ThrownItemRenderer<>(context, 1.5F, true));

    }
}
