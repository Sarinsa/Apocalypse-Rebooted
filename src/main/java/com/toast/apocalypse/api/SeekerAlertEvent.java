package com.toast.apocalypse.api;

import com.toast.apocalypse.common.entity.living.Seeker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Fired when a Seeker attempts to alert nearby entities of its target player.<br>
 * <br>
 *
 * This event is fired from {@link Seeker.AlertOtherMonstersGoal#start()}<br>
 * <br>
 *
 * {@link #seeker} is the Seeker entity that is trying to alert an entity.<br>
 * {@link #toAlert} is a List of mob entities the Seeker is trying to alert.<br>
 * {@link #target} is the Seeker's current target, usually a player.<br>
 * {@link #level} is the Seeker's World object.<br>
 * <br>
 *
 * This event is not {@link Cancelable}<br>
 * <br>
 * The {@link #toAlert} List is modifiable, and mobs can be both added and removed.<br>
 * <br>
 */
public final class SeekerAlertEvent extends Event {

    private final Mob seeker;
    private final List<? extends Mob> toAlert;
    private final LivingEntity target;
    private final Level level;

    public SeekerAlertEvent(Level level, @Nonnull Mob seeker, @Nonnull List<? extends Mob> toAlert, @Nonnull LivingEntity target) {
        this.seeker = seeker;
        this.toAlert = toAlert;
        this.target = target;
        this.level = level;
    }

    public Mob getSeeker() {
        return this.seeker;
    }

    public List<? extends Mob> getToAlert() {
        return this.toAlert;
    }

    public LivingEntity getTarget() {
        return this.target;
    }

    public Level getLevel() {
        return this.level;
    }
}
