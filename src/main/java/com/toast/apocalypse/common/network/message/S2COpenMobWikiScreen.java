package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ClientWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class S2COpenMobWikiScreen {

    public final UUID uuid;

    public S2COpenMobWikiScreen(UUID uuid) {
        this.uuid = uuid;
    }

    public static void handle(S2COpenMobWikiScreen message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleOpenMobWikiScreen(message));
        }
        context.setPacketHandled(true);
    }

    public static S2COpenMobWikiScreen decode(FriendlyByteBuf buffer) {
        return new S2COpenMobWikiScreen(buffer.readUUID());
    }

    public static void encode(S2COpenMobWikiScreen message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.uuid);
    }
}
