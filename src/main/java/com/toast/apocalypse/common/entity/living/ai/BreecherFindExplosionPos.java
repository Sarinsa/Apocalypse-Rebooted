package com.toast.apocalypse.common.entity.living.ai;

import com.toast.apocalypse.common.entity.living.Breecher;
import com.toast.apocalypse.common.tag.ApocalypseBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;

public class BreecherFindExplosionPos extends MoveToBlockGoal {

    private final Breecher breecher;
    private int timesPathRecalc;

    public BreecherFindExplosionPos(Breecher breecher, double speed, int range, int verticalRange) {
        super(breecher, speed, range, verticalRange);
        this.breecher = breecher;
    }

    @Override
    public boolean canUse() {
        boolean canSeeTarget = breecher.getTarget() != null && breecher.canSeeDirectly(breecher.getTarget());
        return super.canUse() && !canSeeTarget;
    }

    @Override
    public boolean canContinueToUse() {
        boolean canSeeTarget = breecher.getTarget() != null && breecher.canSeeDirectly(breecher.getTarget());
        return super.canContinueToUse() && !canSeeTarget;
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        if (breecher.getPlayerTargetUUID() != null && (breecher.getTarget() instanceof Player player && player.getUUID() == breecher.getPlayerTargetUUID())) {
            if (level.getBlockState(pos).is(ApocalypseBlockTags.BREECHER_TARGETS)) {
                double dist = breecher.distanceToSqr(player.getX(), player.getY(), player.getZ());
                return dist < 700.0D;
            }
            return false;
        }
        return false;
    }

    @Override
    public double acceptedDistance() {
        return 2.5D;
    }

    @Override
    public void start() {
        super.start();
        timesPathRecalc = 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (shouldRecalculatePath())
            ++timesPathRecalc;

        if (isReachedTarget() || timesPathRecalc > 8) {
            breecher.forceSwell();
        }
    }
}
