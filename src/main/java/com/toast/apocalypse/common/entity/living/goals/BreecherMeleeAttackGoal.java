package com.toast.apocalypse.common.entity.living.goals;

import com.toast.apocalypse.common.entity.living.BreecherEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class BreecherMeleeAttackGoal extends MeleeAttackGoal {

    public BreecherMeleeAttackGoal(BreecherEntity breecher, double speed, boolean mustSee) {
        super(breecher, speed, mustSee);
    }
}
