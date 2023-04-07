package com.toast.apocalypse.common.entity.living.ai;

import com.toast.apocalypse.common.entity.living.Breecher;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class BreecherSwellGoal extends Goal {
    private final Breecher breecher;
    @Nullable
    private LivingEntity target;

    public BreecherSwellGoal(Breecher breecher) {
        this.breecher = breecher;setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = breecher.getTarget();
        return breecher.shouldForceSwell() || (breecher.getSwellDir() > 0 || target != null && breecher.distanceToSqr(target) < 9.0D);
    }

    @Override
    public void start() {
        breecher.getNavigation().stop();
        target = breecher.getTarget();
    }

    @Override
    public void stop() {
        target = null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (breecher.shouldForceSwell()) {
            breecher.setSwellDir(1);
            return;
        }

        if (target == null) {
            breecher.setSwellDir(-1);
        }
        else if (breecher.distanceToSqr(target) > 49.0D) {
            breecher.setSwellDir(-1);
        }
        else if (!breecher.getSensing().hasLineOfSight(target)) {
            breecher.setSwellDir(-1);
        }
        else {
            breecher.setSwellDir(1);
        }
    }
}
