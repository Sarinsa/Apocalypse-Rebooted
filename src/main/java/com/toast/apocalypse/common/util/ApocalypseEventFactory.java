package com.toast.apocalypse.common.util;

import com.toast.apocalypse.api.SeekerAlertEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ApocalypseEventFactory {

    public static boolean fireSeekerAlertEvent(World world, MobEntity seeker, MobEntity toAlert, LivingEntity target) {
        return MinecraftForge.EVENT_BUS.post(new SeekerAlertEvent(world, seeker, toAlert, target));
    }
}
