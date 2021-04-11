package com.toast.apocalypse.common.core;

import com.toast.apocalypse.api.ApocalypsePlugin;
import com.toast.apocalypse.api.IApocalypseApi;
import com.toast.apocalypse.api.IApocalypsePlugin;
import com.toast.apocalypse.api.IConfigHelper;
import com.toast.apocalypse.api.impl.ApocalypseAPI;
import com.toast.apocalypse.api.impl.ConfigHelper;
import com.toast.apocalypse.api.impl.RegistryHelper;
import com.toast.apocalypse.api.register.IRegistryHelper;
import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import com.toast.apocalypse.common.command.CommandRegister;
import com.toast.apocalypse.common.command.argument.ApocalypseArgumentTypes;
import com.toast.apocalypse.common.core.config.ApocalypseClientConfig;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.core.mod_event.EventRegister;
import com.toast.apocalypse.common.event.CapabilityAttachEvents;
import com.toast.apocalypse.common.event.EntityEvents;
import com.toast.apocalypse.common.network.PacketHandler;
import com.toast.apocalypse.common.register.ApocalypseEffects;
import com.toast.apocalypse.common.register.ApocalypseEntities;
import com.toast.apocalypse.common.register.ApocalypseItems;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Apocalypse.MODID)
public class Apocalypse {

    /** The mod's ID **/
    public static final String MODID = "apocalypse";

    /** A logger instance using the modid as prefix/identifier **/
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    /** The instance of our mod class */
    public static Apocalypse INSTANCE;

    /** Difficulty manager instance */
    private final WorldDifficultyManager difficultyManager = new WorldDifficultyManager();

    /** Registry helper instance */
    private final RegistryHelper registryHelper = new RegistryHelper();

    /** Config helper instance */
    private final ConfigHelper configHelper = new ConfigHelper();

    /** Api class instance */
    private final ApocalypseAPI api = new ApocalypseAPI();

    /** Packet handler instance */
    private final PacketHandler packetHandler = new PacketHandler();

    // Yay, static init
    static {
        EventRegister.init();
    }

    public Apocalypse() {
        INSTANCE = this;

        ApocalypseEntities.initTypes();

        this.packetHandler.registerMessages();

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::onCommonSetup);
        eventBus.addListener(this::onLoadComplete);
        eventBus.addListener(ApocalypseEntities::createEntityAttributes);
        eventBus.register(this.getConfigHelper());

        MinecraftForge.EVENT_BUS.register(new EntityEvents());
        MinecraftForge.EVENT_BUS.register(new CapabilityAttachEvents());
        MinecraftForge.EVENT_BUS.register(this.getDifficultyManager());
        MinecraftForge.EVENT_BUS.addListener(CommandRegister::registerCommands);

        ApocalypseItems.ITEMS.register(eventBus);
        ApocalypseEffects.EFFECTS.register(eventBus);
        ApocalypseEntities.ENTITIES.register(eventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ApocalypseCommonConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ApocalypseClientConfig.CLIENT_SPEC);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ApocalypseArgumentTypes.register();
            ApocalypseCapabilities.registerCapabilities();
            ApocalypseEntities.registerEntitySpawnPlacement();
            ApocalypseEntities.registerFullMoon(this.registryHelper);
        });
    }

    public void onLoadComplete(FMLLoadCompleteEvent event) {
        ModList.get().getAllScanData().forEach(scanData -> {
            scanData.getAnnotations().forEach(annotationData -> {

                // Look for classes annotated with @ApocalypsePlugin
                if (annotationData.getAnnotationType().getClassName().equals(ApocalypsePlugin.class.getName())) {
                    String modid = (String) annotationData.getAnnotationData().getOrDefault("modid", "");

                    if (ModList.get().isLoaded(modid)) {
                        try {
                            Class<?> pluginClass = Class.forName(annotationData.getMemberName());

                            if (IApocalypsePlugin.class.isAssignableFrom(pluginClass)) {
                                IApocalypsePlugin plugin = (IApocalypsePlugin) pluginClass.newInstance();
                                // This makes debugging a bit easier
                                this.registryHelper.setCurrentPluginId(plugin.getPluginId());
                                plugin.load(this.api);
                                LOGGER.info("Found Apocalypse plugin at {} with plugin ID: {}", annotationData.getMemberName(), plugin.getPluginId());
                            }
                        }
                        catch (Exception e) {
                            LOGGER.error("Failed to register Apocalypse plugin at {}! Damn dag nabit damnit!", annotationData.getMemberName());
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
        registryHelper.postSetup();
    }

    public static ResourceLocation resourceLoc(String path) {
        return new ResourceLocation(MODID, path);
    }

    public WorldDifficultyManager getDifficultyManager() {
        return this.difficultyManager;
    }

    public IRegistryHelper getRegistryHelper() {
        return this.registryHelper;
    }

    public ConfigHelper getConfigHelper() {
        return this.configHelper;
    }

    public IApocalypseApi getApi() {
        return this.api;
    }

    public PacketHandler getPacketHandler() {
        return this.packetHandler;
    }
}
