package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ClientWork;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CUpdateWorldDifficulty {

    public final long difficulty;

    public S2CUpdateWorldDifficulty(long difficulty) {
        this.difficulty = difficulty;
    }

    public static void handle(S2CUpdateWorldDifficulty message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleDifficultyUpdate(message));
        }
        context.setPacketHandled(true);
    }

    public static S2CUpdateWorldDifficulty decode(PacketBuffer buffer) {
        return new S2CUpdateWorldDifficulty(buffer.readLong());
    }

    public static void encode(S2CUpdateWorldDifficulty message, PacketBuffer buffer) {
        buffer.writeLong(message.difficulty);
    }
}
