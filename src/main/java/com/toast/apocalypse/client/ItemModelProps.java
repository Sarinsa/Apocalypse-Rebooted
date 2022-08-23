package com.toast.apocalypse.client;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemModelsProperties;

public class ItemModelProps {

    protected static void register() {
        // Lunar clock
        ItemModelsProperties.register(ApocalypseItems.LUNAR_CLOCK.get(), Apocalypse.resourceLoc("moon_phase"), (itemStack, clientWorld, livingEntity) -> {
            Entity entity = livingEntity != null ? livingEntity : itemStack.getEntityRepresentation();

            return entity == null ? 0.0F : (float) ClientUtil.OVERWORLD_MOON_PHASE;
        });
    }
}
