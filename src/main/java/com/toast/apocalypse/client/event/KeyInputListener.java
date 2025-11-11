package com.toast.apocalypse.client.event;

import com.toast.apocalypse.client.ApocalypseKeyBindings;
import com.toast.apocalypse.common.entity.living.Grump;
import com.toast.apocalypse.common.misc.PlayerKeyBindInfo;
import com.toast.apocalypse.common.network.NetworkHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KeyInputListener {
    
    private final Minecraft mc;
    
    
    public KeyInputListener() {
        this.mc = Minecraft.getInstance();
    }
    
    /** Misc keybinding handling */
    @SubscribeEvent
    public void onKey( InputEvent.Key event ) {
        // Check if the player has no open GUIs
        if( mc.player != null && mc.screen == null ) {
            int action = event.getAction();
            
            switch( action ) {
                // RELEASE
                case 0: {
                    if( key( event, ApocalypseKeyBindings.GRUMP_DESCENT ) ) {
                        handleGrumpDescent( false );
                    }
                }
                break;
                // PRESS
                case 1: {
                    if( key( event, mc.options.keyInventory ) ) {
                        handleInventoryPress();
                    }
                    else if( key( event, ApocalypseKeyBindings.GRUMP_DESCENT ) ) {
                        handleGrumpDescent( true );
                    }
                    else if( key( event, ApocalypseKeyBindings.GRUMP_INTERACTION ) ) {
                        handleGrumpInteract();
                    }
                }
                break;
                // REPEAT
                case 2: {
                
                }
                break;
            }
        }
    }
    
    private void handleInventoryPress() {
        if( mc.player != null ) {
            LocalPlayer player = mc.player;
            
            if( player.getVehicle() instanceof Grump ) {
                NetworkHelper.requestOpenGrumpInventory( player.getUUID() );
            }
        }
    }
    
    private void handleGrumpDescent( boolean keyPressed ) {
        if( mc.player != null ) {
            LocalPlayer player = mc.player;
            
            PlayerKeyBindInfo.getInfo( player.getUUID() ).grumpDescent.setValue( keyPressed );
            NetworkHelper.requestGrumpDescentUpdate( player.getUUID(), keyPressed );
        }
    }
    
    private void handleGrumpInteract() {
        if( mc.player != null ) {
            LocalPlayer player = mc.player;
            Vec3 lookVec = player.getViewVector( 1.0F ).scale( player.getBbWidth() );
            
            NetworkHelper.requestGrumpInteractUpdate( player.getUUID(), lookVec );
        }
    }
    
    private boolean key( InputEvent.Key event, KeyMapping checkedKey ) {
        return event.getKey() == checkedKey.getKey().getValue();
    }
}
