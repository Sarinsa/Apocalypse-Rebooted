package com.toast.apocalypse.common.entity.living.goals;

import com.toast.apocalypse.common.entity.living.IFullMoonMob;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class MoonMobPlayerTargetGoal<T extends MobEntity & IFullMoonMob> extends TargetGoal {

    private final T moonMob;

    public MoonMobPlayerTargetGoal(T mobEntity, boolean mustSee) {
        super(mobEntity, mustSee);
        this.moonMob = mobEntity;
    }

    @Override
    public boolean canUse() {
        UUID playerTargetUUID = this.moonMob.getPlayerTargetUUID();

        if (playerTargetUUID == null)
            return false;

        if (this.moonMob.level instanceof ServerWorld) {
            ServerWorld world = (ServerWorld) moonMob.level;

            PlayerEntity player = world.getServer().getPlayerList().getPlayer(playerTargetUUID);

            if (player == null)
                return false;

            if (this.mustSee) {
                if (!this.mob.getSensing().canSee(player))
                    return false;
            }

            return player.isAlive() && !player.isCreative() && !player.isSpectator();
        }
        return false;
    }

    public void start() {
        ServerWorld world = (ServerWorld) this.moonMob.level;

        this.mob.setTarget(world.getServer().getPlayerList().getPlayer(this.moonMob.getPlayerTargetUUID()));
        super.start();
    }
}
