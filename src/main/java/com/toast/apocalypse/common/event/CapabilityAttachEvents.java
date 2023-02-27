package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.capability.difficulty.DifficultyProvider;
import com.toast.apocalypse.common.capability.event_data.EventDataProvider;
import com.toast.apocalypse.common.capability.mobwiki.MobWikiProvider;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityAttachEvents {

    @SubscribeEvent
    public void onEntityCapabilityAttach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(Apocalypse.resourceLoc("difficulty"), new DifficultyProvider());
            event.addCapability(Apocalypse.resourceLoc("event_data"), new EventDataProvider());
            event.addCapability(Apocalypse.resourceLoc("mob_wiki"), new MobWikiProvider());
        }
    }
}
