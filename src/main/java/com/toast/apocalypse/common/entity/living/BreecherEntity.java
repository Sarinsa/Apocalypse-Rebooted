package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.common.register.ApocalypseEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

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

    public BreecherEntity(World world, PlayerEntity playerTarget) {
        super(ApocalypseEntities.BREECHER.get(), world);
        this.playerTarget = playerTarget;
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
