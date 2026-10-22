package com.toast.apocalypse.common.entity.living.ai;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

/**
 * A modified copy-paste of {@link net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal}
 * that can be used by non-pathfinder mobs.
 */
@SuppressWarnings( "resource" )
public class MobHurtByTargetGoal extends TargetGoal {
    
    /**
     * The targeting conditions used to check if the goal owner's
     * "last hurt by" entity can be attacked.
     */
    private static final TargetingConditions TARGET_CONDITIONS = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    
    /**
     * If true, this goal will make other nearby mobs of the same type as the goal owner
     * target the entity that last damaged the goal owner.
     */
    private boolean alertSameType;
    /**
     * A timestamp of when this goal's owner last got hurt by another entity.
     * This is assigned when this goal starts, in {@link #start()}.
     */
    private int timeLastHurt;
    /** Classes of entities that the owner of this goal should not aggro on if damaged by. */
    private final Class<?>[] toIgnoreDamage;
    /**
     * A list of classes for entities that should not be alerted by this goal.
     * If this field is null, this goal's "alert-others" logic will not run.
     */
    @Nullable
    private List<Class<?>> toIgnoreAlert;
    
    
    /**
     * Creates a new instance.
     *
     * @param mob            The owner of this goal.
     * @param toIgnoreDamage Classes of entities that the owner of this goal should not aggro on if damaged by.
     */
    public MobHurtByTargetGoal( Mob mob, Class<?>... toIgnoreDamage ) {
        super( mob, true );
        this.toIgnoreDamage = toIgnoreDamage;
        setFlags( EnumSet.of( Goal.Flag.TARGET ) );
    }
    
    /** @return True if this goal can start running. */
    @Override
    public boolean canUse() {
        int timeLastHurt = mob.getLastHurtByMobTimestamp();
        LivingEntity lastAttacker = mob.getLastHurtByMob();
        
        if( timeLastHurt != this.timeLastHurt && lastAttacker != null ) {
            // Players are ignored if "universal anger" is enabled
            if( lastAttacker.getType() == EntityType.PLAYER && mob.level().getGameRules().getBoolean( GameRules.RULE_UNIVERSAL_ANGER ) ) {
                return false;
            }
            else {
                for( Class<?> clazz : toIgnoreDamage ) {
                    if( clazz.isAssignableFrom( lastAttacker.getClass() ) ) {
                        return false;
                    }
                }
                return canAttack( lastAttacker, TARGET_CONDITIONS );
            }
        }
        return false;
    }
    
    /** Called when this goal has just started running. */
    @Override
    public void start() {
        mob.setTarget( mob.getLastHurtByMob() );
        targetMob = mob.getTarget();
        timeLastHurt = mob.getLastHurtByMobTimestamp();
        unseenMemoryTicks = 300;
        
        if( alertSameType ) alertOthers();
        
        super.start();
    }
    
    /**
     * Enables "alert others" logic for this goal. When enabled, mobs of the same type as this goal's owner
     * will aggro the entity that last hurt it.
     * <br><br>
     * This is designed to be a "builder style" method, and should be called before this goal is
     * added to a mob's goal selector, like so:
     * {@code new MobHurtByTargetGoal(...).setAlertOthers(...)}.
     *
     * @param ignoresAlerts A list of entity classes that should not be alerted by this goal.
     *                      If this list is empty, all mobs, regardless of type, will be alerted.
     */
    public MobHurtByTargetGoal setAlertOthers( List<Class<?>> ignoresAlerts ) {
        alertSameType = true;
        toIgnoreAlert = ignoresAlerts;
        return this;
    }
    
    /** Iterates through nearby mobs and checks each mob if it should be alerted. */
    protected void alertOthers() {
        final double followDistance = getFollowDistance();
        final AABB alertArea = AABB.unitCubeFromLowerCorner( mob.position() ).inflate( followDistance, 10.0D, followDistance );
        final List<? extends Mob> mobs = mob.level().getEntitiesOfClass( mob.getClass(), alertArea, EntitySelector.NO_SPECTATORS );
        
        for( Mob toAlert : mobs ) {
            if( shouldAlertEntity( toAlert ) ) {
                if( toIgnoreAlert != null && toIgnoreAlert.contains( toAlert.getClass() ) )
                    continue;
                toAlert.setTarget( mob.getLastHurtByMob() );
            }
        }
    }
    
    /** @return True if the given mob can be alerted. */
    private boolean shouldAlertEntity( Mob toAlert ) {
        LivingEntity lastHurtBy = toAlert.getLastHurtByMob();
        
        return mob != toAlert && toAlert.getTarget() == null
                && (!(mob instanceof TamableAnimal) || ((TamableAnimal) mob).getOwner() == ((TamableAnimal) toAlert).getOwner())
                && (lastHurtBy != null && !toAlert.isAlliedTo( lastHurtBy ));
    }
}
