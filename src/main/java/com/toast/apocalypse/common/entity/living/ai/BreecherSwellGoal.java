package com.toast.apocalypse.common.entity.living.ai;

import com.toast.apocalypse.common.entity.living.Breecher;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * A modified copy-paste of {@link net.minecraft.world.entity.ai.goal.SwellGoal} for {@link Breecher breechers}
 * that forces swelling and ignores other checks if the Breecher's {@link Breecher#FORCE_SWELL} flag is true.
 */
public class BreecherSwellGoal extends Goal {
    
    /** The owner of this goal. */
    private final Breecher breecher;
    /** The current target of the goal owner. */
    @Nullable
    private LivingEntity target;
    
    
    /**
     * Constructs a new instance.
     *
     * @param breecher The Breecher entity that owns this goal.
     */
    public BreecherSwellGoal( Breecher breecher ) {
        this.breecher = breecher;
        setFlags( EnumSet.of( Goal.Flag.MOVE ) );
    }
    
    
    /** @return True if this goal can start running. */
    @Override
    public boolean canUse() {
        LivingEntity target = breecher.getTarget();
        return breecher.shouldForceSwell() || (breecher.getSwellDir() > 0 || target != null && breecher.distanceToSqr( target ) < 9.0D);
    }
    
    /** Called when this goal has just started running. */
    @Override
    public void start() {
        breecher.getNavigation().stop();
        target = breecher.getTarget();
    }
    
    /** Called when this goal stops running. */
    @Override
    public void stop() {
        target = null;
    }
    
    /** @return True if this goal requires its logic to be updated every tick. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
    
    /** Called to update this goal's logic. */
    @Override
    public void tick() {
        if( breecher.shouldForceSwell() ) {
            breecher.setSwellDir( 1 );
            return;
        }
        if( target == null || breecher.distanceToSqr( target ) > 49.0D || !breecher.getSensing().hasLineOfSight( target ) ) {
            breecher.setSwellDir( -1 );
        }
        else breecher.setSwellDir( 1 );
    }
}
