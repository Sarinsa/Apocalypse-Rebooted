package com.toast.apocalypse.common.core;

import com.toast.apocalypse.api.IApocalypseApi;
import com.toast.apocalypse.api.impl.ApocalypseApiImpl;
import com.toast.apocalypse.api.plugin.ApocalypsePlugin;
import com.toast.apocalypse.api.plugin.IApocalypsePlugin;
import com.toast.apocalypse.common.command.CommandRegister;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.config.ApocalypseServerConfig;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.mod_event.EventRegistry;
import com.toast.apocalypse.common.core.register.*;
import com.toast.apocalypse.common.event.CapabilityAttachListener;
import com.toast.apocalypse.common.event.GameEventListener;
import com.toast.apocalypse.common.network.PacketHandler;
import com.toast.apocalypse.common.triggers.ApocalypseTriggers;
import com.toast.apocalypse.common.util.VersionCheckHelper;
import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod( Apocalypse.MOD_ID )
public final class Apocalypse {
    
    /** The mod's ID. **/
    public static final String MOD_ID = IApocalypseApi.MOD_ID;
    /** The mod's display name. */
    public static final String MOD_NAME = "Apocalypse Rebooted";
    /** A logger instance with this mod's ID as its name. **/
    public static final Logger LOGGER = LogManager.getLogger( MOD_ID );
    /** The instance of the mod class. */
    public static Apocalypse INSTANCE;
    
    /** Difficulty manager instance. */
    private final PlayerDifficultyManager difficultyManager = new PlayerDifficultyManager();
    /** API instance. */
    private final ApocalypseApiImpl api = new ApocalypseApiImpl();
    /** Packet handler instance. */
    private final PacketHandler packetHandler = new PacketHandler();
    
    
    public Apocalypse( FMLJavaModLoadingContext context ) {
        INSTANCE = this;
        
        // Static init stuff
        EventRegistry.init();
        ApocalypseTriggers.init();
        
        ConfigManager.create( "Apocalypse Rebooted", Apocalypse.MOD_ID );
        
        final IEventBus eventBus = context.getModEventBus();
        
        // Register mod event listeners
        eventBus.addListener( ApocalypseTrapTypes::onRegistryCreate );
        eventBus.addListener( ApocalypseEntities::createEntityAttributes );
        eventBus.addListener( ApocalypseEntities::registerEntitySpawnPlacement );
        eventBus.addListener( ApocalypseItems::onCreativeTabPopulate );
        eventBus.addListener( this::onCommonSetup );
        eventBus.addListener( this::onLoadComplete );
        
        // Register game event listeners
        MinecraftForge.EVENT_BUS.register( new GameEventListener() );
        MinecraftForge.EVENT_BUS.register( new CapabilityAttachListener() );
        MinecraftForge.EVENT_BUS.register( getDifficultyManager() );
        MinecraftForge.EVENT_BUS.addListener( CommandRegister::registerCommands );
        
        // Register game objects
        ApocalypseBlocks.register( eventBus );
        ApocalypseItems.register( eventBus );
        ApocalypseSounds.register( eventBus );
        ApocalypseMobEffects.register( eventBus );
        ApocalypseMenus.register( eventBus );
        ApocalypseEntities.register( eventBus );
        ApocalypseParticles.register( eventBus );
        ApocalypseLootMods.register( eventBus );
        ApocalypseTrapTypes.register( eventBus );
        ApocalypseRecipeTypes.register( eventBus );
        ApocalypseRecipeSerializers.register( eventBus );
        ApocalypseBlockEntities.register( eventBus );
        ApocalypseArgumentTypes.register( eventBus );
        
        // Missing mapping listeners
        MinecraftForge.EVENT_BUS.addListener( ApocalypseBlocks::onMissingMappings );
        MinecraftForge.EVENT_BUS.addListener( ApocalypseItems::onMissingMappings );
        
        // Crust does not support per-world / server configs, so for now we use Forge's
        context.registerConfig( ModConfig.Type.SERVER, ApocalypseServerConfig.SERVER_SPEC );
    }
    
    /** Called when the game enters the {@link net.minecraftforge.fml.ModLoadingStage#COMMON_SETUP} loading stage. */
    public void onCommonSetup( FMLCommonSetupEvent event ) {
        packetHandler.registerMessages();
        event.enqueueWork( ApocalypseConfig::initialize );
    }
    
    /** Called when the game enters the {@link net.minecraftforge.fml.ModLoadingStage#COMPLETE} loading stage. */
    public void onLoadComplete( FMLLoadCompleteEvent event ) {
        event.enqueueWork( () -> {
            processPlugins();
            VersionCheckHelper.setUpdateMessage();
        } );
    }
    
    /** Looks for Apocalypse plugins and attempts to load them. */
    private void processPlugins() {
        // Load mod plugins
        ModList.get().getAllScanData().forEach( scanData -> {
            scanData.getAnnotations().forEach( annotationData -> {
                
                // Look for classes annotated with @ApocalypsePlugin
                if( annotationData.annotationType().getClassName().equals( ApocalypsePlugin.class.getName() ) ) {
                    String modId = (String) annotationData.annotationData().getOrDefault( "modId", "" );
                    
                    if( ModList.get().isLoaded( modId ) || modId.isEmpty() ) {
                        try {
                            Class<?> pluginClass = Class.forName( annotationData.memberName() );
                            
                            if( IApocalypsePlugin.class.isAssignableFrom( pluginClass ) ) {
                                IApocalypsePlugin plugin = (IApocalypsePlugin) pluginClass.getConstructor().newInstance();
                                plugin.load( getApi() );
                                LOGGER.info( "Found Apocalypse plugin at {} with plugin ID: {}", annotationData.memberName(), plugin.getPluginId() );
                            }
                        }
                        catch( Exception e ) {
                            LOGGER.error( "Failed to load Apocalypse plugin at {}! Damn dag nabit damnit!", annotationData.memberName() );
                            // noinspection CallToPrintStackTrace
                            e.printStackTrace();
                        }
                    }
                }
            } );
        } );
    }
    
    
    /** @return A resource location of the specified path, under this mod's namespace. */
    public static ResourceLocation rl( String path ) {
        return ResourceLocation.fromNamespaceAndPath( MOD_ID, path );
    }
    
    /** @return The difficulty manager instance. */
    public PlayerDifficultyManager getDifficultyManager() {
        return difficultyManager;
    }
    
    /** @return The Apocalypse API instance. */
    public ApocalypseApiImpl getApi() {
        return api;
    }
}
