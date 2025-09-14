package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.difficulty.PlayerDifficultyManager;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.network.message.S2CSimpleClientTask;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Server-only event listener.
 */
@Mod.EventBusSubscriber(modid = Apocalypse.MODID)
public class GeneralServerListener {

    private static MinecraftServer server;

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        server = event.getServer();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppedEvent event) {
        server = null;
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide) {
            ServerLevel overworld = server.overworld();
            ServerPlayer player = (ServerPlayer) event.getEntity();
            PlayerDifficultyManager difficultyManager = Apocalypse.INSTANCE.getDifficultyManager();

            // Send some neato packets
            NetworkHelper.sendUpdatePlayerDifficulty(player);
            NetworkHelper.sendUpdatePlayerDifficultyMult(player);
            NetworkHelper.sendUpdatePlayerMaxDifficulty(player);

            NetworkHelper.sendSimpleClientTaskRequest(player,
                    difficultyManager.isRainingAcid((ServerLevel) player.level())
                            ? S2CSimpleClientTask.SET_ACID_RAIN
                            : S2CSimpleClientTask.REMOVE_ACID_RAIN);

            NetworkHelper.sendSimpleClientTaskRequest(player,
                    ApocalypseConfig.ACID_RAIN.GENERAL.acidSnow.get()
                            ? S2CSimpleClientTask.ENABLE_ACID_SNOW
                            : S2CSimpleClientTask.DISABLE_ACID_SNOW);
        }
    }
}
