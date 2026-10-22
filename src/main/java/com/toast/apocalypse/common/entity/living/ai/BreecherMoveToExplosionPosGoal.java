package com.toast.apocalypse.common.entity.living.ai;

import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.entity.living.Breecher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;

/**
 * A goal for {@link Breecher breechers} that looks for nearby BOIs (blocks of interest) to blow up if
 * the current siege target cannot be reached. This goal does not run if the breecher does not currently
 * have a lunar siege target.
 */
// TODO - The goal logic is pretty weird right now. The trigger should probably instead be
//        to check if the breecher's path navigation is still running but unable to move the breecher.
public class BreecherMoveToExplosionPosGoal extends MoveToBlockGoal {
    
    /** The owner of this goal. */
    private final Breecher breecher;
    /** How many times the goal owner's path finding system has recalculated a path since this goal started running. */
    private int timesPathRecalc;
    
    
    /**
     * Constructs a new instance.
     *
     * @param breecher        The Breecher entity that owns this goal.
     * @param speedMul        The movement speed modifier this goal uses when making the goal owner move.
     * @param horizontalRange The horizontal search distance in which the goal owner will look for a valid explosion position.
     * @param verticalRange   The vertical search distance in which the goal owner will look for a valid explosion position.
     */
    public BreecherMoveToExplosionPosGoal( Breecher breecher, double speedMul, int horizontalRange, int verticalRange ) {
        super( breecher, speedMul, horizontalRange, verticalRange );
        this.breecher = breecher;
    }
    
    
    /** @return True if this goal can start running. */
    @Override
    public boolean canUse() {
        LivingEntity siegeTarget = breecher.getSiegeTarget();
        if( siegeTarget == null ) return false;
        boolean canSeeTarget = breecher.getTarget() == siegeTarget && breecher.isViewUnobstructed( siegeTarget );
        return super.canUse() && !canSeeTarget;
    }
    
    /** @return True if this goal has started and should continue running. */
    @Override
    public boolean canContinueToUse() {
        boolean canSeeTarget = breecher.getTarget() != null && breecher.isViewUnobstructed( breecher.getTarget() );
        return super.canContinueToUse() && !canSeeTarget;
    }
    
    /** Called when this goal has just started running. */
    @Override
    public void start() {
        super.start();
        timesPathRecalc = 0;
    }
    
    /** Called to update this goal's logic. */
    @Override
    public void tick() {
        super.tick();
        if( shouldRecalculatePath() ) ++timesPathRecalc;
        if( isReachedTarget() || timesPathRecalc > 8 ) breecher.forceSwell();
    }
    
    /**
     * @return True if the given block position is considered a valid explosion target for breechers.
     * Returns false otherwise. Also, if the checked position is too far away from the breecher's siege target,
     * this returns false.
     */
    @Override
    protected boolean isValidTarget( LevelReader level, BlockPos pos ) {
        Player siegeTarget = breecher.getSiegeTarget();
        if( siegeTarget == null ) return false;
        
        if( ApocalypseConfig.ENTITIES.FULL_MOON.breecherExplosionTargets.contains( level.getBlockState( pos ) ) ) {
            double dist = breecher.distanceToSqr( siegeTarget.getX(), siegeTarget.getY(), siegeTarget.getZ() );
            return dist < 700.0D;
        }
        return false;
    }
    
    /**
     * @return The minimum distance the goal owner must be from the target
     * explosion position for this goal to consider the position reached.
     */
    @Override
    public double acceptedDistance() {
        return 2.5D;
    }
}