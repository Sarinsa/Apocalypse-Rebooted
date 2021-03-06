package com.toast.apocalypse.common.entity.living.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.TargetGoal;

public class FullMoonMobTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {


    public FullMoonMobTargetGoal(MobEntity mobEntity, Class<T> targetClass, boolean mustSee) {
        super(mobEntity, targetClass, mustSee);
    }

    @Override
    public boolean canUse() {
        return false;
    }
}
