package com.toast.apocalypse.common.network.message;

import com.toast.apocalypse.common.network.work.ClientWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class S2CApocalypseEvent {
    
    public enum EventStatus {
        STARTED,
        ENDED;
        
        @Nullable
        public static EventStatus fromOrdinal( int ordinal ) {
            for( EventStatus eventStatus : EventStatus.values() ) {
                if( eventStatus.ordinal() == ordinal ) {
                    return eventStatus;
                }
            }
            return null;
        }
    }
    
    public final int eventId;
    public final EventStatus eventStatus;
    
    
    public S2CApocalypseEvent( int eventId, EventStatus eventStatus ) {
        this.eventId = eventId;
        this.eventStatus = eventStatus;
    }
    
    public static void handle( S2CApocalypseEvent message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> ClientWork.handleApocalypseEventUpdate( message ) );
        }
        context.setPacketHandled( true );
    }
    
    public static S2CApocalypseEvent decode( FriendlyByteBuf buffer ) {
        return new S2CApocalypseEvent( buffer.readInt(), EventStatus.fromOrdinal( buffer.readInt() ) );
    }
    
    public static void encode( S2CApocalypseEvent message, FriendlyByteBuf buffer ) {
        buffer.writeInt( message.eventId );
        buffer.writeInt( message.eventStatus.ordinal() );
    }
}
