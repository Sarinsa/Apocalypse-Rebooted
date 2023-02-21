package com.toast.apocalypse.common.entity.living.ai;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class FullMoonMobTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {


    public FullMoonMobTargetGoal(Mob mob, Class<T> targetClass, boolean mustSee) {
        super(mob, targetClass, mustSee);
    }

    @Override
    public boolean canUse() {
        return false;
    }
}
