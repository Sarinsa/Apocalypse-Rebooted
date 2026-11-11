package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.register.ApocalypseDamageTypes;
import com.toast.apocalypse.common.entity.living.ai.SimpleFlyingMoveControl;
import com.toast.apocalypse.common.util.MobHelper;
import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;

import java.util.EnumSet;

@SuppressWarnings( "resource" )
public class Shadefiend extends FlyingMob implements Enemy {
    
    /** Set to true when the Shadefiend is in a space with a harmful light level. */
    protected static final EntityDataAccessor<Boolean> IS_IN_LIGHT = SynchedEntityData.defineId( Shadefiend.class, EntityDataSerializers.BOOLEAN );
    
    
    public Shadefiend( EntityType<? extends Shadefiend> type, Level level ) {
        super( type, level );
        moveControl = new ShadefiendMoveControl( this );
        lookControl = new ShadefiendLookControl( this );
        xpReward = 1;
    }
    
    public static boolean checkShadefiendSpawnRules( EntityType<? extends Shadefiend> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random ) {
        return level.getDifficulty() != Difficulty.PEACEFUL;
    }
    
    public static AttributeSupplier.Builder createShadefiendAttributes() {
        return Mob.createMobAttributes()
                .add( Attributes.MOVEMENT_SPEED, 0.35 )
                .add( Attributes.MAX_HEALTH, 15.0 )
                .add( Attributes.ATTACK_DAMAGE, 3.0 )
                .add( Attributes.FLYING_SPEED, 0.6 );
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define( IS_IN_LIGHT, false );
    }
    
    @Override
    protected void registerGoals() {
        // Only be animate when we have a target player, looks more creepy from a distance
        goalSelector.addGoal( 0, new Shadefiend.MeleeAttackGoal( this ) );
        targetSelector.addGoal( 0, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
    }
    
    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }
    
    public boolean isInLight() {
        return entityData.get( IS_IN_LIGHT );
    }
    
    @Override
    public void tick() {
        super.tick();
        // noinspection resource
        Level level = level();
        
        boolean inHarmfulLight = (level.getBrightness( LightLayer.BLOCK, blockPosition() ) > getLightLevelLimit( LightLayer.BLOCK ))
                || (level.isDay() && level.getBrightness( LightLayer.SKY, blockPosition() ) > getLightLevelLimit( LightLayer.SKY ));
        
        if( level.isClientSide ) spawnParticles();
        else {
            entityData.set( IS_IN_LIGHT, inHarmfulLight );
        }
        // Damage the shadefiend if it is exposed to an intolerable light level
        if( inHarmfulLight ) {
            hurt( ApocalypseDamageTypes.of( level(), ApocalypseDamageTypes.LIGHT_INTOLERANCE ), 2 );
        }
    }
    
    /** Spawns misc particles depending on the state of the Shadefiend. */
    private void spawnParticles() {
        boolean hasTarget = getTarget() != null;
        float flapTickOffset = getId() * 3;
        
        float f = Mth.cos( (flapTickOffset + tickCount) * 7.448451F * ((float) Math.PI / 180F) + (float) Math.PI );
        float f1 = Mth.cos( (flapTickOffset + tickCount + 1) * 7.448451F * ((float) Math.PI / 180F) + (float) Math.PI );
        
        if( hasTarget && f > 0.0F && f1 <= 0.0F ) {
            level().playLocalSound(
                    getX(), getY(), getZ(),
                    ApocalypseObjects.SoundEvents.SHADEFIEND_FLAP.get(),
                    getSoundSource(),
                    0.95F + random.nextFloat() * 0.05F,
                    0.95F + random.nextFloat() * 0.05F,
                    false
            );
        }
        float xOffset = Mth.cos( getYRot() * ((float) Math.PI / 180F) ) * (1.3F + 0.21F);
        float zOffset = Mth.sin( getYRot() * ((float) Math.PI / 180F) ) * (1.3F + 0.21F);
        float yOffset = hasTarget ? (0.3F + f * 0.45F) * 1.2F : 0.3F;
        
        level().addParticle(
                ParticleTypes.SMOKE,
                getX() + (double) xOffset,
                getY() + (double) yOffset,
                getZ() + (double) zOffset,
                0.0D, 0.0D, 0.0D
        );
        level().addParticle( ParticleTypes.SMOKE,
                getX() - (double) xOffset,
                getY() + (double) yOffset,
                getZ() - (double) zOffset,
                0.0D, 0.0D, 0.0D
        );
    }
    
    /** @return The max brightness for the specified light layer that the Shadefiend can tolerate. */
    private int getLightLevelLimit( LightLayer lightLayer ) {
        return lightLayer == LightLayer.SKY ? ApocalypseConfig.ENTITIES.MISC.shadefiendSkyLightTolerance.getInt() :
                ApocalypseConfig.ENTITIES.MISC.shadefiendBlockLightTolerance.getInt();
    }
    
    @Override
    public boolean doHurtTarget( Entity entity ) {
        if( super.doHurtTarget( entity ) ) {
            if( entity instanceof LivingEntity livingEntity ) {
                MobHelper.applyEffect( livingEntity, this, MobEffects.DARKNESS, 0,
                        ApocalypseConfig.ENTITIES.MISC.shadefiendDarknessDuration );
                MobHelper.applyEffect( livingEntity, this, CrustObjects.Effects.VULNERABILITY, 0,
                        ApocalypseConfig.ENTITIES.MISC.shadefiendVulnerabilityDuration );
                MobHelper.applyDeathtouch( livingEntity, ApocalypseConfig.ENTITIES.MISC.shadefiendDeathtouch );
            }
            return true;
        }
        else {
            return false;
        }
    }
    
    @Override
    public boolean canDrownInFluidType( FluidType type ) {
        return false;
    }
    
    @Override
    protected float getStandingEyeHeight( Pose pose, EntityDimensions dimensions ) {
        return dimensions.height * 0.35F;
    }
    
    @Override
    protected BodyRotationControl createBodyControl() {
        return new ShadefiendBodyRotationControl( this );
    }
    
    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }
    
    @Override
    public void playAmbientSound() {
        // Only play sound while having a target
        if( getTarget() != null ) {
            super.playAmbientSound();
        }
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return ApocalypseObjects.SoundEvents.SHADEFIEND_IDLE.get();
    }
    
    @Override
    protected SoundEvent getHurtSound( DamageSource damageSource ) {
        return ApocalypseObjects.SoundEvents.SHADEFIEND_HURT.get();
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return ApocalypseObjects.SoundEvents.SHADEFIEND_DEATH.get();
    }
    
    /**
     * A body rotation control implementation for the Shadefiend that makes sure
     * Y-rotation for the head and body are always the same.
     */
    class ShadefiendBodyRotationControl extends BodyRotationControl {
        public ShadefiendBodyRotationControl( Mob mob ) {
            super( mob );
        }
        
        @Override
        public void clientTick() {
            Shadefiend.this.yHeadRot = Shadefiend.this.yBodyRot = Shadefiend.this.getYRot();
        }
    }
    
    /** A look control implementation that does absolutely nothing. */
    static class ShadefiendLookControl extends LookControl {
        public ShadefiendLookControl( Mob mob ) {
            super( mob );
        }
        
        @Override
        public void tick() {
            // Does nothin'
        }
    }
    
    /** A flying move control implementation for the Shadefiend. */
    static class ShadefiendMoveControl extends SimpleFlyingMoveControl {
        
        /**
         * Creates a new instance.
         *
         * @param flyingMob The owner of this move control.
         */
        public ShadefiendMoveControl( FlyingMob flyingMob ) {
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
                            wantedZ - mob.getZ() );
                    moveVec = moveVec.normalize();
                    
                    if( !isWithinDistToWanted( 1.0 ) && noCollision( moveVec ) ) {
                        mob.setDeltaMovement( mob.getDeltaMovement().add( moveVec.scale( getScaledFlyingSpeed() ) ) );
                    }
                    else {
                        operation = Operation.WAIT;
                    }
                }
            }
            if( mob.getTarget() != null ) {
                updateRotations();
            }
        }
        
        /** Updates the Shadefiend's rotation based on where it is currently headed. */
        private void updateRotations() {
            double x = wantedX - mob.getX();
            double y = wantedY - mob.getY();
            double z = wantedZ - mob.getZ();
            double sqrt = Math.sqrt( x * x + z * z );
            
            if( Math.abs( sqrt ) > (double) 1.0E-5F ) {
                double offset = 1.0D - Math.abs( y * (double) 0.7F ) / sqrt;
                x *= offset;
                z *= offset;
                sqrt = Math.sqrt( x * x + z * z );
                float horizontalAngle = (float) Mth.atan2( z, x );
                float yDegrees = Mth.wrapDegrees( mob.getYRot() + 90.0F );
                float xzDegrees = Mth.wrapDegrees( horizontalAngle * (180F / (float) Math.PI) );
                mob.setYRot( Mth.approachDegrees( yDegrees, xzDegrees, 4.0F ) - 90.0F );
                mob.yBodyRot = mob.getYRot();
                
                float xRot = (float) (-(Mth.atan2( -y, sqrt ) * (double) (180F / (float) Math.PI)));
                mob.setXRot( xRot );
            }
        }
    }
    
    private static class MeleeAttackGoal extends Goal {
        
        private final Shadefiend mob;
        
        private int ticksUntilNextAttack;
        
        
        public MeleeAttackGoal( Shadefiend shadefiend ) {
            setFlags( EnumSet.of( Flag.MOVE ) );
            mob = shadefiend;
        }
        
        private void setWantedPosition( LivingEntity target ) {
            Vec3 vec3 = target.getEyePosition( 1.0F ).add( 0.0D, -(mob.getBbHeight() / 2), 0.0D );
            mob.moveControl.setWantedPosition( vec3.x, vec3.y, vec3.z, 1.0D );
        }
        
        @Override
        public boolean canUse() {
            LivingEntity target = mob.getTarget();
            return mob.isAlive() && target != null && mob.hasLineOfSight( target );
        }
        
        @Override
        public boolean canContinueToUse() {
            LivingEntity target = mob.getTarget();
            
            if( mob.isAlive() && !mob.isVehicle() && target != null && target.isAlive() ) {
                return mob.hasLineOfSight( target ) || ((SimpleFlyingMoveControl) mob.moveControl).canReachCurrentWanted();
            }
            return false;
        }
        
        @Override
        public void start() {
            ticksUntilNextAttack = 0;
            mob.setAggressive( true );
            LivingEntity target = mob.getTarget();
            
            if( target != null ) {
                setWantedPosition( target );
            }
        }
        
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
        
        @Override
        public void stop() {
            mob.setAggressive( false );
            mob.setTarget( null );
        }
        
        @Override
        @SuppressWarnings( "ConstantConditions" )
        public void tick() {
            LivingEntity target = mob.getTarget();
            
            if( target == null ) return;
            if( ticksUntilNextAttack > 0 ) --ticksUntilNextAttack;
            
            if( canAttackReachTarget( target ) && ticksUntilNextAttack <= 0 ) {
                mob.doHurtTarget( target );
                mob.level().playSound(
                        null,
                        mob.blockPosition(),
                        ApocalypseObjects.SoundEvents.SHADEFIEND_BITE.get(),
                        mob.getSoundSource(),
                        mob.getSoundVolume(),
                        (mob.random.nextFloat() - mob.random.nextFloat()) * 0.2F + 1.0F
                );
                ticksUntilNextAttack = 20;
            }
            
            if( mob.tickCount % 10 == 0 ) {
                setWantedPosition( target );
            }
        }
        
        /** @return True if the shadefiend has line of sight to the target and the target is within attack range. */
        private boolean canAttackReachTarget( LivingEntity target ) {
            return mob.getBoundingBox().inflate( 0.4F ).intersects( target.getBoundingBox() )
                    && mob.hasLineOfSight( target );
        }
    }
}