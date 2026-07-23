package com.toast.apocalypse.common.event;

import com.toast.apocalypse.api.event.ApocalypseEvent;
import com.toast.apocalypse.api.event.SeekerAlertEvent;
import com.toast.apocalypse.common.network.NetworkHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public final class ApocalypseEventFactory {
    
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
     * Fires a {@link ApocalypseEvent.Starting} event and posts it on the {@link MinecraftForge#EVENT_BUS} bus.
     *
     * @param player  The player who has an Apocalypse event that is starting.
     * @param eventId The ID of the Apocalypse event that is starting.
     * @return True if the event was canceled.
     */
    public static boolean fireApocalypseEventStarting( Player player, int eventId ) {
        return MinecraftForge.EVENT_BUS.post( new ApocalypseEvent.Starting( player, eventId ) );
    }
    
    /**
     * Fires a {@link ApocalypseEvent.Started} event and posts it on the {@link MinecraftForge#EVENT_BUS} bus.
     *
     * @param player       The player who has an Apocalypse event that just started.
     * @param eventId      The ID of the Apocalypse event that started.
     * @param sendToClient True if the client should be told to fire the event as well.
     */
    public static void fireApocalypseEventStarted( boolean sendToClient, Player player, int eventId ) {
        MinecraftForge.EVENT_BUS.post( new ApocalypseEvent.Started( player, eventId ) );
        if( sendToClient && player instanceof ServerPlayer serverPlayer ) {
            NetworkHelper.sendEventStarted( serverPlayer, eventId );
        }
    }
    
    /**
     * Fires a {@link ApocalypseEvent.Ending} event and posts it on the {@link MinecraftForge#EVENT_BUS} bus.
     *
     * @param player  The player who has en Apocalypse event that is ending.
     * @param eventId The ID of the Apocalypse event that is ending.
     * @return True if the event was canceled.
     */
    public static boolean fireApocalypseEventEnding( Player player, int eventId ) {
        return MinecraftForge.EVENT_BUS.post( new ApocalypseEvent.Ending( player, eventId ) );
    }
    
    /**
     * Fires a {@link ApocalypseEvent.Ended} event and posts it on the {@link MinecraftForge#EVENT_BUS} bus.
     *
     * @param player       The player who has an Apocalypse event that just ended.
     * @param eventId      The ID of the Apocalypse event that ended.
     * @param sendToClient True if the client should be told to fire the event as well.
     */
    public static void fireApocalypseEventEnded( boolean sendToClient, Player player, int eventId ) {
        MinecraftForge.EVENT_BUS.post( new ApocalypseEvent.Ended( player, eventId ) );
        if( sendToClient && player instanceof ServerPlayer serverPlayer ) {
            NetworkHelper.sendEventEnded( serverPlayer, eventId );
        }
    }
    
    
    // Utility class
    private ApocalypseEventFactory() { }
}
