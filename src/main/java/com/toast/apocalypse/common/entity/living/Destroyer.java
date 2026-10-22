package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.entity.living.ai.LunarSiegeTargetPlayerGoal;
import com.toast.apocalypse.common.entity.living.ai.MobHurtByTargetGoal;
import com.toast.apocalypse.common.entity.living.ai.SimpleFlyingMoveControl;
import com.toast.apocalypse.common.entity.projectile.DestroyerFireballEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * This lunar siege mob is a relative of the ghast whose purpose is to break through the player's defenses, mercilessly.
 * The Destroyer differs from normal ghasts in the following ways, assuming config values are set to default:
 * <p>
 * - Its fireballs have a lower explosion radius than a normal Ghast, but can destroy any type of block,
 * except blocks that are designed to be truly indestructible (bedrock, barrier etc.).
 * <p>
 * - Has a massive aggro range.
 * <p>
 * - Can deal heavy damage to equipped armor.
 * <p>
 * - Deflected fireballs are volatile and explode shortly after being hit.
 * <p>
 * - Can potentially survive being hit by multiple deflected fireballs.
 * <br><br>
 * In addition to the above, Destroyers are also capable of fireballing their target player's
 * respawn point, pressuring the player into confronting it.
 */
@SuppressWarnings( "resource" )
public class Destroyer extends AbstractLunarSiegeGhast {
    
    // TODO - This does not need to be a synced data item
    public static final EntityDataAccessor<Boolean> ATTACKED_BY_PT = SynchedEntityData.defineId( Destroyer.class, EntityDataSerializers.BOOLEAN );
    /**
     * True if this destroyer is currently prioritizing shooting|
     * fireballs at its target's spawn point.
     */
    protected boolean isTargetingSpawnPoint = false;
    
    
    /**
     * This entity's entity factory.
     *
     * @param entityType The entity type associated with this entity.
     * @param level      The level this entity exists in.
     */
    public Destroyer( EntityType<? extends Ghast> entityType, Level level ) {
        super( entityType, level );
        moveControl = new SimpleFlyingMoveControl( this );
        xpReward = 5;
    }
    
    
    /** @return The default attributes to register to the Destroyer entity type. */
    public static AttributeSupplier.Builder createDestroyerAttributes() {
        return Mob.createMobAttributes()
                .add( Attributes.MAX_HEALTH, 12.0 )
                .add( Attributes.FOLLOW_RANGE, 2048.0 )
                .add( ForgeMod.SWIM_SPEED.get(), 1.1 );
    }
    
    /**
     * The spawn predicate for the Destroyer entity type.
     *
     * @return True if the spawn should be allowed.
     */
    public static boolean checkDestroyerSpawnRules( EntityType<? extends Destroyer> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random ) {
        return level.getDifficulty() != Difficulty.PEACEFUL && Mob.checkMobSpawnRules( entityType, level, spawnType, pos, random );
    }
    
    /** Called when this mob's goal selectors are to be populated. */
    @Override
    protected void registerGoals() {
        goalSelector.addGoal( 0, new DestroySpawnPointGoal<>( this ) );
        goalSelector.addGoal( 1, new Destroyer.FireballAttackGoal( this ) );
        goalSelector.addGoal( 1, new DestroyerLookAroundGoal( this ) );
        goalSelector.addGoal( 2, new Destroyer.RandomOrRelativeToTargetFlyGoal( this ) );
        targetSelector.addGoal( 0, new MobHurtByTargetGoal( this, Enemy.class ) );
        targetSelector.addGoal( 1, new LunarSiegeTargetPlayerGoal<>( this, false ) );
        targetSelector.addGoal( 2, new NearestAttackableTargetGoal<>( this, Player.class, false, false ) );
    }
    
    /** Called when this entity's synchronized data items are to be defined. */
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define( ATTACKED_BY_PT, false );
    }
    
    /**
     * @return True if this Destroyer at any point has been
     * attacked by their siege target player since it spawned.
     */
    @SuppressWarnings( "BooleanMethodIsAlwaysInverted" )
    public boolean attackedBySiegeTarget() {
        return entityData.get( ATTACKED_BY_PT );
    }
    
    /** @return True if this entity currently has direct line of sight to the given entity. */
    @Override
    public boolean hasLineOfSight( Entity entity ) {
        // Can always see target
        return true;
    }
    
    /** @return True if this entity is invulnerable to the given damage source. */
    @Override
    public boolean isInvulnerableTo( DamageSource damageSource ) {
        return !AbstractLunarSiegeGhast.isDeflectedFireball( damageSource )
                && (isRemoved()
                || isInvulnerable()
                && !damageSource.is( DamageTypeTags.BYPASSES_INVULNERABILITY )
                && !damageSource.isCreativePlayer()
                || damageSource.is( DamageTypeTags.IS_FIRE )
                && fireImmune()
                || damageSource.is( DamageTypeTags.IS_FALL ));
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
        if( isInvulnerableTo( damageSource ) ) {
            return false;
        }
        if( damageSource.getEntity() instanceof Player player ) {
            // If attacked by siege target player, notify the Destroyer, so it
            // focuses on the player rather than their respawn point.
            if( getSiegeTarget() == player ) {
                entityData.set( ATTACKED_BY_PT, true );
            }
        }
        // Prevent instant fireball death and return to sender advancement
        else if( damageSource.getDirectEntity() instanceof Fireball ) {
            // Prevent the destroyer from damaging itself
            // when close up to a wall or solid obstacle
            if( damageSource.getEntity() == this )
                return false;
            
            if( damageSource.getEntity() instanceof Player player ) {
                super.hurt( damageSources().playerAttack( player ), 7.0F );
                return true;
            }
        }
        else if( damageSource.is( DamageTypeTags.IS_EXPLOSION ) && damageSource.getEntity() == this ) {
            return false;
        }
        return super.hurt( damageSource, damage );
    }
    
    /** @return The sound event this entity plays when it is hurt. This can be null. */
    @Override
    protected SoundEvent getHurtSound( DamageSource damageSource ) {
        return ApocalypseObjects.SoundEvents.DESTROYER_HURT.get();
    }
    
    /** @return The sound event this entity plays when it dies. This can be null. */
    @Override
    protected SoundEvent getDeathSound() {
        return ApocalypseObjects.SoundEvents.DESTROYER_DEATH.get();
    }
    
    /** @return The explosion power this ghast applies to its fireballs. */
    @Override // Ghast
    public int getExplosionPower() {
        return explosionPower == 0 ? ApocalypseConfig.ENTITIES.FULL_MOON.destroyerExplosionPower.get() : explosionPower;
    }
    
    /**
     * Called when this entity has been spawned into the world.
     * <br><br>
     * <strong><u>NOTE:</strong></u> Modders must call {@link net.minecraftforge.event.ForgeEventFactory#onFinalizeSpawn(Mob, ServerLevelAccessor, DifficultyInstance, MobSpawnType, SpawnGroupData, CompoundTag)}
     * instead of this method (override-only).
     *
     * @param serverLevel        The server level the entity was spawned into.
     * @param difficultyInstance The level's current difficulty data.
     * @param spawnType          The spawn type of this spawn, which provides context on what caused the spawn to happen.
     * @param spawnGroupData     A data object that normally contains relevant data for mobs that have spawned in packs. Can be null.
     * @param compoundTag        Custom NBT that the entity spawned with. Can be null.
     * @return The final/processed spawn group data. Can be null.
     */
    @SuppressWarnings( "deprecation" )
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn( ServerLevelAccessor serverLevel, DifficultyInstance difficultyInstance, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag ) {
        spawnGroupData = super.finalizeSpawn( serverLevel, difficultyInstance, spawnType, spawnGroupData, compoundTag );
        
        if( compoundTag != null ) {
            explosionPower = compoundTag.getInt( TAG_EXPLOSION_POWER );
        }
        return spawnGroupData;
    }
    
    /** @return True if the given position is within fireball attack range. */
    private boolean withinFiringRange( Vec3 pos ) {
        return horizontalDistToSqr( pos ) < 4096.0D;
    }
    
    
    /** Modified copy-paste of {@link Ghast.GhastShootFireballGoal}. */
    private static class FireballAttackGoal extends Goal {
        
        /** The owner of this goal. */
        private final Destroyer destroyer;
        /**
         * This goal's tick timer.
         * <br><br>
         * <p>0: Goal just started.</p>
         * <p>10: Play warn/shoot sound.</p>
         * <p>20: Shoot fireball and set {@code chargeTime} to -40 as a cooldown.</p>
         */
        public int chargeTime;
        
        
        /**
         * Constructs a new instance.
         *
         * @param destroyer The Destroyer entity that owns this goal.
         */
        public FireballAttackGoal( Destroyer destroyer ) {
            this.destroyer = destroyer;
        }
        
        
        /** @return True if this goal requires its logic to be updated every tick. */
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
        
        /** @return True if this goal can start running. */
        @Override
        public boolean canUse() {
            return destroyer.getTarget() != null && !destroyer.isTargetingSpawnPoint;
        }
        
        /** Called when this goal has just started running. */
        @Override
        public void start() {
            chargeTime = 0;
        }
        
        /** Called when this goal stops running. */
        @Override
        public void stop() {
            destroyer.setCharging( false );
        }
        
        /** Called to update this goal's logic. */
        @Override
        public void tick() {
            LivingEntity target = destroyer.getTarget();
            if( target == null ) return;
            
            if( destroyer.withinFiringRange( target.position() ) && destroyer.hasLineOfSight( target ) ) {
                Level level = destroyer.level();
                ++chargeTime;
                
                if( chargeTime == 10 && !destroyer.isSilent() ) {
                    level.playSound(
                            null,
                            destroyer.blockPosition(),
                            ApocalypseObjects.SoundEvents.DESTROYER_WARN.get(),
                            destroyer.getSoundSource(),
                            destroyer.getSoundVolume(),
                            (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F
                    );
                }
                if( chargeTime == 20 ) {
                    Vec3 viewVec = destroyer.getViewVector( 1.0F );
                    double x = target.getX() - (destroyer.getX() + viewVec.x * 4.0D);
                    double y = target.getY( 0.5D ) - (0.5D + destroyer.getY( 0.5D ));
                    double z = target.getZ() - (destroyer.getZ() + viewVec.z * 4.0D);
                    
                    if( !destroyer.isSilent() ) {
                        level.playSound(
                                null,
                                destroyer.blockPosition(),
                                ApocalypseObjects.SoundEvents.DESTROYER_SHOOT.get(),
                                destroyer.getSoundSource(),
                                destroyer.getSoundVolume(),
                                (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F
                        );
                    }
                    DestroyerFireballEntity fireball = new DestroyerFireballEntity( level, destroyer, x, y, z );
                    fireball.setPos( destroyer.getX() + viewVec.x * 4.0D, destroyer.getY( 0.5D ) + 0.5D, fireball.getZ() + viewVec.z * 4.0D );
                    level.addFreshEntity( fireball );
                    chargeTime = -40;
                }
            }
            else if( chargeTime > 0 ) {
                --chargeTime;
            }
            destroyer.setCharging( chargeTime > 10 );
        }
    }
    
    /**
     * A modified copy-paste of {@link Ghast.RandomFloatAroundGoal} that either picks a random position to fly to
     * if the goal owner has no current target, or attempts to follow the target otherwise.
     */
    static class RandomOrRelativeToTargetFlyGoal extends Goal {
        
        /**
         * The minimum distance that must exist between the goal owner and its target
         * before the goal owner starts following it instead of flying randomly.
         */
        private static final double minDistBeforeFollow = 3000.0D;
        /** The owner of this goal. */
        private final Destroyer destroyer;
        
        
        /**
         * Creates a new instance.
         *
         * @param destroyer The Destroyer entity that owns this goal.
         */
        public RandomOrRelativeToTargetFlyGoal( Destroyer destroyer ) {
            this.destroyer = destroyer;
            setFlags( EnumSet.of( Goal.Flag.MOVE ) );
        }
        
        /** @return True if this goal can start running. */
        @Override
        public boolean canUse() {
            MoveControl controller = destroyer.getMoveControl();
            
            if( !controller.hasWanted() ) {
                return true;
            }
            else {
                double x = controller.getWantedX() - destroyer.getX();
                double y = controller.getWantedY() - destroyer.getY();
                double z = controller.getWantedZ() - destroyer.getZ();
                double d3 = x * x + y * y + z * z;
                return d3 < 1.0D || d3 > 3600.0D;
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
            MoveControl controller = destroyer.getMoveControl();
            
            // Check if goal owner has a target
            if( destroyer.getTarget() != null ) {
                Vec3 respawnPos = null;
                
                // Check if targeting player respawn position is enabled
                if( ApocalypseConfig.ENTITIES.FULL_MOON.destroyerTargetRespawnPos.get() && destroyer.getTarget() instanceof ServerPlayer serverPlayer && !destroyer.attackedBySiegeTarget() ) {
                    if( destroyer.getSiegeTarget() == serverPlayer ) {
                        BlockPos pos = serverPlayer.getRespawnPosition();
                        
                        if( isPlayerSpawnValid( pos, destroyer.level() ) ) {
                            double x = pos.getX();
                            double y = pos.getY() + 10.0D;
                            double z = pos.getZ();
                            
                            if( destroyer.noCollisionTowards( x, y, z, 10 ) ) {
                                respawnPos = new Vec3( x, y, z );
                            }
                        }
                    }
                }
                LivingEntity target = destroyer.getTarget();
                double distToTarget = destroyer.distanceToSqr( target );
                
                // Move toward the target's respawn position, if possible
                if( respawnPos != null ) {
                    if( !destroyer.withinFiringRange( respawnPos ) )
                        controller.setWantedPosition( respawnPos.x, respawnPos.y, respawnPos.z, 1.0D );
                }
                else {
                    // Follow target if too far away
                    if( distToTarget > minDistBeforeFollow ) {
                        controller.setWantedPosition( target.getX(), target.getY() + 10.0D, target.getZ(), 1.0D );
                    }
                    // Pick a random pos to fly to otherwise
                    else {
                        setRandomWantedPosition();
                    }
                }
            }
            // Goal owner has no target, just fly randomly
            else {
                setRandomWantedPosition();
            }
        }
        
        /** Creates a new randomized position nearby for the goal owner to try and fly to. */
        private void setRandomWantedPosition() {
            RandomSource random = destroyer.getRandom();
            double x = destroyer.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double y = destroyer.getY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 10.0F);
            double z = destroyer.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            destroyer.getMoveControl().setWantedPosition( x, y, z, 1.0D );
        }
    }
    
    /**
     * A fireball attack goal that makes the Destroyer launch fireballs at
     * its lunar siege target's respawn position, if enabled.
     */
    private static class DestroySpawnPointGoal<T extends Destroyer> extends Goal {
        
        /** The owner of this goal. */
        private final T destroyer;
        /** The block position of the lunar siege target's respawn position. */
        @Nullable
        private BlockPos respawnPos;
        /**
         * This goal's tick timer.
         * <br><br>
         * <p>0: Goal just started.</p>
         * <p>10: Play warn/shoot sound.</p>
         * <p>20: Shoot fireball and set {@code chargeTime} to -40 as a cooldown.</p>
         */
        public int chargeTime;
        
        
        /**
         * Creates a new instance.
         *
         * @param destroyer The Destroyer entity that owns this goal.
         */
        private DestroySpawnPointGoal( T destroyer ) {
            this.destroyer = destroyer;
        }
        
        
        /** @return True if this goal can start running. */
        @Override
        public boolean canUse() {
            if( !ApocalypseConfig.ENTITIES.FULL_MOON.destroyerTargetRespawnPos.get() )
                return false;
            
            // This goal can only run if the goal owner has not been attacked by its siege target since spawning
            if( destroyer.getSiegeTarget() instanceof ServerPlayer targetPlayer && !destroyer.attackedBySiegeTarget() ) {
                if( targetPlayer.getRespawnPosition() != null &&
                        (targetPlayer.getRespawnDimension().equals( destroyer.level().dimension() ))
                        && isPlayerSpawnValid( targetPlayer.getRespawnPosition(), destroyer.level() ) ) {
                    respawnPos = targetPlayer.getRespawnPosition();
                    return true;
                }
            }
            return false;
        }
        
        /** @return True if this goal has started and should continue running. */
        @Override
        public boolean canContinueToUse() {
            if( !ApocalypseConfig.ENTITIES.FULL_MOON.destroyerTargetRespawnPos.get() )
                return false;
            
            if( destroyer.getSiegeTarget() instanceof ServerPlayer targetPlayer && !destroyer.attackedBySiegeTarget() ) {
                if( respawnPos != null && (targetPlayer.getRespawnDimension().equals( destroyer.level().dimension() )) ) {
                    return isPlayerSpawnValid( respawnPos, destroyer.level() );
                }
            }
            return false;
        }
        
        /** Called when this goal has just started running. */
        @Override
        public void start() {
            chargeTime = 0;
            destroyer.isTargetingSpawnPoint = true;
        }
        
        /** Called when this goal stops running. */
        @Override
        public void stop() {
            respawnPos = null;
            destroyer.setCharging( false );
            destroyer.isTargetingSpawnPoint = false;
        }
        
        /** Called to update this goal's logic. */
        @Override
        public void tick() {
            if( respawnPos == null ) return;
            
            if( destroyer.horizontalDistToSqr( respawnPos ) < 4096.0D ) {
                Level level = destroyer.level();
                ++chargeTime;
                
                if( chargeTime == 10 && !destroyer.isSilent() ) {
                    level.levelEvent( null, 1015, destroyer.blockPosition(), 0 );
                }
                if( chargeTime == 20 ) {
                    Vec3 vec3 = destroyer.getViewVector( 1.0F );
                    double x = respawnPos.getX() - (destroyer.getX() + vec3.x * 4.0D);
                    double y = respawnPos.getY() - (0.5D + destroyer.getY( 0.5D ));
                    double z = respawnPos.getZ() - (destroyer.getZ() + vec3.z * 4.0D);
                    
                    if( !destroyer.isSilent() ) {
                        level.levelEvent( null, 1016, destroyer.blockPosition(), 0 );
                    }
                    DestroyerFireballEntity fireball = new DestroyerFireballEntity( level, destroyer, x, y, z );
                    fireball.setPos( destroyer.getX() + vec3.x * 4.0D, destroyer.getY( 0.5D ) + 0.5D, fireball.getZ() + vec3.z * 4.0D );
                    level.addFreshEntity( fireball );
                    chargeTime = -40;
                }
            }
            else if( chargeTime > 0 ) {
                --chargeTime;
            }
            destroyer.setCharging( chargeTime > 10 );
        }
    }
    
    /** A goal for updating the goal owner's look angles. */
    // TODO - Maybe create a custom LookControl implementation instead of having a goal for this
    protected static class DestroyerLookAroundGoal extends Goal {
        
        /** The owner of this goal. */
        private final Destroyer destroyer;
        
        
        /**
         * Creates a new instance.
         *
         * @param destroyer The Destroyer entity that owns this goal.
         */
        public DestroyerLookAroundGoal( Destroyer destroyer ) {
            this.destroyer = destroyer;
            setFlags( EnumSet.of( Goal.Flag.LOOK ) );
        }
        
        
        /** @return True if this goal requires its logic to be updated every tick. */
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
        
        /** @return True if this goal can start running. */
        @Override
        public boolean canUse() {
            return true;
        }
        
        /** Called to update this goal's logic. */
        public void tick() {
            if( destroyer.getTarget() == null ) {
                Vec3 vec3 = destroyer.getDeltaMovement();
                destroyer.setYRot( -((float) Mth.atan2( vec3.x, vec3.z )) * (180F / (float) Math.PI) );
            }
            else if( destroyer.getTarget() instanceof ServerPlayer serverPlayer ) {
                double x, z;
                
                if( !destroyer.attackedBySiegeTarget() && destroyer.isTargetingSpawnPoint
                        && serverPlayer.getRespawnPosition() != null && (serverPlayer.getRespawnDimension().equals( destroyer.level().dimension() ))
                        && isPlayerSpawnValid( serverPlayer.getRespawnPosition(), destroyer.level() ) ) {
                    BlockPos respawnPos = serverPlayer.getRespawnPosition();
                    x = respawnPos.getX() - destroyer.getX();
                    z = respawnPos.getZ() - destroyer.getZ();
                }
                else {
                    x = serverPlayer.getX() - destroyer.getX();
                    z = serverPlayer.getZ() - destroyer.getZ();
                }
                destroyer.setYRot( -((float) Mth.atan2( x, z )) * (180F / (float) Math.PI) );
            }
            else {
                LivingEntity target = destroyer.getTarget();
                
                double x = target.getX() - destroyer.getX();
                double z = target.getZ() - destroyer.getZ();
                destroyer.setYRot( -((float) Mth.atan2( x, z )) * (180F / (float) Math.PI) );
            }
            destroyer.yBodyRot = destroyer.getYRot();
        }
    }
    
    /**
     * Checks if the player's respawn point is still valid
     * after possibly having been destroyed.
     */
    private static boolean isPlayerSpawnValid( @Nullable BlockPos pos, Level level ) {
        if( pos == null ) return false;
        Block block = level.getBlockState( pos ).getBlock();
        return block instanceof BedBlock || block instanceof RespawnAnchorBlock;
    }
}