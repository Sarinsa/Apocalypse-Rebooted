package com.toast.apocalypse.common.event;

import com.toast.apocalypse.common.capability.difficulty.DifficultyCapProvider;
import com.toast.apocalypse.common.capability.mobwiki.MobWikiCapProvider;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityAttachListener {

    @SubscribeEvent
    public void onEntityCapabilityAttach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(Apocalypse.resourceLoc("difficulty"), new DifficultyCapProvider());
            event.addCapability(Apocalypse.resourceLoc("mob_wiki"), new MobWikiCapProvider());
        }
    }
}
