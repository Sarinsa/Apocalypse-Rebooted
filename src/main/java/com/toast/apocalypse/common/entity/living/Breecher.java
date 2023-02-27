package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.common.entity.living.ai.MobHurtByTargetGoal;
import com.toast.apocalypse.common.entity.living.ai.MoonMobPlayerTargetGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This is a full moon mob identical to a creeper in almost every way, except that it has a much farther aggro range
 * that ignores line of sight, moves slightly faster and will explode when they detect that they can't get any closer to the player.<br>
 * Visually, the ony difference is that their eyes are entranced by the moon's power.
 */
public class Breecher extends Creeper implements IFullMoonMob {

    /** The constant player target, if this mob was spawned by the full moon event */
    private UUID playerTargetUUID;
    protected int eventGeneration = 0;

    public Breecher(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createBreecherAttributes() {
        return Creeper.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.FOLLOW_RANGE, 40);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SwellGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new MobHurtByTargetGoal(this, Enemy.class));
        this.targetSelector.addGoal(1, new MoonMobPlayerTargetGoal<>(this, false));
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
    }

    /**
     * Completely ignore line of sight; the target
     * is always "visible"
     */
    @Override
    public boolean hasLineOfSight(Entity entity) {
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;

            if (IFullMoonMob.shouldDisappear(getPlayerTargetUUID(), serverLevel, this)) {
                IFullMoonMob.spawnSmoke(serverLevel, this);
                discard();
            }
        }
    }

    @Nullable
    @Override
    public UUID getPlayerTargetUUID() {
        return this.playerTargetUUID;
    }

    @Override
    public void setPlayerTargetUUID(@Nullable UUID playerTargetUUID) {
        this.playerTargetUUID = playerTargetUUID;
    }

    @Override
    public int getEventGeneration() {
        return eventGeneration;
    }

    @Override
    public void setEventGeneration(int generation) {
        eventGeneration = generation;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);

        if (this.getPlayerTargetUUID() != null) {
            compoundTag.putUUID(PLAYER_UUID_KEY, this.getPlayerTargetUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);

        if (compoundTag.hasUUID(PLAYER_UUID_KEY)) {
            this.setPlayerTargetUUID(compoundTag.getUUID(PLAYER_UUID_KEY));
        }
    }
}
