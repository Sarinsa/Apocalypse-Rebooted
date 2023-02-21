package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ClientWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class S2COpenGrumpInventory {

    public final UUID uuid;
    public final int containerId;
    public final int entityID;

    public S2COpenGrumpInventory(UUID uuid, int containerId, int entityID) {
        this.uuid = uuid;
        this.containerId = containerId;
        this.entityID = entityID;
    }

    public static void handle(S2COpenGrumpInventory message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleOpenGrumpInventory(message));
        }
        context.setPacketHandled(true);
    }

    public static S2COpenGrumpInventory decode(FriendlyByteBuf buffer) {
        return new S2COpenGrumpInventory(buffer.readUUID(), buffer.readInt(), buffer.readInt());
    }

    public static void encode(S2COpenGrumpInventory message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.uuid);
        buffer.writeInt(message.containerId);
        buffer.writeInt(message.entityID);
    }
}
