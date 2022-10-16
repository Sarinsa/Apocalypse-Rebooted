package com.toast.apocalypse.common.entity.living.goals;

import com.toast.apocalypse.common.entity.living.FearwolfEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.Path;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class FearwolfRunAwayGoal extends Goal {

    private final FearwolfEntity fearwolf;
    private final double speedMul;
    private Path path;

    public FearwolfRunAwayGoal(FearwolfEntity fearwolf, double speedMul) {
        this.fearwolf = fearwolf;
        this.speedMul = speedMul;
    }

    @Override
    public boolean canUse() {
        if (fearwolf.runningAway() && fearwolf.isAlive() && !fearwolf.isVehicle() && !fearwolf.isPassenger()) {
            Vector3d vector3d = RandomPositionGenerator.getPosAvoid(fearwolf, 16, 7, fearwolf.position());

            if (vector3d == null) {
                return false;
            }
            else {
                path = fearwolf.getNavigation().createPath(vector3d.x, vector3d.y, vector3d.z, 0);
                return path != null;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return fearwolf.isAlive() && !fearwolf.isVehicle() && !fearwolf.isPassenger()
                && fearwolf.getNavigation().isDone();
    }

    @Override
    public void start() {
        if (!fearwolf.level.isClientSide) {
            ServerWorld world = (ServerWorld) fearwolf.level;
            spawnSmoke(world, fearwolf);
        }
        fearwolf.addEffect(new EffectInstance(Effects.INVISIBILITY, 150, 0));
        fearwolf.getNavigation().moveTo(path, speedMul);
        fearwolf.setRunningAway(false);
    }

    private static void spawnSmoke(ServerWorld world, MobEntity mob) {
        for (int i = 0; i < 15; i++) {
            world.sendParticles(ParticleTypes.SMOKE, mob.getX(), mob.getY(), mob.getZ(), 4, 0.1, 0.1, 0.1, 0.1);
        }
    }
}
