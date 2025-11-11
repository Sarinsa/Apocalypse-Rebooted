package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ClientWork;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CDynTrapUpdate {
    
    public final BlockPos pos;
    public final String id;
    public final int trapRadius;
    
    public S2CDynTrapUpdate( BlockPos pos, String id, int trapRadius ) {
        this.pos = pos;
        this.id = id;
        this.trapRadius = trapRadius;
    }
    
    public static void handle( S2CDynTrapUpdate message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> ClientWork.handleDynTrapUpdate( message ) );
        }
        context.setPacketHandled( true );
    }
    
    public static S2CDynTrapUpdate decode( FriendlyByteBuf buffer ) {
        return new S2CDynTrapUpdate( buffer.readBlockPos(), buffer.readUtf(), buffer.readInt() );
    }
    
    public static void encode( S2CDynTrapUpdate message, FriendlyByteBuf buffer ) {
        buffer.writeBlockPos( message.pos );
        buffer.writeUtf( message.id );
        buffer.writeInt( message.trapRadius );
    }
}
