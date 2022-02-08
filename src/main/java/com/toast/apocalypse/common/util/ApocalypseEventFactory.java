package com.toast.apocalypse.common.util;

import com.toast.apocalypse.api.SeekerAlertEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class ApocalypseEventFactory {

    public static void fireSeekerAlertEvent(World world, MobEntity seeker, List<MobEntity> toAlert, LivingEntity target) {
        MinecraftForge.EVENT_BUS.post(new SeekerAlertEvent(world, seeker, toAlert, target));
    }
}
