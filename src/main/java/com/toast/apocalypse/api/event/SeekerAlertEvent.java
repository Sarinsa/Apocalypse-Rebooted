package com.toast.apocalypse.api.event;

import com.toast.apocalypse.common.entity.living.Seeker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

/**
 * Fired when a Seeker attempts to alert nearby entities of its target player, server-side only.
 * <br><br>
 * This event is fired from {@link Seeker.AlertOtherMonstersGoal#start()} and is
 * posted on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 * <br><br>
 * This event is {@link Cancelable}. If the event is canceled, the Seeker responsible
 * for firing it will not alert any nearby mobs, and will also not play the alert sfx.
 * <br><br>
 * The {@link #toAlert} list can be safely modified.
 */
@Cancelable
public final class SeekerAlertEvent extends Event {
    
    /** The Seeker entity that is trying to alert nearby mobs. */
    private final Mob seeker;
    /** A modifiable list of mobs that should be alerted by the Seeker. */
    private final List<? extends Mob> toAlert;
    /** The target that the Seeker is alerting nearby mobs of. */
    private final LivingEntity target;
    /** The level where the alert event is taking place. */
    private final Level level;
    
    
    public SeekerAlertEvent( Level level, Mob seeker, List<? extends Mob> toAlert, LivingEntity target ) {
        this.level = level;
        this.seeker = seeker;
        this.toAlert = toAlert;
        this.target = target;
    }
    
    public Mob getSeeker() {
        return seeker;
    }
    
    public List<? extends Mob> getToAlert() {
        return toAlert;
    }
    
    public LivingEntity getTarget() {
        return target;
    }
    
    public Level getLevel() {
        return level;
    }
}
