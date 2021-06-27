package com.toast.apocalypse.common.entity.living;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

/**
 * This is a full moon mob identical to a creeper in almost every way, except that it has a much farther aggro range
 * that ignores line of sight, moves slightly faster and will explode when they detect that they can't get any closer to the player.<br>
 * Visually, the ony difference is that their eyes are entranced by the moon's power.
 */
public class BreecherEntity extends CreeperEntity implements IFullMoonMob {

    /** The constant player target, if this mob was spawned by the full moon event */
    private PlayerEntity playerTarget;

    public BreecherEntity(EntityType<? extends CreeperEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createBreecherAttributes() {
        return CreeperEntity.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.FOLLOW_RANGE, 40);
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

        if (this.shouldExplode() && this.isAlive()) {
            this.ignite();
        }
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
    public PlayerEntity getPlayerTarget() {
        return this.playerTarget;
    }

    @Override
    public void setPlayerTarget(PlayerEntity playerTarget) {
        this.playerTarget = playerTarget;
    }
}
