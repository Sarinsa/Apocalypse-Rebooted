package com.toast.apocalypse.common.entity.living.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * A modified copy-paste of {@link net.minecraft.world.entity.monster.Ghast.GhastMoveControl}
 * that can manually be interrupted.
 */
@SuppressWarnings( "resource" )
public class SimpleFlyingMoveControl extends MoveControl {
    
    /** The amount of ticks to wait before picking a new destination. */
    protected int timeNextPosition;
    
    
    /**
     * Creates a new instance.
     *
     * @param flyingMob The owner of this move control.
     */
    public SimpleFlyingMoveControl( FlyingMob flyingMob ) {
        super( flyingMob );
    }
    
    
    /** Updates this move control's logic. */
    @Override
    public void tick() {
        if( operation == Operation.MOVE_TO ) {
            if( timeNextPosition-- <= 0 ) {
                timeNextPosition += mob.getRandom().nextInt( 5 ) + 2;
                Vec3 moveVec = new Vec3(
                        wantedX - mob.getX(),
                        wantedY - mob.getY(),
                        wantedZ - mob.getZ()
                );
                if( noCollision( moveVec ) ) {
                    moveVec = moveVec.normalize().scale( getScaledFlyingSpeed() );
                    mob.setDeltaMovement( mob.getDeltaMovement().add( moveVec ) );
                }
                else {
                    operation = Operation.WAIT;
                }
            }
        }
    }
    
    /**
     * Sets the current operation of this move control to either {@link Operation#WAIT} or {@link Operation#MOVE_TO},
     * depending on the value of the {@code operating} parameter.
     * <br><br>
     * In this specific move control implementation, only the operations {@link Operation#WAIT} and {@link Operation#MOVE_TO}
     * are used, to either stop or resume moving.
     */
    public void setOperating( boolean operating ) {
        operation = operating ? Operation.MOVE_TO : Operation.WAIT;
    }
    
    /**
     * Calculates the flying speed to use when moving this move control's entity.
     *
     * @return This move control's entity's flying speed attribute value scaled by {@link #speedModifier} * 0.1.
     * If the entity does not have the {@link Attributes#FLYING_SPEED} attribute, a default value of 1.0 is used instead.
     */
    public double getScaledFlyingSpeed() {
        AttributeInstance attributeInstance = mob.getAttribute( Attributes.FLYING_SPEED );
        double flyingSpeed = attributeInstance == null ? 1.0 : attributeInstance.getValue();
        return 0.1 * (speedModifier * flyingSpeed);
    }
    
    /**
     * @return True if this move control is currently active and no blocking collisions exist
     * between the move control's entity and the current destination.
     */
    public boolean canReachCurrentWanted() {
        return hasWanted() && noCollision( getWantedX(), getWantedY(), getWantedZ() );
    }
    
    /** @return True if this move control is active and the distance to the current destination is no more than {@code minDist}. */
    public boolean isWithinDistToWanted( double minDist ) {
        return hasWanted() && mob.distanceToSqr( getWantedX(), getWantedY(), getWantedZ() ) < Math.abs( minDist );
    }
    
    /**
     * Uses this move control's entity's hitbox to check if there are any collisions
     * between the entity and the target position.
     *
     * @param pos The target position.
     * @return True if no collisions are found between this move control's entity and the target position.
     * Returns false otherwise.
     */
    protected boolean noCollision( Vec3 pos ) {
        final int distance = Mth.ceil( pos.length() );
        final Vec3 normal = pos.normalize();
        AABB boundingBox = mob.getBoundingBox().inflate( 0.1 );
        
        for( int i = 1; i < distance; i++ ) {
            boundingBox = boundingBox.move( normal );
            if( !mob.level().noCollision( mob, boundingBox ) )
                return false;
        }
        return true;
    }
    
    /**
     * Calls the above {@link #noCollision(Vec3)}, using the given coordinates to
     * create a position vector.
     *
     * @return True if no collisions are found between this move control's entity and the target position.
     * Returns false otherwise.
     */
    protected boolean noCollision( double x, double y, double z ) {
        return noCollision( new Vec3( x, y, z ) );
    }
}

