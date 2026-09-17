package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.api.entity.ILunarSiegeMob;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public abstract class AbstractLunarSiegeGhast extends Ghast implements ILunarSiegeMob {
    
    protected UUID playerTargetUUID;
    
    public AbstractLunarSiegeGhast( EntityType<? extends Ghast> entityType, Level level ) {
        super( entityType, level );
    }
    
    /**
     * Used for the seeker and destroyer when shooting fireballs
     * at their target. Only X and Z distance is checked to prevent
     * players from being able to hide far underground to avoid
     * the constant barrage of explosions that is waiting for them.
     */
    public final double horizontalDistanceToSqr( Entity entity ) {
        return horizontalDistanceToSqr( entity.position() );
    }
    
    public final double horizontalDistanceToSqr( BlockPos pos ) {
        return horizontalDistanceToSqr( new Vec3( pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D ) );
    }
    
    public final double horizontalDistanceToSqr( Vec3 vec ) {
        double x = this.getX() - vec.x;
        double z = this.getZ() - vec.z;
        return x * x + z * z;
    }
    
    @Override
    public boolean canDrownInFluidType( FluidType type ) {
        return false;
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        // noinspection ConstantConditions
        return null;
    }
    
    @Override
    protected float getSoundVolume() {
        // Louder than vanilla ghast; help players realize they might be getting blasted with fireballs from afar.
        return 12.0F;
    }
    
    /**
     * Checks if this ghast type has direct
     * line of sight to the target entity.
     */
    public boolean canSeeDirectly( Entity entity ) {
        Vec3 vector3d = new Vec3( this.getX(), this.getEyeY(), this.getZ() );
        Vec3 vector3d1 = new Vec3( entity.getX(), entity.getEyeY(), entity.getZ() );
        return level().clip( new ClipContext( vector3d, vector3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this ) ).getType() == HitResult.Type.MISS;
    }
    
    @Nullable
    @Override
    public UUID getPlayerTargetUUID() {
        return this.playerTargetUUID;
    }
    
    @Override
    public void setPlayerTargetUUID( @Nullable UUID playerTargetUUID ) {
        this.playerTargetUUID = playerTargetUUID;
    }
    
    @Override
    public void addAdditionalSaveData( CompoundTag compoundTag ) {
        super.addAdditionalSaveData( compoundTag );
        
        if( this.getPlayerTargetUUID() != null ) {
            compoundTag.putUUID( TAG_PLAYER_UUID, getPlayerTargetUUID() );
        }
    }
    
    @Override
    public void readAdditionalSaveData( CompoundTag compoundTag ) {
        super.readAdditionalSaveData( compoundTag );
        
        if( compoundTag.hasUUID( TAG_PLAYER_UUID ) ) {
            setPlayerTargetUUID( compoundTag.getUUID( TAG_PLAYER_UUID ) );
        }
    }
    
    @SuppressWarnings( "SameParameterValue" )
    protected boolean canReachDist( double x, double y, double z, int dist ) {
        Vec3 vector3d = new Vec3( x - getX(), y - getY(), z - getZ() );
        vector3d = vector3d.normalize();
        AABB aabb = getBoundingBox().inflate( 0.5F );
        
        for( int i = 0; i < dist; i++ ) {
            aabb = aabb.move( vector3d );
            
            if( !level().noCollision( this, aabb ) ) {
                return false;
            }
        }
        return true;
    }
    
    /** Copied from ghast */
    protected static class LookAroundGoal extends Goal {
        private final AbstractLunarSiegeGhast ghast;
        
        public LookAroundGoal( AbstractLunarSiegeGhast ghast ) {
            this.ghast = ghast;
            setFlags( EnumSet.of( Goal.Flag.LOOK ) );
        }
        
        @Override
        public boolean canUse() {
            return true;
        }
        
        public void tick() {
            if( ghast.getTarget() == null ) {
                Vec3 vec3 = ghast.getDeltaMovement();
                ghast.setYRot( -((float) Mth.atan2( vec3.x, vec3.z )) * (180F / (float) Math.PI) );
            }
            else {
                LivingEntity target = ghast.getTarget();
                
                double x = target.getX() - ghast.getX();
                double z = target.getZ() - ghast.getZ();
                ghast.setYRot( -((float) Mth.atan2( x, z )) * (180F / (float) Math.PI) );
            }
            ghast.yBodyRot = ghast.getYRot();
        }
    }
}