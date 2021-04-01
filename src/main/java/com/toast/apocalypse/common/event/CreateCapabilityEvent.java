package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.capability.rain_tick.RainTickCapabilityProvider;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CreateCapabilityEvent {

    @SubscribeEvent
    public void onCapabilityCreation(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(Apocalypse.resourceLoc("rain_tick"), new RainTickCapabilityProvider());
        }
    }
}
