package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ServerWork;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class C2SOpenGrumpInventory {

    public final UUID uuid;

    public C2SOpenGrumpInventory(UUID uuid) {
        this.uuid = uuid;
    }

    public static void handle(C2SOpenGrumpInventory message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> ServerWork.handleOpenGrumpInventory(message));
        }
        context.setPacketHandled(true);
    }

    public static C2SOpenGrumpInventory decode(PacketBuffer buffer) {
        return new C2SOpenGrumpInventory(buffer.readUUID());
    }

    public static void encode(C2SOpenGrumpInventory message, PacketBuffer buffer) {
        buffer.writeUUID(message.uuid);
    }
}
