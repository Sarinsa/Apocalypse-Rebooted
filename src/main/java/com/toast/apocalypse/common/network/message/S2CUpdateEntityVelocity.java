package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ClientWork;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CUpdateEntityVelocity {

    public final double xMotion;
    public final double yMotion;
    public final double zMotion;

    public final int entityId;

    public S2CUpdateEntityVelocity(Entity entity, Vector3d velocity) {
        this(velocity.x, velocity.y, velocity.z, entity.getId());
    }

    public S2CUpdateEntityVelocity(double xMotion, double yMotion, double zMotion, int entityId) {
        this.xMotion = xMotion;
        this.yMotion = yMotion;
        this.zMotion = zMotion;
        this.entityId = entityId;
    }

    public static void handle(S2CUpdateEntityVelocity message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleEntityVelocityUpdate(message));
        }
        context.setPacketHandled(true);
    }

    public static S2CUpdateEntityVelocity decode(PacketBuffer buffer) {
        return new S2CUpdateEntityVelocity(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readInt());
    }

    public static void encode(S2CUpdateEntityVelocity message, PacketBuffer buffer) {
        buffer.writeDouble(message.xMotion);
        buffer.writeDouble(message.yMotion);
        buffer.writeDouble(message.zMotion);
        buffer.writeInt(message.entityId);
    }
}
