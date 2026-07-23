package com.toast.apocalypse.client;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.Entity;

public class ItemModelProps {
    
    protected static void register() {
        // Lunar clock
        ItemProperties.register( ApocalypseObjects.Items.LUNAR_CLOCK.get(), Apocalypse.rl( "moon_phase" ), ( itemStack, clientLevel, livingEntity, seed ) -> {
            Entity entity = livingEntity != null ? livingEntity : itemStack.getEntityRepresentation();
            // noinspection resource
            return entity == null ? 0.0F : (float) entity.level().getMoonPhase();
        } );
    }
}
