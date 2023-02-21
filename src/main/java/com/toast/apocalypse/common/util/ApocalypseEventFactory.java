package com.toast.apocalypse.common.util;

import com.toast.apocalypse.api.SeekerAlertEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class ApocalypseEventFactory {

    public static void fireSeekerAlertEvent(Level level, Mob seeker, List<? extends Mob> toAlert, LivingEntity target) {
        MinecraftForge.EVENT_BUS.post(new SeekerAlertEvent(level, seeker, toAlert, target));
    }
}
