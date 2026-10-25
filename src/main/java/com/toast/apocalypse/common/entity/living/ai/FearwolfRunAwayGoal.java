package com.toast.apocalypse.common.entity.living.ai;

import com.toast.apocalypse.common.entity.living.Fearwolf;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * A goal for {@link Fearwolf fearwolves} that attempts to generate an "escape path"
 * for the fearwolf to flee from its attacker when it gets hurt.
 */
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
        setFlags( EnumSet.of( Goal.Flag.MOVE ) );
    }
    
    
    /** @return True if this goal requires its logic to be updated every tick. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
    
    /** @return True if this goal can start running. */
    @Override
    public boolean canUse() {
        if( fearwolf.isAlive() && !fearwolf.isVehicle() && !fearwolf.isPassenger() &&
                fearwolf.getLastHurtByMob() instanceof Player player && timeSinceHurt() < 20 ) {
            final Vec3 pos = DefaultRandomPos.getPosAway( fearwolf, 16, 7, player.position() );
            // Check if we were even able to generate a valid position
            if( pos == null ) {
                return false;
            }
            // Make sure the destination isn't closer to the player than
            // the goal owner's current position
            if( player.distanceToSqr( pos.x, pos.y, pos.z ) < player.distanceToSqr( fearwolf ) ) {
                return false;
            }
            // Attempt to create a path to the destination
            else {
                escapePath = fearwolf.getNavigation().createPath( pos.x, pos.y, pos.z, 0 );
                return escapePath != null;
            }
        }
        return false;
    }
    
    /** @return True if this goal has started and should continue running. */
    @Override
    public boolean canContinueToUse() {
        return fearwolf.isAlive() && !fearwolf.isVehicle() && !fearwolf.isPassenger() && !fearwolf.getNavigation().isDone();
    }
    
    /** Called when this goal has just started running. */
    @Override
    public void start() {
        if( fearwolf.getNavigation().moveTo( escapePath, speedMul ) ) {
            fearwolf.addEffect( new MobEffectInstance( MobEffects.INVISIBILITY, 150, 0 ) );
            ServerLevel level = (ServerLevel) fearwolf.level();
            spawnSmoke( level, fearwolf );
        }
    }
    
    /** Called when this goal stops running. */
    @Override
    public void stop() {
        fearwolf.removeEffect( MobEffects.INVISIBILITY );
    }
    
    /** @return How many ticks have passed since the goal owner was last hurt by a mob. */
    private int timeSinceHurt() {
        return Math.abs( fearwolf.tickCount - fearwolf.getLastHurtByMobTimestamp() );
    }
    
    /** Convenience method for spawning smoke particles at the given mob's position. */
    private static void spawnSmoke( ServerLevel level, Mob mob ) {
        for( int i = 0; i < 15; i++ ) {
            level.sendParticles( ParticleTypes.SMOKE, mob.getX(), mob.getY(), mob.getZ(),
                    4, 0.1, 0.1, 0.1, 0.1 );
        }
    }
}
