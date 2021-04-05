package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ClientWork;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CUpdateWorldDifficultyRate {

    public final double difficultyRate;

    public S2CUpdateWorldDifficultyRate(double rate) {
        this.difficultyRate = rate;
    }

    public static void handle(S2CUpdateWorldDifficultyRate message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleDifficultyRateUpdate(message));
        }
        context.setPacketHandled(true);
    }

    public static S2CUpdateWorldDifficultyRate decode(PacketBuffer buffer) {
        return new S2CUpdateWorldDifficultyRate(buffer.readDouble());
    }

    public static void encode(S2CUpdateWorldDifficultyRate message, PacketBuffer buffer) {
        buffer.writeDouble(message.difficultyRate);
    }
}
