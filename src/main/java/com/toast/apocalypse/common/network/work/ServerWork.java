package com.toast.apocalypse.common.network.work;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.Grump;
import com.toast.apocalypse.common.misc.PlayerKeyBindInfo;
import com.toast.apocalypse.common.network.message.C2SOpenGrumpInventory;
import com.toast.apocalypse.common.network.message.C2SUpdateGrumpDescent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerWork {

    private static MinecraftServer server;

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        server = event.getServer();
    }

    public static void handleOpenGrumpInventory(C2SOpenGrumpInventory message) {
        if (server == null)
            return;

        ServerPlayer player = server.getPlayerList().getPlayer(message.uuid);

        if (player != null && player.getVehicle() instanceof Grump) {
            ((Grump) player.getVehicle()).openContainerForPlayer(player);
        }
    }

    public static void handleUpdateGrumpDescent(C2SUpdateGrumpDescent message) {
        if (server == null)
            return;

        ServerPlayer player = server.getPlayerList().getPlayer(message.uuid);

        if (player != null && player.getVehicle() instanceof Grump) {
            PlayerKeyBindInfo.getInfo(player.getUUID()).grumpDescent.setValue(!message.keyReleased);
        }
    }
}
