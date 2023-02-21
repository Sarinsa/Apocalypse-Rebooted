package com.toast.apocalypse.common.entity.living.ai;

import com.toast.apocalypse.common.entity.living.Breecher;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class BreecherMeleeAttackGoal extends MeleeAttackGoal {

    public BreecherMeleeAttackGoal(Breecher breecher, double speed, boolean mustSee) {
        super(breecher, speed, mustSee);
    }
}
