package com.toast.apocalypse.common.entity.living.ai;

import com.toast.apocalypse.common.entity.living.Fearwolf;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

/**
 * A goal for {@link Fearwolf fearwolves} that attempts to generate an "escape path"
 * for the fearwolf to flee from its attacker when it gets hurt.
 */
// TODO - Track if the fearwolf is still moving along the escape path
//        when the goal ticks, so we can pop the fearwolf out of invis when appropriate
public class FearwolfRunAwayGoal extends Goal {
    
    /** The owner of this goal. */
    private final Fearwolf fearwolf;
    /** The movement speed modifier this goal uses when making the goal owner move. */
    private final double speedMul;
    /** The "escape" path created by this goal that the goal owner should take to flee. */
    private Path escapePath;
    
    
    /**
     * Creates a new instance.
     *
     * @param fearwolf The Fearwolf entity that owns this goal.
     * @param speedMul The movement speed modifier this goal uses when making the goal owner move.
     */
    public FearwolfRunAwayGoal( Fearwolf fearwolf, double speedMul ) {
        this.fearwolf = fearwolf;
        this.speedMul = speedMul;
    }
    
    
    /** @return True if this goal requires its logic to be updated every tick. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
    
    /** @return True if this goal can start running. */
    @Override
    public boolean canUse() {
        if( fearwolf.runningAway() && fearwolf.isAlive() && !fearwolf.isVehicle() && !fearwolf.isPassenger() ) {
            Vec3 vec3 = DefaultRandomPos.getPosAway( fearwolf, 16, 7, fearwolf.position() );
            
            if( vec3 == null ) {
                return false;
            }
            else {
                escapePath = fearwolf.getNavigation().createPath( vec3.x, vec3.y, vec3.z, 0 );
                return escapePath != null;
            }
        }
        return false;
    }
    
    /** @return True if this goal has started and should continue running. */
    @Override
    public boolean canContinueToUse() {
        return fearwolf.isAlive() && !fearwolf.isVehicle() && !fearwolf.isPassenger()
                && fearwolf.getNavigation().isDone();
    }
    
    /** Called when this goal has just started running. */
    @Override
    public void start() {
        // noinspection resource
        if( !fearwolf.level().isClientSide ) {
            ServerLevel level = (ServerLevel) fearwolf.level();
            spawnSmoke( level, fearwolf );
        }
        fearwolf.addEffect( new MobEffectInstance( MobEffects.INVISIBILITY, 150, 0 ) );
        fearwolf.getNavigation().moveTo( escapePath, speedMul );
        fearwolf.setRunningAway( false );
    }
    
    /** Convenience method for spawning smoke particles at the given mob's position. */
    private static void spawnSmoke( ServerLevel level, Mob mob ) {
        for( int i = 0; i < 15; i++ ) {
            level.sendParticles( ParticleTypes.SMOKE, mob.getX(), mob.getY(), mob.getZ(),
                    4, 0.1, 0.1, 0.1, 0.1 );
        }
    }
}
