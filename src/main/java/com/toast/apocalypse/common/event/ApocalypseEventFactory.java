package com.toast.apocalypse.common.event;

import com.toast.apocalypse.api.event.ApocalypseEvent;
import com.toast.apocalypse.api.event.SeekerAlertEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class ApocalypseEventFactory {
    
    /**
     * Fires a {@link SeekerAlertEvent} and posts it on the  {@link MinecraftForge#EVENT_BUS} bus.
     *
     * @param level   The level where the alert event is taking place.
     * @param seeker  The seeker that is triggering this alert event.
     * @param toAlert A list of mobs that are being alerted by the seeker.
     * @param target  The seeker's current target.
     * @return True if the event was canceled.
     */
    public static boolean fireSeekerAlertEvent( Level level, Mob seeker, List<? extends Mob> toAlert, LivingEntity target ) {
        return MinecraftForge.EVENT_BUS.post( new SeekerAlertEvent( level, seeker, toAlert, target ) );
    }
    
    /**
     * Fires a {@link ApocalypseEvent.Start} event and posts it on the {@link MinecraftForge#EVENT_BUS} bus.
     *
     * @param player  The player to start an Apocalypse event for.
     * @param eventId The ID of the Apocalypse event that is starting.
     * @return True if the event was canceled.
     */
    public static boolean fireApocalypseStartEvent( Player player, int eventId ) {
        return MinecraftForge.EVENT_BUS.post( new ApocalypseEvent.Start( player, eventId ) );
    }
    
    /**
     * Fires a {@link ApocalypseEvent.Stop} event and posts it on the {@link MinecraftForge#EVENT_BUS} bus.
     *
     * @param player  The player to stop an Apocalypse event for.
     * @param eventId The ID of the Apocalypse event that is stopping.
     * @return True if the event was canceled.
     */
    public static boolean fireApocalypseStopEvent( Player player, int eventId ) {
        return MinecraftForge.EVENT_BUS.post( new ApocalypseEvent.Stop( player, eventId ) );
    }
}
