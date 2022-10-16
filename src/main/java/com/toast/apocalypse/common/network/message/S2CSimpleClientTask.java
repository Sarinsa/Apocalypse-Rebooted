package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ClientWork;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSimpleClientTask {

    public static final byte SET_ACID_RAIN = 0;
    public static final byte REMOVE_ACID_RAIN = 1;

    public byte action;


    public S2CSimpleClientTask(byte action) {
        this.action = action;
    }

    public static void handle(S2CSimpleClientTask message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleSimpleClientTaskRequest(message));
        }
        context.setPacketHandled(true);
    }

    public static S2CSimpleClientTask decode(PacketBuffer buffer) {
        return new S2CSimpleClientTask(buffer.readByte());
    }

    public static void encode(S2CSimpleClientTask message, PacketBuffer buffer) {
        buffer.writeByte(message.action);
    }
}
