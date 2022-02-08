package com.toast.apocalypse.api;

import com.toast.apocalypse.common.entity.living.SeekerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Fired when a Seeker attempts to alert nearby entities of its target player.<br>
 * <br>
 *
 * This event is fired from {@link SeekerEntity.AlertOtherMonstersGoal#start()}<br>
 * <br>
 *
 * {@link #seeker} is the Seeker entity that is trying to alert an entity.<br>
 * {@link #toAlert} is a List of mob entities the Seeker is trying to alert.<br>
 * {@link #target} is the Seeker's current target, usually a player.<br>
 * {@link #world} is the Seeker's World object.<br>
 * <br>
 *
 * This event is not {@link Cancelable}<br>
 * <br>
 * The {@link #toAlert} List is modifiable, and mobs can be both added and removed.<br>
 * <br>
 */
public final class SeekerAlertEvent extends Event {

    private final MobEntity seeker;
    private final List<MobEntity> toAlert;
    private final LivingEntity target;
    private final World world;

    public SeekerAlertEvent(World world, @Nonnull MobEntity seeker, @Nonnull List<MobEntity> toAlert, @Nonnull LivingEntity target) {
        this.seeker = seeker;
        this.toAlert = toAlert;
        this.target = target;
        this.world = world;
    }

    public MobEntity getSeeker() {
        return this.seeker;
    }

    public List<MobEntity> getToAlert() {
        return this.toAlert;
    }

    public LivingEntity getTarget() {
        return this.target;
    }

    public World getLevel() {
        return this.world;
    }
}
