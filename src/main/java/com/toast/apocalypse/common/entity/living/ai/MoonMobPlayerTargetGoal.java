package com.toast.apocalypse.common.entity.living.ai;

import com.toast.apocalypse.common.entity.living.AbstractFullMoonGhastEntity;
import com.toast.apocalypse.common.entity.living.GrumpEntity;
import com.toast.apocalypse.common.entity.living.IFullMoonMob;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class MoonMobPlayerTargetGoal<T extends MobEntity & IFullMoonMob> extends TargetGoal {

    private final T moonMob;

    public MoonMobPlayerTargetGoal(T mobEntity, boolean mustSee) {
        super(mobEntity, mustSee);
        moonMob = mobEntity;
    }

    @Override
    public boolean canUse() {
        UUID playerTargetUUID = moonMob.getPlayerTargetUUID();

        if (playerTargetUUID == null)
            return false;

        PlayerEntity player = moonMob.level.getPlayerByUUID(playerTargetUUID);

        if (player == null)
            return false;

        if (moonMob instanceof GrumpEntity) {
            GrumpEntity grump = (GrumpEntity) moonMob;

            if (grump.hasOwner())
                return false;
        }

        if (mustSee) {
            if (mob instanceof AbstractFullMoonGhastEntity) {
                if (!((AbstractFullMoonGhastEntity) moonMob).canSeeDirectly(player))
                    return false;
            }
            else if (!mob.getSensing().canSee(player))
                return false;
        }
        return player.isAlive() && !player.isCreative() && !player.isSpectator();
    }

    @SuppressWarnings("ConstantConditions")
    public void start() {
        moonMob.setTarget(moonMob.level.getPlayerByUUID(moonMob.getPlayerTargetUUID()));
        super.start();
    }
}
