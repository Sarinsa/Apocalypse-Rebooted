package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ClientWork;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CUpdateMoonPhase {

    public final int moonPhase;

    public S2CUpdateMoonPhase(int moonPhase) {
        this.moonPhase = moonPhase;
    }

    public static void handle(S2CUpdateMoonPhase message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleMoonPhaseUpdate(message));
        }
        context.setPacketHandled(true);
    }

    public static S2CUpdateMoonPhase decode(PacketBuffer buffer) {
        return new S2CUpdateMoonPhase(buffer.readInt());
    }

    public static void encode(S2CUpdateMoonPhase message, PacketBuffer buffer) {
        buffer.writeInt(message.moonPhase);
    }
}
