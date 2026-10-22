package com.toast.apocalypse.common.entity.living.ai;

import com.toast.apocalypse.api.entity.ILunarSiegeMob;
import com.toast.apocalypse.common.entity.living.AbstractLunarSiegeGhast;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;

/**
 * A target goal for mobs spawned by lunar sieges. Attempts to make the goal owner
 * focus on their designated siege target if possible, ignoring "last hurt by".
 */
public class LunarSiegeTargetPlayerGoal<T extends Mob & ILunarSiegeMob<T>> extends TargetGoal {
    
    /** The owner of this goal. */
    private final T siegeMob;
    
    
    /**
     * Creates a new instance.
     *
     * @param siegeMob The Lunar Siege mob that owns this goal.
     * @param mustSee  True if the goal owner requires line of sight to its siege target in order for this goal to run.
     */
    public LunarSiegeTargetPlayerGoal( T siegeMob, boolean mustSee ) {
        super( siegeMob, mustSee );
        this.siegeMob = siegeMob;
    }
    
    
    /** @return True if this goal can start running. */
    @Override
    public boolean canUse() {
        Player siegeTarget = siegeMob.getSiegeTarget();
        if( siegeTarget == null ) return false;
        
        // Prevent tamed siege mobs from attacking their owners
        if( siegeMob instanceof OwnableEntity ownable ) {
            if( ownable.getOwnerUUID() != null ) return false;
        }
        // Check whether direct line of sight is required
        if( mustSee ) {
            if( mob instanceof AbstractLunarSiegeGhast ) {
                if( !((AbstractLunarSiegeGhast) siegeMob).isViewUnobstructed( siegeTarget ) )
                    return false;
            }
            else if( !mob.getSensing().hasLineOfSight( siegeTarget ) )
                return false;
        }
        return siegeTarget.isAlive() && !siegeTarget.isCreative() && !siegeTarget.isSpectator();
    }
    
    /** Called when this goal has just started running. */
    @Override
    public void start() {
        LivingEntity target = siegeMob.getTarget();
        Player siegeTarget = siegeMob.getSiegeTarget();
        siegeMob.setTarget( siegeTarget != null ? siegeTarget : target );
        super.start();
    }
}
