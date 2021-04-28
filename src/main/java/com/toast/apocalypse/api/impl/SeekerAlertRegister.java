package com.toast.apocalypse.api.impl;

import com.toast.apocalypse.api.TriConsumer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;

import javax.annotation.Nullable;
import java.util.HashMap;

public class SeekerAlertRegister {

    private final HashMap<Class<? extends LivingEntity>, TriConsumer<?, ?, ?>> alertBehaviors = new HashMap<>();

    protected SeekerAlertRegister() {

    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <A extends LivingEntity, B extends LivingEntity, C extends MobEntity> TriConsumer<A, B, C> getFromEntity(Class<? extends LivingEntity> entityClass) {
        return (TriConsumer<A, B, C>) this.alertBehaviors.get(entityClass);
    }

    public boolean containsEntry(Class<? extends LivingEntity> entityClass) {
        return this.alertBehaviors.containsKey(entityClass);
    }

    public <A extends LivingEntity, B extends LivingEntity, C extends MobEntity> void addEntry(Class<A> entityClass, TriConsumer<A, B, C> logic) {
        this.alertBehaviors.put(entityClass, logic);
    }
}
