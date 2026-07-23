package com.toast.apocalypse.common.entity.projectile;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.entity.living.Grump;
import fathertoast.crust.api.lib.EntityEventHelper;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * This is a fishhook projectile that can be fired by monsters to pull targets closer.<br>
 * Players are able to block this projectile with a shield, negating its effects.
 * <p>
 * Essentially a copy-paste of {@link net.minecraft.world.entity.projectile.FishingHook}
 */
public class MonsterFishHook extends Projectile implements IEntityAdditionalSpawnData {
    
    private static final EntityDataAccessor<Integer> DATA_HOOKED_ENTITY = SynchedEntityData.defineId( MonsterFishHook.class, EntityDataSerializers.INT );
    private static final EntityDataAccessor<Boolean> LAUNCHED_BY_RIDER = SynchedEntityData.defineId( MonsterFishHook.class, EntityDataSerializers.BOOLEAN );
    private int life;
    private Entity hookedIn;
    private State currentState = State.FLYING;
    
    public MonsterFishHook( EntityType<? extends MonsterFishHook> entityType, Level level ) {
        super( entityType, level );
    }
    
    private MonsterFishHook( Level level, Mob mob ) {
        super( ApocalypseEntities.MONSTER_FISH_HOOK.get(), level );
        setOwner( mob );
        noCulling = true;
    }
    
    public MonsterFishHook( Mob mob, LivingEntity target, Level level ) {
        this( level, mob );
        
        final Vec3 lookVec = mob.getViewVector( 1.0F ).scale( mob.getBbWidth() );
        setPos( mob.getX() + lookVec.x, mob.getEyeY() - 0.1, mob.getZ() + lookVec.z );
        
        final double dX = target.getX() - getX();
        final double dY = target.getY( 0.3333 ) - getY();
        final double dZ = target.getZ() - getZ();
        final double dH = Mth.sqrt( (float) (dX * dX + dZ * dZ) );
        
        shoot( dX, dY + dH * 0.2, dZ, 1.3F, 0 );
    }
    
    public MonsterFishHook( Vec3 riderLookVec, Mob mob, Level level ) {
        this( level, mob );
        
        getEntityData().set( LAUNCHED_BY_RIDER, true );
        
        final Vec3 vec = mob.getEyePosition().add( riderLookVec.x * 10, riderLookVec.y * 10, riderLookVec.z * 10 );
        setPos( mob.getX() + riderLookVec.x, mob.getEyeY() - 0.1, mob.getZ() + riderLookVec.z );
        
        final double dX = vec.x() - getX();
        final double dY = vec.y() - getY();
        final double dZ = vec.z() - getZ();
        final double dH = Mth.sqrt( (float) (dX * dX + dZ * dZ) );
        
        shoot( dX, dY + dH * 0.2, dZ, 1.3F, 0 );
    }
    
    @Override
    protected void defineSynchedData() {
        getEntityData().define( DATA_HOOKED_ENTITY, 0 );
        getEntityData().define( LAUNCHED_BY_RIDER, false );
    }
    
    @Override
    public void onSyncedDataUpdated( EntityDataAccessor<?> dataParameter ) {
        if( DATA_HOOKED_ENTITY.equals( dataParameter ) ) {
            int hookedId = getEntityData().get( DATA_HOOKED_ENTITY );
            // noinspection resource
            hookedIn = hookedId > 0 ? level().getEntity( hookedId - 1 ) : null;
        }
        super.onSyncedDataUpdated( dataParameter );
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance( double distance ) {
        return distance < 4096.0D;
    }
    
    @Override
    public void lerpTo( double parameter, double mappings, double would, float be, float nice, int to, boolean have ) {
    }
    
    @Override
    public void tick() {
        super.tick();
        LivingEntity livingEntity = this.getLivingOwner();
        
        // Remove self if owner is null
        if( livingEntity == null ) {
            discard();
        }
        else if( !shouldStopFishing( livingEntity ) ) {
            // Remove hook after a while, unless launched by a rider.
            if( !getEntityData().get( LAUNCHED_BY_RIDER ) ) {
                ++life;
                if( life >= 120 ) {
                    discard();
                    return;
                }
            }
            float fluidHeight = 0.0F;
            BlockPos pos = blockPosition();
            // noinspection resource
            FluidState fluidState = level().getFluidState( pos );
            
            if( fluidState.getFluidType() == ForgeMod.WATER_TYPE.get() ) {
                fluidHeight = fluidState.getHeight( level(), pos );
            }
            
            if( currentState == State.FLYING ) {
                if( hookedIn != null ) {
                    setDeltaMovement( Vec3.ZERO );
                    currentState = State.HOOKED_IN_ENTITY;
                    return;
                }
                
                if( fluidHeight > 0.0F ) {
                    setDeltaMovement( getDeltaMovement().multiply( 0.3D, 0.2D, 0.3D ) );
                    currentState = State.BOBBING;
                    return;
                }
                checkCollision();
            }
            else {
                if( currentState == State.HOOKED_IN_ENTITY ) {
                    if( hookedIn != null ) {
                        if( !hookedIn.isAlive() ) {
                            hookedIn = null;
                            currentState = State.FLYING;
                        }
                        else {
                            setPos( hookedIn.getX(), hookedIn.getY( 0.8D ), hookedIn.getZ() );
                        }
                    }
                    return;
                }
                
                if( currentState == State.BOBBING ) {
                    Vec3 vec3 = getDeltaMovement();
                    double d0 = getY() + vec3.y - pos.getY() - (double) fluidHeight;
                    
                    if( Math.abs( d0 ) < 0.01D ) {
                        d0 += Math.signum( d0 ) * 0.1D;
                    }
                    setDeltaMovement( vec3.x * 0.9D, vec3.y - d0 * (double) random.nextFloat() * 0.2D, vec3.z * 0.9D );
                }
            }
            
            if( fluidState.getFluidType() != ForgeMod.WATER_TYPE.get() ) {
                setDeltaMovement( getDeltaMovement().add( 0.0D, -0.03D, 0.0D ) );
            }
            move( MoverType.SELF, getDeltaMovement() );
            updateRotation();
            
            if( currentState == State.FLYING && (onGround() || horizontalCollision) ) {
                setDeltaMovement( Vec3.ZERO );
            }
            setDeltaMovement( getDeltaMovement().scale( 0.92D ) );
            reapplyPosition();
        }
    }
    
    private boolean shouldStopFishing( LivingEntity owner ) {
        if( !owner.isAlive() || !(distanceToSqr( owner ) > 1024.0D) ) {
            return false;
        }
        else {
            discard();
            return true;
        }
    }
    
    private void checkCollision() {
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector( this, this::canHitEntity );
        this.onHit( hitResult );
    }
    
    @Override
    protected boolean canHitEntity( Entity entity ) {
        return super.canHitEntity( entity ) || entity.isAlive() && entity instanceof ItemEntity;
    }
    
    @Override
    protected void onHitEntity( EntityHitResult hitResult ) {
        super.onHitEntity( hitResult );
        
        // noinspection resource
        if( !level().isClientSide ) {
            Entity entity = hitResult.getEntity();
            
            if( entity instanceof Player player ) {
                if( isBlocked( player ) ) {
                    if( getLivingOwner() instanceof Grump grump ) {
                        grump.hookBlocked();
                        EntityEventHelper.SHIELD_BLOCK_SOUND.broadcast( player );
                    }
                    discard();
                    return;
                }
            }
            hookedIn = hitResult.getEntity();
            setHookedEntity();
        }
    }
    
    private boolean isBlocked( Player player ) {
        if( player.isBlocking() ) {
            Vec3 hookPos = position();
            
            Vec3 playerView = player.getViewVector( 1.0F );
            Vec3 vec3 = hookPos.vectorTo( player.position() ).normalize();
            vec3 = new Vec3( vec3.x, 0.0D, vec3.z );
            return vec3.dot( playerView ) < 0.0D;
        }
        return false;
    }
    
    @Override
    protected void onHitBlock( BlockHitResult hitResult ) {
        super.onHitBlock( hitResult );
        setDeltaMovement( getDeltaMovement().normalize().scale( hitResult.distanceTo( this ) ) );
    }
    
    private void setHookedEntity() {
        getEntityData().set( DATA_HOOKED_ENTITY, hookedIn.getId() + 1 );
    }
    
    @Override
    public void addAdditionalSaveData( CompoundTag compoundTag ) {
        // Nothing to write.
    }
    
    @Override
    public void readAdditionalSaveData( CompoundTag compoundTag ) {
        // Nothing to read.
    }
    
    @Override
    public void handleEntityEvent( byte event ) {
        // noinspection resource
        if( event == 31 && level().isClientSide && hookedIn instanceof LocalPlayer ) {
            bringInHookedEntity();
        }
        super.handleEntityEvent( event );
    }
    
    public void bringInHookedEntity() {
        LivingEntity livingEntity = getLivingOwner();
        
        if( livingEntity != null ) {
            // noinspection resource
            level().playSound(
                    null,
                    livingEntity.blockPosition(),
                    ApocalypseObjects.SoundEvents.MONSTER_HOOK_RETRIEVE.get(),
                    SoundSource.NEUTRAL,
                    0.6F,
                    0.4F / (level().random.nextFloat() * 0.4F + 0.8F)
            );
            double xMotion = livingEntity.getX() - getX();
            double yMotion = livingEntity.getY() - getY();
            double zMotion = livingEntity.getZ() - getZ();
            
            double v = Math.sqrt( xMotion * xMotion + yMotion * yMotion + zMotion * zMotion );
            double multiplier = 0.3;
            
            Vec3 velocity = new Vec3( xMotion * multiplier, yMotion * (multiplier / 2) + Math.sqrt( v ) * 0.1, zMotion * multiplier );
            hookedIn.setDeltaMovement( velocity );
            
            if( hookedIn instanceof ServerPlayer serverPlayer ) {
                serverPlayer.connection.connection.send( new ClientboundSetEntityMotionPacket( serverPlayer ) );
            }
        }
    }
    
    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }
    
    @Nullable
    public LivingEntity getLivingOwner() {
        Entity entity = this.getOwner();
        return entity instanceof LivingEntity ? (LivingEntity) entity : null;
    }
    
    @Nullable
    public Entity getHookedIn() {
        return this.hookedIn;
    }
    
    @Override
    public boolean canChangeDimensions() {
        return false;
    }
    
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket( this );
    }
    
    @Override
    public void writeSpawnData( FriendlyByteBuf buffer ) {
        buffer.writeInt( getOwner() == null ? getId() : getOwner().getId() );
    }
    
    @Override
    public void readSpawnData( FriendlyByteBuf additionalData ) {
        // noinspection resource
        this.setOwner( level().getEntity( additionalData.readInt() ) );
    }
    
    enum State {
        FLYING,
        HOOKED_IN_ENTITY,
        BOBBING
    }
}
