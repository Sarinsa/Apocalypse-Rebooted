package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ClientWork;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CUpdatePlayerDifficultyRate {

    public final double multiplier;

    public S2CUpdatePlayerDifficultyRate(double multiplier) {
        this.multiplier = multiplier;
    }

    public static void handle(S2CUpdatePlayerDifficultyRate message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleDifficultyRateUpdate(message));
        }
        context.setPacketHandled(true);
    }

    public static S2CUpdatePlayerDifficultyRate decode(PacketBuffer buffer) {
        return new S2CUpdatePlayerDifficultyRate(buffer.readDouble());
    }

    public static void encode(S2CUpdatePlayerDifficultyRate message, PacketBuffer buffer) {
        buffer.writeDouble(message.multiplier);
    }
}
