package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ClientWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CUpdatePlayerDifficulty {

    public final long difficulty;

    public S2CUpdatePlayerDifficulty(long difficulty) {
        this.difficulty = difficulty;
    }

    public static void handle(S2CUpdatePlayerDifficulty message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleDifficultyUpdate(message));
        }
        context.setPacketHandled(true);
    }

    public static S2CUpdatePlayerDifficulty decode(FriendlyByteBuf buffer) {
        return new S2CUpdatePlayerDifficulty(buffer.readLong());
    }

    public static void encode(S2CUpdatePlayerDifficulty message, FriendlyByteBuf buffer) {
        buffer.writeLong(message.difficulty);
    }
}
