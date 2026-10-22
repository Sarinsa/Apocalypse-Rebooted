package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.api.entity.ILunarSiegeMob;
import com.toast.apocalypse.common.entity.projectile.DestroyerFireballEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

@SuppressWarnings( "resource" )
public abstract class AbstractLunarSiegeGhast extends Ghast implements ILunarSiegeMob {
    
    // NBT keys
    protected static final String TAG_EXPLOSION_POWER = "ExplosionPower";
    
    /** The UUID of this lunar siege Ghast's siege target. */
    @Nullable
    protected UUID playerTargetUUID;
    
    
    /**
     * This entity's entity factory.
     *
     * @param entityType The entity type associated with this entity.
     * @param level      The level this entity exists in.
     */
    public AbstractLunarSiegeGhast( EntityType<? extends Ghast> entityType, Level level ) {
        super( entityType, level );
    }
    
    
    /** @return The squared XZ distance to the given entity. */
    public final double horizontalDistToSqr( Entity entity ) {
        return horizontalDistToSqr( entity.position() );
    }
    
    /** @return The squared XZ distance to the given block position. */
    public final double horizontalDistToSqr( BlockPos pos ) {
        return horizontalDistToSqr( new Vec3( pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D ) );
    }
    
    /** @return The squared XZ distance to the given position vector. */
    public final double horizontalDistToSqr( Vec3 vec ) {
        double x = getX() - vec.x;
        double z = getZ() - vec.z;
        return x * x + z * z;
    }
    
    /** @return True if this entity can drown in the given fluid type. */
    @Override
    public boolean canDrownInFluidType( FluidType type ) {
        return false;
    }
    
    /** @return The sound event this entity plays as its ambient/idle sound. This can be null. */
    @Override
    protected SoundEvent getAmbientSound() {
        // noinspection ConstantConditions
        return null;
    }
    
    /**
     * @return The sound volume of the sounds played by this entity.
     * <br><br>
     * Despite the name, this value should be considered the range of the sound, not the actual loudness of it.
     */
    @Override
    protected float getSoundVolume() {
        // Louder than vanilla ghast; help players realize they might be getting blasted with fireballs from afar.
        return 12.0F;
    }
    
    /**
     * @return True if there are no block collisions between this entity and the given entity.
     * <br><br>
     * This method performs the same checks as {@link LivingEntity#hasLineOfSight(Entity)}, except
     * there is no distance limit.
     */
    public boolean isViewUnobstructed( Entity entity ) {
        if( entity.level() != level() ) return false;
        Vec3 vector3d = new Vec3( getX(), getEyeY(), getZ() );
        Vec3 vector3d1 = new Vec3( entity.getX(), entity.getEyeY(), entity.getZ() );
        return level().clip( new ClipContext( vector3d, vector3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this ) ).getType() == HitResult.Type.MISS;
    }
    
    /**
     * @return The UUID of this lunar siege mob's siege player target.
     * Lunar siege mobs spawned from commands or spawn eggs will normally not have a target UUID,
     * in which case this is expected to return null.
     */
    @Nullable
    @Override // ILunarSiegeMob
    public UUID getPlayerTargetUUID() {
        return playerTargetUUID;
    }
    
    /**
     * Sets this lunar siege mob's siege player target UUID.
     *
     * @param uuid The UUID of the player to target.
     */
    @Override // ILunarSiegeMob
    public void setPlayerTargetUUID( @Nullable UUID uuid ) {
        playerTargetUUID = uuid;
    }
    
    /**
     * Called when the entity is being saved, allowing additional data
     * to be saved to NBT.
     */
    @Override
    public void addAdditionalSaveData( CompoundTag compoundTag ) {
        super.addAdditionalSaveData( compoundTag );
        
        if( getPlayerTargetUUID() != null ) {
            compoundTag.putUUID( TAG_PLAYER_UUID, getPlayerTargetUUID() );
        }
    }
    
    /** Called when this entity is being loaded from NBT. */
    @Override
    public void readAdditionalSaveData( CompoundTag compoundTag ) {
        super.readAdditionalSaveData( compoundTag );
        
        if( compoundTag.hasUUID( TAG_PLAYER_UUID ) ) {
            setPlayerTargetUUID( compoundTag.getUUID( TAG_PLAYER_UUID ) );
        }
    }
    
    /**
     * @return True if this entity can move in the direction of {@code x}, {@code y} and {@code z}
     * for at least {@code dist} amount of blocks without colliding with anything.
     */
    @SuppressWarnings( "SameParameterValue" )
    protected boolean noCollisionTowards( double x, double y, double z, int dist ) {
        Vec3 vec = new Vec3( x - getX(), y - getY(), z - getZ() ).normalize();
        AABB aabb = getBoundingBox().inflate( 0.5F );
        
        for( int i = 0; i < dist; i++ ) {
            aabb = aabb.move( vec );
            if( !level().noCollision( this, aabb ) ) {
                return false;
            }
        }
        return true;
    }
    
    /** @return True if the given damage source is a player-deflected fireball. */
    protected static boolean isDeflectedFireball( DamageSource damageSource ) {
        if( !(damageSource.getEntity() instanceof Player) ) return false;
        Entity entity = damageSource.getDirectEntity();
        return entity instanceof LargeFireball || entity instanceof DestroyerFireballEntity;
    }
    
    
    /** A modified copy-paste of {@link Ghast.GhastLookGoal}. */
    protected static class LookAroundGoal extends Goal {
        
        /** The owner of this goal. */
        private final AbstractLunarSiegeGhast ghast;
        
        
        /**
         * Creates a new instance.
         *
         * @param ghast The lunar siege ghast entity that owns this goal.
         */
        public LookAroundGoal( AbstractLunarSiegeGhast ghast ) {
            this.ghast = ghast;
            setFlags( EnumSet.of( Goal.Flag.LOOK ) );
        }
        
        /** @return True if this goal can start running. */
        @Override
        public boolean canUse() {
            return true;
        }
        
        /** Called to update this goal's logic. */
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