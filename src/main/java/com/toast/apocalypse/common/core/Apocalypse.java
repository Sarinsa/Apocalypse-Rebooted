package com.toast.apocalypse.common.core;

import com.toast.apocalypse.api.impl.ApocalypseAPI;
import com.toast.apocalypse.api.impl.RegistryHelper;
import com.toast.apocalypse.api.plugin.ApocalypsePlugin;
import com.toast.apocalypse.api.plugin.IApocalypseApi;
import com.toast.apocalypse.api.plugin.IApocalypsePlugin;
import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import com.toast.apocalypse.common.command.CommandRegister;
import com.toast.apocalypse.common.command.argument.ApocalypseArgumentTypes;
import com.toast.apocalypse.common.compat.top.TOPEntityInfoProvider;
import com.toast.apocalypse.common.core.config.ApocalypseClientConfig;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.core.config.ApocalypseServerConfig;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.core.mod_event.EventRegistry;
import com.toast.apocalypse.common.event.CapabilityAttachEvents;
import com.toast.apocalypse.common.event.EntityEvents;
import com.toast.apocalypse.common.event.PlayerEvents;
import com.toast.apocalypse.common.event.VillagerTradeEvents;
import com.toast.apocalypse.common.network.PacketHandler;
import com.toast.apocalypse.common.register.*;
import com.toast.apocalypse.common.triggers.ApocalypseTriggers;
import com.toast.apocalypse.common.util.MobWikiIndexes;
import com.toast.apocalypse.common.util.RainDamageTickHelper;
import com.toast.apocalypse.common.util.VersionCheckHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;
import java.util.function.Supplier;

@Mod(Apocalypse.MODID)
public class Apocalypse {

    /** The mod's ID **/
    public static final String MODID = "apocalypse";

    /** The mod's display name */
    public static final String MOD_NAME = "Apocalypse Rebooted";

    /** A logger instance using the modid as prefix/identifier **/
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    /** The instance of the mod class */
    public static Apocalypse INSTANCE;

    /** Difficulty manager instance */
    private final PlayerDifficultyManager difficultyManager = new PlayerDifficultyManager();

    /** Registry helper instance */
    private final RegistryHelper registryHelper = new RegistryHelper();

    /** Api class instance */
    private final ApocalypseAPI api = new ApocalypseAPI();

    /** Packet handler instance */
    private final PacketHandler packetHandler = new PacketHandler();


    public Apocalypse() {
        INSTANCE = this;

        EventRegistry.init();
        ApocalypseTriggers.init();
        MobWikiIndexes.init();

        this.packetHandler.registerMessages();

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(ApocalypseEntities::createEntityAttributes);
        eventBus.addListener(this::onCommonSetup);
        eventBus.addListener(this::onLoadComplete);
        eventBus.addListener(this::sendIMCMessages);
        eventBus.addListener(this::readIMCMessages);

        MinecraftForge.EVENT_BUS.register(new RainDamageTickHelper());
        MinecraftForge.EVENT_BUS.register(new EntityEvents());
        MinecraftForge.EVENT_BUS.register(new PlayerEvents());
        MinecraftForge.EVENT_BUS.register(new CapabilityAttachEvents());
        MinecraftForge.EVENT_BUS.register(this.getDifficultyManager());
        MinecraftForge.EVENT_BUS.register(new VillagerTradeEvents());
        MinecraftForge.EVENT_BUS.addListener(CommandRegister::registerCommands);

        ApocalypseBlocks.BLOCKS.register(eventBus);
        ApocalypseItems.ITEMS.register(eventBus);
        ApocalypseEffects.EFFECTS.register(eventBus);
        ApocalypseEntities.ENTITIES.register(eventBus);
        ApocalypseParticles.PARTICLES.register(eventBus);
        ApocalypseTileEntities.TILE_ENTITIES.register(eventBus);

        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.COMMON, ApocalypseCommonConfig.COMMON_SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, ApocalypseClientConfig.CLIENT_SPEC);
        context.registerConfig(ModConfig.Type.SERVER, ApocalypseServerConfig.SERVER_SPEC);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ApocalypseArgumentTypes.register();
            ApocalypseCapabilities.registerCapabilities();
            ApocalypseEntities.registerEntitySpawnPlacement();
        });
    }

    public void onLoadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            this.processPlugins();
            VersionCheckHelper.setUpdateMessage();
        });
    }

    private void processPlugins() {
        // Load mod plugins
        ModList.get().getAllScanData().forEach(scanData -> {
            scanData.getAnnotations().forEach(annotationData -> {

                // Look for classes annotated with @ApocalypsePlugin
                if (annotationData.getAnnotationType().getClassName().equals(ApocalypsePlugin.class.getName())) {
                    String modid = (String) annotationData.getAnnotationData().getOrDefault("modid", "");

                    if (ModList.get().isLoaded(modid) || modid.isEmpty()) {
                        try {
                            Class<?> pluginClass = Class.forName(annotationData.getMemberName());

                            if (IApocalypsePlugin.class.isAssignableFrom(pluginClass)) {
                                IApocalypsePlugin plugin = (IApocalypsePlugin) pluginClass.newInstance();
                                this.registryHelper.setCurrentPluginId(plugin.getPluginId());
                                plugin.load(this.getApi());
                                LOGGER.info("Found Apocalypse plugin at {} with plugin ID: {}", annotationData.getMemberName(), plugin.getPluginId());
                            }
                        }
                        catch (Exception e) {
                            LOGGER.error("Failed to load Apocalypse plugin at {}! Damn dag nabit damnit!", annotationData.getMemberName());
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
        // Post setup
        this.registryHelper.postSetup();
    }

    public void sendIMCMessages(InterModEnqueueEvent event) {
        // Compat for TheOneProbe
        if (ModList.get().isLoaded("theoneprobe")) {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPEntityInfoProvider::new);
        }
    }

    /**
     * Yeets the API instance to mods asking for it.
     */
    public void readIMCMessages(InterModProcessEvent event) {
        event.getIMCStream().forEach((message) -> {
            if (message.getMethod().equals("getApocalypseApi")) {
                Supplier<Function<IApocalypseApi, Void>> supplier = message.getMessageSupplier();
                supplier.get().apply(this.getApi());
            }
        });
    }

    public static ResourceLocation resourceLoc(String path) {
        return new ResourceLocation(MODID, path);
    }

    public PlayerDifficultyManager getDifficultyManager() {
        return this.difficultyManager;
    }

    public RegistryHelper getRegistryHelper() {
        return this.registryHelper;
    }

    public ApocalypseAPI getApi() {
        return this.api;
    }

    public PacketHandler getPacketHandler() {
        return this.packetHandler;
    }
}
