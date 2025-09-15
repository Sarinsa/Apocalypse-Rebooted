package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.network.message.S2CSimpleClientTask;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Server-only event listener.
 */
@Mod.EventBusSubscriber(modid = Apocalypse.MODID)
public class ServerEventListener {

    private static MinecraftServer server;

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        server = event.getServer();
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES) {
            if (type == EntityType.PLAYER) continue;

            try {
                Entity entity = type.create(event.getServer().overworld());

                // We only bother with living entities.
                if (entity instanceof LivingEntity) {
                    EntityEventListener.ENTITY_FOR_TYPE.put(type, entity);
                }
                else if (entity == null) {
                    Apocalypse.LOGGER.error("Failed to create entity instance for type {}! Mob spawn difficulty config list will not work for this type!", type);
                }
            }
            catch (Exception ignored) {
                // If this explodes, no worries (probably isn't a normal living entity anyway)
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppedEvent event) {
        server = null;
        EntityEventListener.ENTITY_FOR_TYPE.clear();
    }
}
