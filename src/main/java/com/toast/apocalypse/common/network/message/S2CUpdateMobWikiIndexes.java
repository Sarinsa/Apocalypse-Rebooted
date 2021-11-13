package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ClientWork;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CUpdateMobWikiIndexes {

    public final int[] indexes;

    public S2CUpdateMobWikiIndexes(int[] indexes) {
        this.indexes = indexes;
    }

    public static void handle(S2CUpdateMobWikiIndexes message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleMobWikiIndexUpdate(message));
        }
        context.setPacketHandled(true);
    }

    public static S2CUpdateMobWikiIndexes decode(PacketBuffer buffer) {
        return new S2CUpdateMobWikiIndexes(buffer.readVarIntArray());
    }

    public static void encode(S2CUpdateMobWikiIndexes message, PacketBuffer buffer) {
        buffer.writeVarIntArray(message.indexes);
    }
}
