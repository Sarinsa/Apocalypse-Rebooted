package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ClientWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CUpdatePlayerMaxDifficulty {

    public final long maxDifficulty;

    public S2CUpdatePlayerMaxDifficulty(long maxDifficulty) {
        this.maxDifficulty = maxDifficulty;
    }

    public static void handle(S2CUpdatePlayerMaxDifficulty message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleMaxDifficultyUpdate(message));
        }
        context.setPacketHandled(true);
    }

    public static S2CUpdatePlayerMaxDifficulty decode(FriendlyByteBuf buffer) {
        return new S2CUpdatePlayerMaxDifficulty(buffer.readLong());
    }

    public static void encode(S2CUpdatePlayerMaxDifficulty message, FriendlyByteBuf buffer) {
        buffer.writeLong(message.maxDifficulty);
    }
}
