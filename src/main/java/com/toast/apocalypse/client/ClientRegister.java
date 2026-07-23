package com.toast.apocalypse.client;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.client.config.ClientConfig;
import com.toast.apocalypse.client.event.ClientEventListener;
import com.toast.apocalypse.client.event.KeyInputListener;
import com.toast.apocalypse.client.particle.LunarDespawnSmokeParticle;
import com.toast.apocalypse.client.renderer.DifficultyOverlayRenderHandler;
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
import com.toast.apocalypse.common.compat.ryaomic.RyoamicCompat;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import fathertoast.crust.api.config.client.ClientConfigUtil;
import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
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
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = Apocalypse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ClientRegister {
    
    // Client config
    public static final ClientConfig CLIENT_CONFIG = new ClientConfig(
            ConfigManager.getRequired( Apocalypse.MOD_ID ), "client_settings" );
    
    
    public static final IGuiOverlay DIFFICULTY_OVERLAY = ( forgeGui, guiGraphics, partialTick, screenWidth, screenHeight ) -> {
        if( forgeGui.getMinecraft().options.hideGui || Minecraft.getInstance().options.renderDebug )
            return;
        DifficultyOverlayRenderHandler.renderDifficulty( forgeGui, guiGraphics, screenWidth, screenHeight );
    };
    
    
    @SubscribeEvent
    public static void onClientSetup( FMLClientSetupEvent event ) {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener( ClientUtil::onAddLayer );
        
        MinecraftForge.EVENT_BUS.register( new ClientEventListener() );
        MinecraftForge.EVENT_BUS.register( new KeyInputListener() );
        
        // Init client configs
        CLIENT_CONFIG.SPEC.initialize();
        
        // Tell Forge to open the config editor when our mod's "Config" button is clicked in the Mods screen
        ClientConfigUtil.registerConfigButtonAsEditScreen();
        
        RyoamicCompat.init();
        
        event.enqueueWork( () -> {
            ItemModelProps.register();
            registerMenuScreens();
        } );
    }
    
    @SubscribeEvent
    public static void onGuiOverlayRegister( RegisterGuiOverlaysEvent event ) {
        event.registerAbove( VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id(), "difficulty_overlay", DIFFICULTY_OVERLAY );
    }
    
    @SubscribeEvent
    public static void registerParticles( RegisterParticleProvidersEvent event ) {
        event.registerSpriteSet( ApocalypseObjects.ParticleTypes.LUNAR_DESPAWN_SMOKE.get(), LunarDespawnSmokeParticle.Factory::new );
    }
    
    @SubscribeEvent
    public static void registerLayerDefs( EntityRenderersEvent.RegisterLayerDefinitions event ) {
        event.registerLayerDefinition( ApocalypseModelLayers.GHOST, GhostModel::createBodyLayer );
        event.registerLayerDefinition( ApocalypseModelLayers.DESTROYER, GhastModel::createBodyLayer );
        event.registerLayerDefinition( ApocalypseModelLayers.SEEKER, GhastModel::createBodyLayer );
        event.registerLayerDefinition( ApocalypseModelLayers.GRUMP, GhastModel::createBodyLayer );
        event.registerLayerDefinition( ApocalypseModelLayers.BREECHER, () -> CreeperModel.createBodyLayer( CubeDeformation.NONE ) );
        event.registerLayerDefinition( ApocalypseModelLayers.FEARWOLF, FearwolfModel::createBodyLayer );
        event.registerLayerDefinition( ApocalypseModelLayers.SHADEFIEND, ShadefiendModel::createBodyLayer );
        
        event.registerLayerDefinition( ApocalypseModelLayers.BUCKET_HELMET, BucketHelmetModel::createBodyLayer );
        event.registerLayerDefinition( ApocalypseModelLayers.GRUMP_BUCKET_HELMET, GrumpBucketHelmetModel::createBodyLayer );
    }
    
    @SubscribeEvent
    public static void registerRenderer( EntityRenderersEvent.RegisterRenderers event ) {
        event.registerEntityRenderer( ApocalypseEntities.GHOST.get(), GhostRenderer::new );
        event.registerEntityRenderer( ApocalypseEntities.DESTROYER.get(), DestroyerRenderer::new );
        event.registerEntityRenderer( ApocalypseEntities.SEEKER.get(), SeekerRenderer::new );
        event.registerEntityRenderer( ApocalypseEntities.GRUMP.get(), GrumpRenderer::new );
        event.registerEntityRenderer( ApocalypseEntities.BREECHER.get(), BreecherRenderer::new );
        event.registerEntityRenderer( ApocalypseEntities.FEARWOLF.get(), FearwolfRenderer::new );
        event.registerEntityRenderer( ApocalypseEntities.SHADEFIEND.get(), ShadefiendRenderer::new );
        
        event.registerEntityRenderer( ApocalypseEntities.MONSTER_FISH_HOOK.get(), MonsterHookRenderer::new );
        event.registerEntityRenderer( ApocalypseEntities.DESTROYER_FIREBALL.get(), ( context ) -> new ThrownItemRenderer<>( context, 3.0F, true ) );
        event.registerEntityRenderer( ApocalypseEntities.SEEKER_FIREBALL.get(), ( context ) -> new ThrownItemRenderer<>( context, 1.5F, true ) );
        
    }
    
    /** Registers Apocalypse's menu screens. */
    private static void registerMenuScreens() {
        registerMenu( ApocalypseObjects.MenuTypes.DYNAMIC_TRAP, DynamicTrapMenuScreen::new );
    }
    
    /**
     * Registers a menu screen by mapping a registered menu type to a menu screen factory.
     *
     * @param regObj        The menu type registry object.
     * @param screenFactory The screen factory to map to the specified menu type.
     */
    @SuppressWarnings( "SameParameterValue" )
    private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>>
    void registerMenu( RegistryObject<MenuType<?>> regObj, MenuScreens.ScreenConstructor<M, U> screenFactory ) {
        // noinspection unchecked
        MenuScreens.register( (MenuType<? extends M>) regObj.get(), screenFactory );
    }
}
