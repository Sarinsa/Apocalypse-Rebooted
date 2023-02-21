package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ServerWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class C2SUpdateGrumpDescent {

    public final UUID uuid;
    public final boolean keyReleased;

    public C2SUpdateGrumpDescent(UUID uuid, boolean keyReleased) {
        this.uuid = uuid;
        this.keyReleased = keyReleased;
    }

    public static void handle(C2SUpdateGrumpDescent message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> ServerWork.handleUpdateGrumpDescent(message));
        }
        context.setPacketHandled(true);
    }

    public static C2SUpdateGrumpDescent decode(FriendlyByteBuf buffer) {
        return new C2SUpdateGrumpDescent(buffer.readUUID(), buffer.readBoolean());
    }

    public static void encode(C2SUpdateGrumpDescent message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.uuid);
        buffer.writeBoolean(message.keyReleased);
    }
}
