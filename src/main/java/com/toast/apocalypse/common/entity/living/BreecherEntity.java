package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.common.entity.living.goals.MobEntityAttackedByTargetGoal;
import com.toast.apocalypse.common.entity.living.goals.MoonMobPlayerTargetGoal;
import com.toast.apocalypse.common.util.MobWikiIndexes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This is a full moon mob identical to a creeper in almost every way, except that it has a much farther aggro range
 * that ignores line of sight, moves slightly faster and will explode when they detect that they can't get any closer to the player.<br>
 * Visually, the ony difference is that their eyes are entranced by the moon's power.
 */
public class BreecherEntity extends CreeperEntity implements IFullMoonMob {

    /** The constant player target, if this mob was spawned by the full moon event */
    private UUID playerTargetUUID;

    public BreecherEntity(EntityType<? extends CreeperEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createBreecherAttributes() {
        return CreeperEntity.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.FOLLOW_RANGE, 40);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new CreeperSwellGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, OcelotEntity.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, CatEntity.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(0, new MobEntityAttackedByTargetGoal(this, IMob.class));
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
    public boolean canSee(Entity entity) {
        return true;
    }


    @Override
    public void tick() {
        super.tick();
    }

    /** @return True if this breecher should explode. */
    public boolean shouldExplode() {
        if (this.getTarget() != null && this.navigation.getPath() != null) {
            Path path = this.navigation.getPath();
            PathNavigator navigator = this.getNavigation();

            return !path.canReach();
        }
        return false;
    }

    @Nullable
    @Override
    public UUID getPlayerTargetUUID() {
        return this.playerTargetUUID;
    }

    @Override
    public void setPlayerTargetUUID(UUID playerTargetUUID) {
        this.playerTargetUUID = playerTargetUUID;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);

        if (this.getPlayerTargetUUID() != null) {
            compoundNBT.putUUID(PLAYER_UUID_KEY, this.getPlayerTargetUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);

        if (compoundNBT.hasUUID(PLAYER_UUID_KEY)) {
            this.setPlayerTargetUUID(compoundNBT.getUUID(PLAYER_UUID_KEY));
        }
    }
}
