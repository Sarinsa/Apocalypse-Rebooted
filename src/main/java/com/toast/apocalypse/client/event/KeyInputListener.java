package com.toast.apocalypse.client.event;

import com.mojang.blaze3d.platform.InputConstants;
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
    
    /** The game. Woah. */
    private final Minecraft mc;
    
    
    public KeyInputListener() {
        this.mc = Minecraft.getInstance();
    }
    
    
    /** Called when a keyboard key input occurs, such as pressing, releasing, or repeating a key. */
    @SubscribeEvent
    public void onKey( InputEvent.Key event ) {
        // Check if the player has no open GUIs
        if( mc.player != null && mc.screen == null ) {
            int action = event.getAction();
            
            switch( action ) {
                case InputConstants.RELEASE -> {
                    if( key( event, ApocalypseKeyBindings.GRUMP_DESCENT ) )
                        handleGrumpDescent( false );
                }
                case InputConstants.PRESS -> {
                    if( key( event, mc.options.keyInventory ) )
                        handleInventoryPress();
                    else if( key( event, ApocalypseKeyBindings.GRUMP_DESCENT ) )
                        handleGrumpDescent( true );
                    else if( key( event, ApocalypseKeyBindings.GRUMP_INTERACTION ) )
                        handleGrumpInteract();
                }
                case InputConstants.REPEAT -> {
                    // Nothing to do yet.
                }
            }
        }
    }
    
    /** Called when the inventory key is pressed. */
    private void handleInventoryPress() {
        final LocalPlayer player = mc.player;
        if( player == null ) return;
        
        // Open the Grump inventory if the player is riding a Grump
        if( player.getVehicle() instanceof Grump ) {
            NetworkHelper.requestOpenGrumpInventory( player.getUUID() );
        }
    }
    
    /**
     * Called when the "Grump descent" key is pressed or released.
     *
     * @see ApocalypseKeyBindings#GRUMP_DESCENT
     */
    private void handleGrumpDescent( boolean keyPressed ) {
        final LocalPlayer player = mc.player;
        if( player == null ) return;
        
        PlayerKeyBindInfo.getInfo( player.getUUID() ).grumpDescent.setValue( keyPressed );
        NetworkHelper.requestGrumpDescentUpdate( player.getUUID(), keyPressed );
    }
    
    /**
     * Called when the "Grump interaction" key is pressed.
     *
     * @see ApocalypseKeyBindings#GRUMP_INTERACTION
     */
    private void handleGrumpInteract() {
        final LocalPlayer player = mc.player;
        if( player == null ) return;
        
        Vec3 lookVec = player.getViewVector( 1.0F ).scale( player.getBbWidth() );
        NetworkHelper.requestGrumpInteractUpdate( player.getUUID(), lookVec );
    }
    
    /**
     * Convenience method for comparing the key of a key event against a key mapping.
     *
     * @return True if the event's key is equal to the key of the specified key mapping.
     */
    private boolean key( InputEvent.Key event, KeyMapping checkedKey ) {
        return event.getKey() == checkedKey.getKey().getValue();
    }
}
