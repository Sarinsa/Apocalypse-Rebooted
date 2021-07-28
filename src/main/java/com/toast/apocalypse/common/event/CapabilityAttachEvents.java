package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.capability.difficulty.DifficultyCapabilityProvider;
import com.toast.apocalypse.common.capability.entity_marker.EntityMarkerCapabilityProvider;
import com.toast.apocalypse.common.capability.event_data.EventDataCapabilityProvider;
import com.toast.apocalypse.common.capability.rain_tick.RainTickCapabilityProvider;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityAttachEvents {

    @SubscribeEvent
    public void onEntityCapabilityAttach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(Apocalypse.resourceLoc("rain_tick"), new RainTickCapabilityProvider());
            event.addCapability(Apocalypse.resourceLoc("difficulty"), new DifficultyCapabilityProvider());
            event.addCapability(Apocalypse.resourceLoc("event_data"), new EventDataCapabilityProvider());
        }
        else if (event.getObject() instanceof LivingEntity) {
            event.addCapability(Apocalypse.resourceLoc("entity_marker"), new EntityMarkerCapabilityProvider());
        }
    }
}
