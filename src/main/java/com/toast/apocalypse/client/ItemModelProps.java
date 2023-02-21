package com.toast.apocalypse.client;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.Entity;

public class ItemModelProps {

    protected static void register() {
        // Lunar clock
        ItemProperties.register(ApocalypseItems.LUNAR_CLOCK.get(), Apocalypse.resourceLoc("moon_phase"), (itemStack, clientLevel, livingEntity, seed) -> {
            Entity entity = livingEntity != null ? livingEntity : itemStack.getEntityRepresentation();
            return entity == null ? 0.0F : (float) ClientUtil.OVERWORLD_MOON_PHASE;
        });
    }
}
