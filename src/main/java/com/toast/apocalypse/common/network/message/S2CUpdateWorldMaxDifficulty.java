package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ClientWork;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CUpdateWorldMaxDifficulty {

    public final long maxDifficulty;

    public S2CUpdateWorldMaxDifficulty(long maxDifficulty) {
        this.maxDifficulty = maxDifficulty;
    }

    public static void handle(S2CUpdateWorldMaxDifficulty message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleMaxDifficultyUpdate(message));
        }
        context.setPacketHandled(true);
    }

    public static S2CUpdateWorldMaxDifficulty decode(PacketBuffer buffer) {
        return new S2CUpdateWorldMaxDifficulty(buffer.readLong());
    }

    public static void encode(S2CUpdateWorldMaxDifficulty message, PacketBuffer buffer) {
        buffer.writeLong(message.maxDifficulty);
    }
}
