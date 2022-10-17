package com.toast.apocalypse.common.network.work;

import com.toast.apocalypse.common.entity.living.GrumpEntity;
import com.toast.apocalypse.common.network.message.C2SOpenGrumpInventory;
import com.toast.apocalypse.common.network.message.C2SUpdateGrumpDescent;
import com.toast.apocalypse.common.misc.PlayerKeyBindInfo;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class ServerWork {

    public static void handleOpenGrumpInventory(C2SOpenGrumpInventory message) {
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);

        if (server == null)
            return;

        ServerPlayerEntity player = server.getPlayerList().getPlayer(message.uuid);

        if (player != null && player.getVehicle() instanceof GrumpEntity) {
            ((GrumpEntity) player.getVehicle()).openContainerForPlayer(player);
        }
    }

    public static void handleUpdateGrumpDescent(C2SUpdateGrumpDescent message) {
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);

        if (server == null)
            return;

        ServerPlayerEntity player = server.getPlayerList().getPlayer(message.uuid);

        if (player != null && player.getVehicle() instanceof GrumpEntity) {
            PlayerKeyBindInfo.getInfo(player.getUUID()).grumpDescent.setValue(!message.keyReleased);
        }
    }
}
