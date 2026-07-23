package com.toast.apocalypse.common.network.work;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.client.ClientUtil;
import com.toast.apocalypse.client.renderer.DifficultyOverlayRenderHandler;
import com.toast.apocalypse.client.screen.GrumpInventoryScreen;
import com.toast.apocalypse.common.blockentity.DynamicTrapBlockEntity;
import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import com.toast.apocalypse.common.capability.difficulty.DifficultyCapProvider;
import com.toast.apocalypse.common.entity.living.Grump;
import com.toast.apocalypse.common.event.ApocalypseEventFactory;
import com.toast.apocalypse.common.inventory.container.GrumpInventoryContainer;
import com.toast.apocalypse.common.network.message.*;
import com.toast.apocalypse.common.util.References;
import fathertoast.crust.api.util.OnClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;

import static com.toast.apocalypse.common.network.message.S2CSimpleClientTask.*;

@OnClient
public class ClientWork {
    
    
    public static void handleDifficultyUpdate( S2CUpdatePlayerDifficulty message ) {
        LocalPlayer player = Minecraft.getInstance().player;
        
        if( player != null ) {
            player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).orElse( DifficultyCapProvider.SUPPLIER.get() ).setDifficulty( message.difficulty );
        }
    }
    
    
    public static void handleDifficultyRateUpdate( S2CUpdatePlayerDifficultyRate message ) {
        LocalPlayer player = Minecraft.getInstance().player;
        
        if( player != null ) {
            player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).orElse( DifficultyCapProvider.SUPPLIER.get() ).setDifficultyMult( message.multiplier );
        }
    }
    
    
    public static void handleMaxDifficultyUpdate( S2CUpdatePlayerMaxDifficulty message ) {
        LocalPlayer player = Minecraft.getInstance().player;
        
        if( player != null ) {
            long maxDifficulty = message.maxDifficulty;
            player.getCapability( ApocalypseCapabilities.DIFFICULTY_CAPABILITY ).orElse( DifficultyCapProvider.SUPPLIER.get() ).setMaxDifficulty( maxDifficulty );
            DifficultyOverlayRenderHandler.COLOR_THRESHOLD = maxDifficulty > -1 ? maxDifficulty : References.DEFAULT_COLOR_CHANGE;
        }
    }
    
    
    public static void handleOpenGrumpInventory( S2COpenGrumpInventory message ) {
        LocalPlayer player = Minecraft.getInstance().player;
        
        if( player == null || !(player.getUUID().equals( message.uuid )) )
            return;
        
        ClientLevel level = Minecraft.getInstance().level;
        
        if( level == null )
            return;
        
        Entity entity = level.getEntity( message.entityID );
        
        if( !(entity instanceof Grump grump) )
            return;
        
        Inventory playerInventory = Minecraft.getInstance().player.getInventory();
        
        GrumpInventoryContainer container = new GrumpInventoryContainer( message.containerId, playerInventory, grump.getInventory(), grump );
        Minecraft.getInstance().player.containerMenu = container;
        Minecraft.getInstance().setScreen( new GrumpInventoryScreen( container, playerInventory, grump ) );
    }
    
    
    public static void handleSimpleClientTaskRequest( S2CSimpleClientTask message ) {
        switch( message.actionId ) {
            case SET_ACID_RAIN: {
                ClientUtil.setIsRainingAcid( true );
                break;
            }
            case REMOVE_ACID_RAIN: {
                ClientUtil.setIsRainingAcid( false );
                break;
            }
            case ENABLE_ACID_SNOW: {
                ClientUtil.setAcidSnowEnabled( true );
                break;
            }
            case DISABLE_ACID_SNOW: {
                ClientUtil.setAcidSnowEnabled( false );
                break;
            }
        }
    }
    
    
    public static void handleDynTrapUpdate( S2CDynTrapUpdate message ) {
        BlockPos pos = message.pos;
        ClientLevel level = Minecraft.getInstance().level;
        
        if( level == null ) return;
        
        if( level.getExistingBlockEntity( pos ) instanceof DynamicTrapBlockEntity trap ) {
            if( message.id.isEmpty() ) {
                trap.setCurrentTrap( null );
                return;
            }
            ResourceLocation id = ResourceLocation.tryParse( message.id );
            
            if( id == null )
                return;
            
            if( ApocalypseObjects.TRAP_ACTIONS_REGISTRY.get().containsKey( id ) ) {
                trap.setCurrentTrap( ApocalypseObjects.TRAP_ACTIONS_REGISTRY.get().getValue( id ) );
                trap.setCurrentTrapRadius( message.trapRadius );
            }
        }
    }
    
    public static void handleApocalypseEventUpdate( S2CApocalypseEvent message ) {
        final LocalPlayer player = Minecraft.getInstance().player;
        
        if( player == null ) return;
        
        switch( message.eventStatus ) {
            case STARTED -> ApocalypseEventFactory.fireApocalypseEventStarted( false, player, message.eventId );
            case ENDED -> ApocalypseEventFactory.fireApocalypseEventEnded( false, player, message.eventId );
        }
    }
}
