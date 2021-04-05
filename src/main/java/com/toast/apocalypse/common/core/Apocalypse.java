package com.toast.apocalypse.common.core;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import com.toast.apocalypse.common.command.CommandRegister;
import com.toast.apocalypse.common.core.config.ApocalypseClientConfig;
import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import com.toast.apocalypse.common.core.mod_event.EventRegister;
import com.toast.apocalypse.common.event.CapabilityAttachEvents;
import com.toast.apocalypse.common.event.CommonConfigReloadListener;
import com.toast.apocalypse.common.event.EntityEvents;
import com.toast.apocalypse.common.network.PacketHandler;
import com.toast.apocalypse.common.register.ApocalypseEffects;
import com.toast.apocalypse.common.register.ApocalypseEntities;
import com.toast.apocalypse.common.register.ApocalypseItems;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Apocalypse.MODID)
public class Apocalypse {

    /** The mod's ID **/
    public static final String MODID = "apocalypse";

    /** A logger instance using the modid as prefix/identifier **/
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    /** The instance of out mod class */
    public static Apocalypse INSTANCE;

    /** The difficulty manager instance */
    private final WorldDifficultyManager difficultyManager = new WorldDifficultyManager();

    /** Packet handler instance */
    private final PacketHandler packetHandler = new PacketHandler();

    public Apocalypse() {
        INSTANCE = this;

        ApocalypseEntities.initTypes();

        this.packetHandler.registerMessages();

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::onCommonSetup);
        eventBus.addListener(ApocalypseEntities::createEntityAttributes);

        MinecraftForge.EVENT_BUS.register(new EntityEvents());
        MinecraftForge.EVENT_BUS.register(new CapabilityAttachEvents());
        MinecraftForge.EVENT_BUS.register(this.getDifficultyManager());
        MinecraftForge.EVENT_BUS.addListener(CommandRegister::registerCommands);
        MinecraftForge.EVENT_BUS.addListener(this::onServerAboutToStart);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStopped);

        ApocalypseItems.ITEMS.register(eventBus);
        ApocalypseEffects.EFFECTS.register(eventBus);
        ApocalypseEntities.ENTITIES.register(eventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ApocalypseCommonConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ApocalypseClientConfig.CLIENT_SPEC);
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ApocalypseCapabilities.registerCapabilities();
            ApocalypseEntities.registerEntitySpawnPlacement();
            EventRegister.registerEvents();
        });
    }

    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        this.getDifficultyManager().init(event.getServer());
    }

    public void onServerStopped(FMLServerStoppedEvent event) {
        this.getDifficultyManager().cleanup();
    }

    public static ResourceLocation resourceLoc(String path) {
        return new ResourceLocation(MODID, path);
    }

    public WorldDifficultyManager getDifficultyManager() {
        return this.difficultyManager;
    }

    public PacketHandler getPacketHandler() {
        return this.packetHandler;
    }
}
