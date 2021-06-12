package com.toast.apocalypse.client;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.register.ApocalypseItems;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class ItemModelProps {

    protected static void register() {
        // Lunar clock
        ItemModelsProperties.register(ApocalypseItems.LUNAR_CLOCK.get(), Apocalypse.resourceLoc("moon_phase"), (itemStack, clientWorld, livingEntity) -> {
            Entity entity = livingEntity != null ? livingEntity : itemStack.getEntityRepresentation();

            if (entity == null) {
                return 0.0F;
            }
            else {
                if (clientWorld == null && entity.level instanceof ClientWorld) {
                    clientWorld = (ClientWorld)entity.level;
                }

                if (clientWorld == null) {
                    return 0.0F;
                }
                else {
                    double moonBrightness;

                    if (clientWorld.dimension() == World.OVERWORLD) {
                        moonBrightness = clientWorld.getMoonBrightness();
                    }
                    else {
                        moonBrightness = DimensionType.MOON_BRIGHTNESS_PER_PHASE[clientWorld.random.nextInt(DimensionType.MOON_BRIGHTNESS_PER_PHASE.length)];
                    }
                    return (float) moonBrightness;
                }
            }
        });
    }
}
