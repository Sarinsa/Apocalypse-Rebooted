package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.api.entity.ILunarSiegeMob;
import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.entity.living.ai.BreecherMoveToExplosionPosGoal;
import com.toast.apocalypse.common.entity.living.ai.BreecherSwellGoal;
import com.toast.apocalypse.common.entity.living.ai.LunarSiegeTargetPlayerGoal;
import com.toast.apocalypse.common.entity.living.ai.MobHurtByTargetGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This lunar siege mob is identical to a Creeper in almost every way, except that it has:
 * <p>
 * - A much greater aggro range.
 * <p>
 * - Ignores line of sight.
 * <p>
 * - Explode when they detect that they can't get any closer to the player.
 * <br><br>
 * Visually, the ony difference is that their eyes are entranced by the moon's power.
 */
@SuppressWarnings( "resource" )
public class Breecher extends Creeper implements ILunarSiegeMob {
    
    /** A flag for forcefully telling a breecher to explode. */
    // TODO - Just use the vanilla ignite flag instead, this is obsolete
    public static final EntityDataAccessor<Boolean> FORCE_SWELL = SynchedEntityData.defineId( Breecher.class, EntityDataSerializers.BOOLEAN );
    
    /** The UUID of this breecher's siege target. */
    private UUID playerTargetUUID;
    
    
    /**
     * This entity's entity factory.
     *
     * @param entityType The entity type associated with this entity.
     * @param level      The level this entity exists in.
     */
    public Breecher( EntityType<? extends Creeper> entityType, Level level ) {
        super( entityType, level );
    }
    
    
    /** @return The default attributes to register to the Breecher entity type. */
    public static AttributeSupplier.Builder createBreecherAttributes() {
        return Creeper.createAttributes().add( Attributes.FOLLOW_RANGE, 40.0 );
    }
    
    /** Called when this mob's goal selectors are to be populated. */
    protected void registerGoals() {
        goalSelector.addGoal( 1, new FloatGoal( this ) );
        goalSelector.addGoal( 2, new BreecherSwellGoal( this ) );
        goalSelector.addGoal( 3, new BreecherMoveToExplosionPosGoal( this, 1.0D, 20, 6 ) );
        goalSelector.addGoal( 3, new AvoidEntityGoal<>( this, Ocelot.class, 6.0F, 1.0D, 1.2D ) );
        goalSelector.addGoal( 3, new AvoidEntityGoal<>( this, Cat.class, 6.0F, 1.0D, 1.2D ) );
        goalSelector.addGoal( 4, new MeleeAttackGoal( this, 1.0D, false ) );
        goalSelector.addGoal( 5, new WaterAvoidingRandomStrollGoal( this, 0.8D ) );
        goalSelector.addGoal( 6, new LookAtPlayerGoal( this, Player.class, 8.0F ) );
        goalSelector.addGoal( 6, new RandomLookAroundGoal( this ) );
        targetSelector.addGoal( 0, new MobHurtByTargetGoal( this, Enemy.class ) );
        targetSelector.addGoal( 1, new LunarSiegeTargetPlayerGoal<>( this, false ) );
    }
    
    /** Called when this entity's synchronized data items are to be defined. */
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define( FORCE_SWELL, false );
    }
    
    /**
     * Completely ignore line of sight; the target
     * is always "visible"
     */
    @Override
    public boolean hasLineOfSight( Entity entity ) {
        return true;
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
    
    /** @return The sound event this entity plays when it is hurt. This can be null. */
    @Override
    protected SoundEvent getHurtSound( DamageSource damageSource ) {
        return ApocalypseObjects.SoundEvents.BREECHER_HURT.get();
    }
    
    /** @return The sound event this entity plays when it dies. This can be null. */
    @Override
    protected SoundEvent getDeathSound() {
        return ApocalypseObjects.SoundEvents.BREECHER_DEATH.get();
    }
    
    @Deprecated( forRemoval = true ) // Obsolete, will use vanilla IGNITE flag in the future
    public void forceSwell() {
        entityData.set( FORCE_SWELL, true );
    }
    
    @Deprecated( forRemoval = true ) // Obsolete, will use vanilla IGNITE flag in the future
    public boolean shouldForceSwell() {
        return entityData.get( FORCE_SWELL );
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
}