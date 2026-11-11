package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.api.entity.ILunarSiegeMob;
import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.entity.living.ai.LunarSiegeTargetPlayerGoal;
import com.toast.apocalypse.common.entity.living.ai.MobHurtByTargetGoal;
import com.toast.apocalypse.common.entity.living.ai.SimpleFlyingMoveControl;
import com.toast.apocalypse.common.util.MobHelper;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

/**
 * This is a lunar siege mob that has the ability to phase through blocks, just like the Vex.
 * To compliment this, it has a massive aggro range and can track its target through walls,
 * making them the bread and butter of invasions. A quick summary of the Ghost's traits:
 *
 * <p> - Deals light base damage, but possesses the "deathtouch" trait,
 * assuring they always deal at least half a heart of damage.</p>
 *
 * <p> - Applies a short increased gravity effect to help deal with players that can fly.</p>
 *
 * <p> - Has a chance to retreat if damaged, often times phasing through a nearby wall, which can be disorienting.</p>
 */
@SuppressWarnings( "resource" )
public class Ghost extends FlyingMob implements Enemy, ILunarSiegeMob {
    
    // Event IDs
    public static final byte FREEZE_SFX_ID = (byte) 7;
    
    // NBT Keys
    public static final String TAG_FREEZE_TIME = "FreezeTime";
    
    /** True if the ghost should be completely frozen (immobilized). */
    private static final EntityDataAccessor<Boolean> IS_FROZEN = SynchedEntityData.defineId( Ghost.class, EntityDataSerializers.BOOLEAN );
    
    /** The UUID of this ghost's siege target. */
    private UUID playerTargetUUID;
    /** If the ghost should move away from its target in a random direction */
    private boolean isRetreating;
    /**
     * How many ticks this Ghost is frozen for.
     *
     * @see Ghost#aiStep()
     */
    private int freezeTicks = 0;
    
    
    /**
     * This entity's entity factory.
     *
     * @param entityType The entity type associated with this entity.
     * @param level      The level this entity exists in.
     */
    public Ghost( EntityType<? extends FlyingMob> entityType, Level level ) {
        super( entityType, level );
        moveControl = new GhostMovementController<>( this );
        xpReward = 3;
    }
    
    
    /** @return The default attributes to register to the Ghost entity type. */
    public static AttributeSupplier.Builder createGhostAttributes() {
        return FlyingMob.createMobAttributes()
                .add( Attributes.ATTACK_DAMAGE, 1.0 )
                .add( Attributes.MAX_HEALTH, 4.0 )
                .add( Attributes.FOLLOW_RANGE, 2048.0 )
                .add( Attributes.FLYING_SPEED, 0.4 );
    }
    
    /**
     * The spawn predicate for the Fearwolf entity type.
     *
     * @return True if the spawn should be allowed.
     */
    @SuppressWarnings( "unused" )
    public static boolean checkGhostSpawnRules( EntityType<? extends Ghost> entityType, ServerLevelAccessor level, MobSpawnType spawnReason, BlockPos pos, RandomSource random ) {
        return level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn( level, pos, random );
    }
    
    /** Called when this mob's goal selectors are to be populated. */
    @Override
    protected void registerGoals() {
        goalSelector.addGoal( 0, new Ghost.ManeuverAttackerGoal<>( this ) );
        goalSelector.addGoal( 1, new Ghost.MeleeAttackGoal<>( this ) );
        goalSelector.addGoal( 2, new RandomFlyGoal( this ) );
        goalSelector.addGoal( 3, new GhostLookAtGoal( this, Player.class, 8.0F ) );
        targetSelector.addGoal( 0, new MobHurtByTargetGoal( this, Enemy.class ) );
        targetSelector.addGoal( 1, new LunarSiegeTargetPlayerGoal<>( this, false ) );
        targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, false, false ) );
    }
    
    /** Called when this entity's synchronized data items are to be defined. */
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define( IS_FROZEN, false );
    }
    
    /**
     * @return True if this entity should be despawned when the difficulty
     * in its level is set to {@link Difficulty#PEACEFUL}.
     */
    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }
    
    /**
     * Gets this entity's standing eye height.
     * In {@link LivingEntity} this is called from {@link LivingEntity#getEyeHeight(Pose, EntityDimensions)}
     * when {@code pose} is not {@link Pose#SLEEPING}.
     *
     * @param pose       This entity's current pose.
     * @param dimensions The size of this entity's collision box.
     * @return The Y-distance from the bottom of this entity's collision box to its eyes.
     */
    @Override
    protected float getStandingEyeHeight( Pose pose, EntityDimensions dimensions ) {
        return 1.80F;
    }
    
    /**
     * Applies knockback to this entity.
     *
     * @param strength The knockback strength.
     * @param ratioX   The X ratio of the knockback.
     * @param ratioZ   The Z ratio of the knockback.
     */
    @Override
    public void knockback( double strength, double ratioX, double ratioZ ) {
        if( !isFrozen() ) super.knockback( strength, ratioX, ratioZ );
    }
    
    /**
     * Attempts to damage this entity.
     *
     * @param damageSource The damage source that is damaging the entity.
     * @param damage       The amount of damage to inflict.
     * @return True if the entity was actually hurt. Returns false otherwise.
     */
    @Override
    public boolean hurt( DamageSource damageSource, float damage ) {
        final Entity entity = damageSource.getEntity();
        
        if( super.hurt( damageSource, damage ) ) {
            // A 50/50 chance to retreat when hurt, if not frozen
            if( !isFrozen() && entity != null && entity == getTarget() && random.nextInt( 2 ) == 0 ) {
                setRetreating( true );
            }
            return true;
        }
        return false;
    }
    
    /** Causes this entity to hurt the given entity. */
    @Override
    public boolean doHurtTarget( Entity entity ) {
        if( super.doHurtTarget( entity ) ) {
            if( entity instanceof LivingEntity livingEntity ) {
                MobHelper.applyEffect( livingEntity, this, CrustObjects.Effects.WEIGHT, 1,
                        ApocalypseConfig.ENTITIES.FULL_MOON.ghostWeightDuration );
                MobHelper.applyDeathtouch( livingEntity, ApocalypseConfig.ENTITIES.FULL_MOON.ghostDeathtouch );
            }
            return true;
        }
        else {
            return false;
        }
    }
    
    /** @return The sound source this entity uses when playing sounds. */
    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }
    
    /** @return True if this entity currently has direct line of sight to the given entity. */
    @Override
    public boolean hasLineOfSight( Entity entity ) {
        // Can always see target
        return true;
    }
    
    /** @return True if this entity gets pushed when colliding with another entity's collision box. */
    @Override
    public boolean isPushable() {
        return false; // Cannot be pushed by entities
    }
    
    /** Causes this entity to push the given entity. */
    @Override
    protected void doPush( Entity entity ) {
        // Does not push other entities
    }
    
    /** @return True if this entity can drown in the given fluid type. */
    @Override
    public boolean canDrownInFluidType( FluidType type ) {
        return false;
    }
    
    /** @return True if this entity can be pushed by fluid blocks. */
    @Override
    public boolean isPushedByFluid( FluidType fluidType ) {
        return false; // Not pushed by fluids
    }
    
    /** @return True if this entity's movement is affected when it is inside a fluid. */
    @Override
    protected boolean isAffectedByFluids() {
        return false; // Not affected by fluids
    }
    
    /** Called when this entity is being hurt by being inside lava. */
    @Override
    public void lavaHurt() {
        // Immune to lava
    }
    
    /** @return True if the ghost just got hit and is falling back. */
    public boolean isRetreating() {
        return isRetreating;
    }
    
    /** Sets the retreating state of the ghost. */
    protected void setRetreating( boolean retreating ) {
        isRetreating = retreating;
    }
    
    /** @return True if this ghost is currently frozen. */
    public boolean isFrozen() {
        return this.entityData.get( IS_FROZEN );
    }
    
    /** Sets this ghost as frozen for the given amount of ticks. */
    public void freeze( int ticks ) {
        entityData.set( IS_FROZEN, true );
        freezeTicks = ticks;
        
        if( !level().isClientSide ) {
            level().broadcastEntityEvent( this, FREEZE_SFX_ID );
        }
    }
    
    /** Unfreezes this ghost. */
    private void unfreeze() {
        entityData.set( IS_FROZEN, false );
    }
    
    /** Helper method for spawning particles and playing the freeze sound. */
    private void playFreezeSfx() {
        level().playLocalSound( blockPosition(), ApocalypseObjects.SoundEvents.GHOST_FREEZE.get(), SoundSource.HOSTILE,
                1.0F, 1.0F, false
        );
        for( int i = 0; i < 13; i++ ) {
            level().addParticle( ParticleTypes.END_ROD,
                    getX() + 0.5D, getY() + 0.5D, getZ() + 0.5D,
                    random.nextGaussian() / 3, random.nextGaussian() / 3, random.nextGaussian() / 3
            );
        }
    }
    
    /**
     * Called on the client when the server sends an entity event.
     *
     * @param eventId The ID of the entity event.
     */
    @Override
    public void handleEntityEvent( byte eventId ) {
        if( eventId == FREEZE_SFX_ID ) playFreezeSfx();
        else super.handleEntityEvent( eventId );
    }
    
    /** @return The sound event this entity plays as its ambient/idle sound. This can be null. */
    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if( isFrozen() ) return null;
        return ApocalypseObjects.SoundEvents.GHOST_IDLE.get();
    }
    
    /**
     * @param damageSource The damage source that caused this entity to take damage
     * @return The sound event this entity plays when it is hurt. This can be null.
     */
    @Override
    protected SoundEvent getHurtSound( DamageSource damageSource ) {
        return ApocalypseObjects.SoundEvents.GHOST_HURT.get();
    }
    
    /**
     * Plays this entity's hurt sound when it is hurt.
     *
     * @param damageSource The damage source that caused this entity to take damage.
     */
    @Override
    protected void playHurtSound( DamageSource damageSource ) {
        if( isFrozen() ) return;
        super.playHurtSound( damageSource );
    }
    
    /** @return The sound event this entity plays when it dies. This can be null. */
    @Override
    protected SoundEvent getDeathSound() {
        return ApocalypseObjects.SoundEvents.GHOST_DEATH.get();
    }
    
    /**
     * Plays this entity's step sound.
     *
     * @param pos   The block position where the sound should be played.
     * @param state The block state that was stepped on.
     */
    @Override
    protected void playStepSound( BlockPos pos, BlockState state ) {
        // No step sound
    }
    
    /**
     * @return This entity's mob type.
     * <br>
     * Among other things, this determines whether enchantments such as
     * {@link net.minecraft.world.item.enchantment.Enchantments#SMITE smite}
     * or {@link net.minecraft.world.item.enchantment.Enchantments#BANE_OF_ARTHROPODS bane of arthropods} are effective against this entity.
     */
    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }
    
    /** Called each tick on both client and server to update this entity. */
    @Override
    public void tick() {
        noPhysics = true;
        super.tick();
        noPhysics = false;
    }
    
    /** Called on both server and client each tick to update this entity's logic and movement. */
    @Override
    public void aiStep() {
        if( isAlive() ) {
            MobHelper.burnInSunlight( this );
            
            if( !level().isClientSide ) {
                // Tick freeze timer
                if( --freezeTicks <= 0 && isFrozen() ) {
                    unfreeze();
                }
            }
        }
        super.aiStep();
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
     * @param playerTargetUUID The UUID of the player to target.
     */
    @Override // ILunarSiegeMob
    public void setPlayerTargetUUID( @Nullable UUID playerTargetUUID ) {
        this.playerTargetUUID = playerTargetUUID;
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
        compoundTag.putInt( TAG_FREEZE_TIME, freezeTicks );
    }
    
    /** Called when this entity is being loaded from NBT. */
    @Override
    public void readAdditionalSaveData( CompoundTag compoundTag ) {
        super.readAdditionalSaveData( compoundTag );
        
        if( compoundTag.hasUUID( TAG_PLAYER_UUID ) ) {
            setPlayerTargetUUID( compoundTag.getUUID( TAG_PLAYER_UUID ) );
        }
        if( NBTHelper.containsNumber( compoundTag, TAG_FREEZE_TIME ) ) {
            freezeTicks = compoundTag.getInt( TAG_FREEZE_TIME );
        }
    }
    
    /** A flying move controller implementation for the Ghost. */
    private static class GhostMovementController<T extends Ghost> extends SimpleFlyingMoveControl {
        
        /** The ghost that owns this controller instance. */
        final T ghost;
        
        
        /**
         * Creates a new istance.
         *
         * @param ghost The owner of this move control.
         */
        public GhostMovementController( T ghost ) {
            super( ghost );
            this.ghost = ghost;
        }
        
        
        /** Updates this move control's logic. */
        @Override
        public void tick() {
            if( operation == Operation.MOVE_TO ) {
                // Halt all movement if frozen
                if( ghost.isFrozen() ) return;
                
                final Vec3 moveVec = new Vec3( wantedX - ghost.getX(), wantedY - ghost.getY(), wantedZ - ghost.getZ() );
                double dist = moveVec.length();
                
                // If the ghost is very close to the target destination, slow down smoothly
                if( dist < ghost.getBoundingBox().getSize() ) {
                    operation = Operation.WAIT;
                    ghost.setDeltaMovement( ghost.getDeltaMovement().scale( 0.5D ) );
                }
                else {
                    // Update delta movement
                    ghost.setDeltaMovement( ghost.getDeltaMovement().add( moveVec.normalize().scale( getScaledFlyingSpeed() ) ) );
                    
                    // Update body rotation based on whether the ghost has a target or not
                    if( ghost.getTarget() == null ) {
                        Vec3 velocity = ghost.getDeltaMovement();
                        ghost.setYRot( -((float) Mth.atan2( velocity.x, velocity.z )) * (180F / (float) Math.PI) );
                    }
                    else {
                        double xDist = ghost.getTarget().getX() - ghost.getX();
                        double zDist = ghost.getTarget().getZ() - ghost.getZ();
                        ghost.setYRot( -((float) Mth.atan2( xDist, zDist )) * (180F / (float) Math.PI) );
                    }
                    ghost.yBodyRot = ghost.getYRot();
                }
            }
        }
    }
    
    /** A movement goal that attempts to move the ghost away from the entity that last hurt it. */
    private static class ManeuverAttackerGoal<T extends Ghost> extends Goal {
        
        /** The owner of this goal. */
        private final T ghost;
        
        
        /**
         * Creates a new instance.
         *
         * @param ghost The Ghost entity that owns this goal.
         */
        public ManeuverAttackerGoal( T ghost ) {
            this.ghost = ghost;
            setFlags( EnumSet.of( Flag.MOVE ) );
        }
        
        
        /** @return True if this goal can start running. */
        @Override
        public boolean canUse() {
            return ghost.getTarget() != null && ghost.isRetreating();
        }
        
        /** @return True if this goal has started and should continue running. */
        @Override
        public boolean canContinueToUse() {
            return ghost.getTarget() != null && ghost.getTarget().isAlive() && ghost.isRetreating() && ghost.moveControl.hasWanted();
        }
        
        /** Called when this goal has just started running. */
        @Override
        public void start() {
            RandomSource random = ghost.getRandom();
            double x = ghost.getX() + (random.nextGaussian() * 10);
            double y = ghost.getY() + (random.nextGaussian() * 10);
            double z = ghost.getZ() + (random.nextGaussian() * 10);
            ghost.moveControl.setWantedPosition( x, y, z, 1.25 );
        }
        
        /** Called when this goal stops running. */
        @Override
        public void stop() {
            ghost.setRetreating( false );
        }
    }
    
    /**
     * A simple melee-attack AI for the Ghost that stops running if the ghost is frozen.
     *
     * @see Ghost#isFrozen()
     */
    private static class MeleeAttackGoal<T extends Ghost> extends Goal {
        
        /** The owner of this goal. */
        private final T ghost;
        /** How many ticks are left until the next attack can be performed. */
        private int ticksUntilNextAttack;
        
        
        /**
         * Constructs a new instance.
         *
         * @param ghost The Ghost entity that owns this goal.
         */
        public MeleeAttackGoal( T ghost ) {
            setFlags( EnumSet.of( Goal.Flag.MOVE ) );
            this.ghost = ghost;
        }
        
        
        /** @return True if this goal requires its logic to be updated every tick. */
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
        
        /** @return True if this goal can start running. */
        @Override
        public boolean canUse() {
            return !ghost.isFrozen() && ghost.getTarget() != null && ghost.getTarget().isAlive();
        }
        
        /** @return True if this goal has started and should continue running. */
        @Override
        public boolean canContinueToUse() {
            return canUse();
        }
        
        /** Called when this goal has just started running. */
        @Override
        public void start() {
            ghost.setAggressive( true );
        }
        
        /** Called when this goal stops running. */
        @Override
        public void stop() {
            LivingEntity target = ghost.getTarget();
            if( !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test( target ) ) {
                ghost.setTarget( null );
            }
            ghost.setAggressive( false );
        }
        
        /** Called to update this goal's logic. */
        @Override
        public void tick() {
            LivingEntity target = ghost.getTarget();
            if( target == null ) return;
            
            double distance = ghost.distanceToSqr( target );
            
            if( !ghost.isRetreating() && ghost.tickCount % 10 == 0 ) {
                followTarget( target );
            }
            if( ticksUntilNextAttack <= 0 ) {
                if( canAttackReach( target, distance ) ) {
                    ghost.doHurtTarget( target );
                    ticksUntilNextAttack = 20;
                }
            }
            else {
                --ticksUntilNextAttack;
            }
        }
        
        /** Makes the goal owner move towards the target's approximate center of mass. */
        private void followTarget( LivingEntity target ) {
            Vec3 pos = target.getEyePosition( 1.0F ).add( 0.0D, -(ghost.getBbHeight() / 1.8), 0.0D );
            ghost.moveControl.setWantedPosition( pos.x, pos.y, pos.z, 1.0 );
        }
        
        /** @return True if the goal owner is close enough to the target to attack. */
        private boolean canAttackReach( LivingEntity target, double distance ) {
            return distance <= (double) (ghost.getBbWidth() * 2.0F * ghost.getBbWidth() * 2.0F + target.getBbWidth());
        }
    }
    
    /** A modified copy-paste of the vanilla Ghast random fly goal. */
    static class RandomFlyGoal extends Goal {
        
        /** The owner of this goal. */
        private final Ghost ghost;
        
        
        /**
         * Creates a new instance.
         *
         * @param ghost The Ghost entity that owns this goal.
         */
        public RandomFlyGoal( Ghost ghost ) {
            this.ghost = ghost;
            setFlags( EnumSet.of( Goal.Flag.MOVE ) );
        }
        
        
        /** @return True if this goal can start running. */
        @Override
        public boolean canUse() {
            MoveControl moveControl = ghost.getMoveControl();
            
            if( ghost.getTarget() != null || ghost.isRetreating() )
                return false;
            
            if( !moveControl.hasWanted() ) {
                return true;
            }
            else {
                double x = moveControl.getWantedX() - ghost.getX();
                double y = moveControl.getWantedY() - ghost.getY();
                double z = moveControl.getWantedZ() - ghost.getZ();
                double sqr = x * x + y * y + z * z;
                return sqr < 1.0D || sqr > 3600.0D;
            }
        }
        
        /** @return True if this goal has started and should continue running. */
        @Override
        public boolean canContinueToUse() {
            return false;
        }
        
        /** Called when this goal has just started running. */
        @Override
        public void start() {
            RandomSource random = ghost.getRandom();
            final double x = ghost.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 8.0F);
            final double y = ghost.getY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 8.0F);
            final double z = ghost.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 8.0F);
            ghost.getMoveControl().setWantedPosition( x, y, z, 1.0 );
        }
    }
    
    /**
     * A look-at goal that stops running if the Ghost is frozen.
     *
     * @see Ghost#isFrozen()
     */
    static class GhostLookAtGoal extends LookAtPlayerGoal {
        
        /** The owner of this goal. */
        private final Ghost ghost;
        
        
        /**
         * Creates a new instance.
         *
         * @param ghost  The Ghost entity that owns this goal.
         * @param lookAt The base class of the type of entity that should be looked at.
         * @param range  The look target search range.
         */
        public GhostLookAtGoal( Ghost ghost, Class<? extends LivingEntity> lookAt, float range ) {
            super( ghost, lookAt, range );
            this.ghost = ghost;
            setFlags( EnumSet.of( Flag.LOOK ) );
        }
        
        
        /** @return True if this goal requires its logic to be updated every tick. */
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
        
        /** @return True if this goal can start running. */
        @Override
        public boolean canUse() {
            return super.canUse() && !ghost.isFrozen();
        }
        
        /** @return True if this goal has started and should continue running. */
        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !ghost.isFrozen();
        }
    }
}