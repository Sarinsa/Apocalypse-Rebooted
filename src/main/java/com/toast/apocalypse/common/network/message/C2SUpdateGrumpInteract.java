package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ServerWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;

import java.util.UUID;
import java.util.function.Supplier;

public class C2SUpdateGrumpInteract {
    
    public final UUID uuid;
    public final Vec3 lookVec;
    
    public C2SUpdateGrumpInteract( UUID uuid, Vec3 lookVec ) {
        this.uuid = uuid;
        this.lookVec = lookVec;
    }
    
    public static void handle( C2SUpdateGrumpInteract message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isServer() ) {
            context.enqueueWork( () -> ServerWork.handleUpdateGrumpInteract( message ) );
        }
        context.setPacketHandled( true );
    }
    
    public static C2SUpdateGrumpInteract decode( FriendlyByteBuf buffer ) {
        UUID uuid = buffer.readUUID();
        Vector3f lookVec = buffer.readVector3f();
        
        return new C2SUpdateGrumpInteract( uuid, new Vec3( lookVec.x, lookVec.y, lookVec.z ) );
    }
    
    public static void encode( C2SUpdateGrumpInteract message, FriendlyByteBuf buffer ) {
        buffer.writeUUID( message.uuid );
        buffer.writeVector3f( new Vector3f(
                (float) message.lookVec.x,
                (float) message.lookVec.y,
                (float) message.lookVec.z )
        );
    }
}
