package com.toast.apocalypse.client.event;

import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventListener {
    
    public ClientEventListener() { }
    
    
    /** Called after a GUI overlay has been rendered on the screen. */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public void onRenderGuiOverlayPost( RenderGuiOverlayEvent.Post event ) { }
    
    /** Called after a screen has been initialized. */
    @SubscribeEvent
    public void onScreenInitialized( ScreenEvent.Init.Post event ) { }
}
