package com.toast.apocalypse.common.event;

import com.toast.apocalypse.api.event.SeekerAlertEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class ApocalypseEventFactory {
    
    /**
     * Fires a {@link SeekerAlertEvent} and posts it on the  {@link MinecraftForge#EVENT_BUS} bus.
     *
     * @return True if the event was canceled.
     */
    public static boolean fireSeekerAlertEvent( Level level, Mob seeker, List<? extends Mob> toAlert, LivingEntity target ) {
        return MinecraftForge.EVENT_BUS.post( new SeekerAlertEvent( level, seeker, toAlert, target ) );
    }
}
