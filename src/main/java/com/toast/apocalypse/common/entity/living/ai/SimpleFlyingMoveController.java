package com.toast.apocalypse.common.entity.living.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SimpleFlyingMoveController extends MoveControl {
    
    protected int floatDuration;
    
    public SimpleFlyingMoveController( FlyingMob flyingMob ) {
        super( flyingMob );
    }
    
    @Override
    public void setWantedPosition( double x, double y, double z, double speedMod ) {
        super.setWantedPosition( x, y, z, speedMod );
    }
    
    public void setAction( Operation operation ) {
        this.operation = operation;
    }
    
    
    @Override
    public void tick() {
        if( operation == Operation.MOVE_TO ) {
            if( floatDuration-- <= 0 ) {
                floatDuration += mob.getRandom().nextInt( 5 ) + 2;
                Vec3 moveVec = new Vec3(
                        wantedX - mob.getX(),
                        wantedY - mob.getY(),
                        wantedZ - mob.getZ() );
                final int distance = Mth.ceil( moveVec.length() );
                moveVec = moveVec.normalize();
                
                if( mob.getRandom().nextBoolean() || canReach( moveVec, distance ) ) {
                    mob.setDeltaMovement( mob.getDeltaMovement().add( moveVec.scale( getScaledMoveSpeed() ) ) );
                }
                else {
                    operation = Operation.WAIT;
                }
            }
        }
    }
    
    public double getScaledMoveSpeed() {
        return 0.1 * speedModifier * mob.getAttributeValue( Attributes.MOVEMENT_SPEED ) / Attributes.MOVEMENT_SPEED.getDefaultValue();
    }
    
    public boolean canReachCurrentWanted() {
        return hasWanted() && canReachPosition( getWantedX(), getWantedY(), getWantedZ() );
    }
    
    public boolean canReachPosition( double x, double y, double z ) {
        final Vec3 targetVec = new Vec3( x - mob.getX(), y - mob.getY(), z - mob.getZ() );
        final int distance = Mth.ceil( targetVec.length() );
        return canReach( targetVec.normalize(), distance );
    }
    
    protected boolean canReach( Vec3 direction, int distance ) {
        AABB boundingBox = mob.getBoundingBox();
        
        for( int i = 1; i < distance; i++ ) {
            boundingBox = boundingBox.move( direction );
            
            // noinspection resource
            if( !mob.level().noCollision( mob, boundingBox ) ) return false;
        }
        return true;
    }
}

