package com.toast.apocalypse.common.entity.living.goals;

import com.toast.apocalypse.common.entity.living.IFullMoonMob;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.player.PlayerEntity;

public class MoonMobPlayerTargetGoal<T extends MobEntity & IFullMoonMob> extends TargetGoal {

    private final T moonMob;

    public MoonMobPlayerTargetGoal(T mobEntity, boolean mustSee) {
        super(mobEntity, mustSee);
        this.moonMob = mobEntity;
    }

    @Override
    public boolean canUse() {
        PlayerEntity player = this.moonMob.getPlayerTarget();
        return player != null && player.isAlive() && !player.isCreative() && !player.isSpectator();
    }

    public void start() {
        this.mob.setTarget(this.moonMob.getPlayerTarget());
        super.start();
    }
}
