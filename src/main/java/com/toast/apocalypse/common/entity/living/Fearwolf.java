package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.entity.living.ai.FearwolfRunAwayGoal;
import com.toast.apocalypse.common.util.MobHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * This monster is an aggressive, yet cowardly wolf variant that attacks nearby players on sight, but is quick to flee
 * the moment the target retaliates.
 * <br><br>
 * The Fearwolf differs from normal wolves in the following ways:
 * <p>- Applies a slowness potion effect to its target on attack.</p>
 * <p>- Possesses the "deathtouch" trait, meaning the damage it deals cannot be reduced by armor or other effects, but will also
 * never one-shot the target.</p>
 * <p>- Cloaks itself and turns temporarily invisible when hurt by players.</p>
 */
@SuppressWarnings( "resource" )
public class Fearwolf extends Monster implements Enemy {
    
    /**
     * This entity's entity factory.
     *
     * @param entityType The entity type associated with this entity.
     * @param level      The level this entity exists in.
     */
    public Fearwolf( EntityType<? extends Monster> entityType, Level level ) {
        super( entityType, level );
    }
    
    
    /** @return The default attributes to register to the Fearwolf entity type. */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add( Attributes.MOVEMENT_SPEED, 0.35 )
                .add( Attributes.MAX_HEALTH, 10.0 )
                .add( Attributes.ATTACK_DAMAGE, 2.0 );
    }
    
    /**
     * The spawn predicate for the Fearwolf entity type.
     *
     * @return True if the spawn should be allowed.
     */
    @SuppressWarnings( "unused" )
    public static boolean checkFearwolfSpawnRules( EntityType<? extends Fearwolf> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random ) {
        return level.getDifficulty() != Difficulty.PEACEFUL && level.getBlockState( pos.below() ).is( BlockTags.ANIMALS_SPAWNABLE_ON ) && level.getEntitiesOfClass( Player.class, new AABB( pos ).inflate( 40.0D ) ).isEmpty();
    }
    
    /** Called when this mob's goal selectors are to be populated. */
    @Override
    protected void registerGoals() {
        goalSelector.addGoal( 1, new FloatGoal( this ) );
        goalSelector.addGoal( 2, new FearwolfRunAwayGoal( this, 1.0D ) );
        goalSelector.addGoal( 3, new MeleeAttackGoal( this, 0.85D, true ) );
        goalSelector.addGoal( 4, new WaterAvoidingRandomStrollGoal( this, 0.6D ) );
        goalSelector.addGoal( 5, new LookAtPlayerGoal( this, Player.class, 8.0F ) );
        goalSelector.addGoal( 6, new RandomLookAroundGoal( this ) );
        targetSelector.addGoal( 1, (new HurtByTargetGoal( this )).setAlertOthers() );
        targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
    }
    
    /** Causes this entity to hurt the given entity. */
    @Override
    public boolean doHurtTarget( Entity entity ) {
        if( super.doHurtTarget( entity ) ) {
            if( entity instanceof LivingEntity livingEntity ) {
                MobHelper.applyEffect( livingEntity, this, MobEffects.MOVEMENT_SLOWDOWN, 0,
                        ApocalypseConfig.ENTITIES.MISC.fearwolfSlownessDuration );
                MobHelper.applyDeathtouch( livingEntity, ApocalypseConfig.ENTITIES.MISC.fearwolfDeathtouch );
            }
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Plays this entity's step sound.
     *
     * @param pos   The block position where the sound should be played.
     * @param state The block state that was stepped on.
     */
    @Override
    protected void playStepSound( BlockPos pos, BlockState state ) {
        playSound( ApocalypseObjects.SoundEvents.FEARWOLF_STEP.get(), 0.15F, 1.0F );
    }
    
    /** @return The sound event this entity plays as its ambient/idle sound. This can be null. */
    @Override
    protected SoundEvent getAmbientSound() {
        return ApocalypseObjects.SoundEvents.FEARWOLF_IDLE.get();
    }
    
    /** @return The sound event this entity plays when it is hurt. This can be null. */
    @Override
    protected SoundEvent getHurtSound( DamageSource damageSource ) {
        return ApocalypseObjects.SoundEvents.FEARWOLF_HURT.get();
    }
    
    /** @return The sound event this entity plays when it dies. This can be null. */
    @Override
    protected SoundEvent getDeathSound() {
        return ApocalypseObjects.SoundEvents.FEARWOLF_DEATH.get();
    }
    
    /**
     * @return The sound volume to use for the sounds played by this entity.
     * <br><br>
     * Despite the name, this value should be considered the range of the sound, not the actual loudness of it.
     */
    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }
    
    /** @return The sound pitch to use for the "voice" sounds played by this entity. */
    @Override
    public float getVoicePitch() {
        return (random.nextFloat() - random.nextFloat()) * 0.2F + 0.8F;
    }
    
    /** @return The amount of experience this entity drops when it is killed. */
    @Override
    public int getExperienceReward() {
        return level().random.nextIntBetweenInclusive( 1, 6 );
    }
}