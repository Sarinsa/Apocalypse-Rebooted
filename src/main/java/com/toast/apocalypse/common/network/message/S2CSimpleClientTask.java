package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ClientWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSimpleClientTask {
    
    public static final byte SET_ACID_RAIN = 0;
    public static final byte REMOVE_ACID_RAIN = 1;
    public static final byte ENABLE_ACID_SNOW = 2;
    public static final byte DISABLE_ACID_SNOW = 3;
    
    
    /** The "ID" of the logic to run on the client. */
    public byte actionId;
    /** Optional value for some actions. Can be interpreted as different things depending on actions. */
    public int value;
    
    
    public S2CSimpleClientTask( byte actionId, int value ) {
        this.actionId = actionId;
    }
    
    public S2CSimpleClientTask( byte actionId ) {
        this( actionId, 0 );
    }
    
    public static void handle( S2CSimpleClientTask message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> ClientWork.handleSimpleClientTaskRequest( message ) );
        }
        context.setPacketHandled( true );
    }
    
    public static S2CSimpleClientTask decode( FriendlyByteBuf buffer ) {
        return new S2CSimpleClientTask( buffer.readByte(), buffer.readInt() );
    }
    
    public static void encode( S2CSimpleClientTask message, FriendlyByteBuf buffer ) {
        buffer.writeByte( message.actionId );
        buffer.writeInt( message.value );
    }
}
