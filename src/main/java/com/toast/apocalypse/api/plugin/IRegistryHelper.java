package com.toast.apocalypse.api.plugin;

import com.toast.apocalypse.api.TriConsumer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;

/**
 * All sub-helpers and registration methods
 * for a mod plugin are present here.
 */
public interface IRegistryHelper {

    /**
     * If wanted, additional logic for an entity being alerted by a Seeker
     * can be registered here.
     *
     * @param alertable The class of the entity that should be alertable by the seeker.
     * @param triConsumer A TriConsumer<A, B, C> handling the additional logic.
     *
     * @param <ENTITY>> The entity that should be alerted.
     * @param <TARGET>> The seeker's current target.
     * @param <SEEKER>> The seeker that is currently alerting other mobs.
     */
    <ENTITY extends LivingEntity, TARGET extends LivingEntity, SEEKER extends MobEntity> void registerSeekerAlertable(Class<ENTITY> alertable, TriConsumer<ENTITY, TARGET, SEEKER> triConsumer);
}
