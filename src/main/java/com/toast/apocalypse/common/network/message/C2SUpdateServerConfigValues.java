package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ServerWork;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SUpdateServerConfigValues {

    public final double maxDifficulty;
    public final double gracePeriod;

    public C2SUpdateServerConfigValues(double maxDifficulty, double gracePeriod) {
        this.maxDifficulty = maxDifficulty;
        this.gracePeriod = gracePeriod;
    }

    public static void handle(C2SUpdateServerConfigValues message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> ServerWork.handleServerConfigUpdate(message));
        }
        context.setPacketHandled(true);
    }

    public static C2SUpdateServerConfigValues decode(PacketBuffer buffer) {
        return new C2SUpdateServerConfigValues(buffer.readDouble(), buffer.readDouble());
    }

    public static void encode(C2SUpdateServerConfigValues message, PacketBuffer buffer) {
        buffer.writeDouble(message.maxDifficulty);
        buffer.writeDouble(message.gracePeriod);
    }
}
