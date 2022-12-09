package com.toast.apocalypse.common.entity.living.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;

public class FullMoonMobTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {


    public FullMoonMobTargetGoal(MobEntity mobEntity, Class<T> targetClass, boolean mustSee) {
        super(mobEntity, targetClass, mustSee);
    }

    @Override
    public boolean canUse() {
        return false;
    }
}
