package com.toast.apocalypse.common.entity.living;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This is a full moon mob identical to a creeper in almost every way, except that it has a much farther aggro range
 * that ignores line of sight, moves slightly faster and will explode when they detect that they can't get any closer to the player.<br>
 * Visually, the ony difference is that their eyes are entranced by the moon's power.
 */
public class BreecherEntity extends CreeperEntity implements IFullMoonMob {

    private boolean exploding;

    public BreecherEntity(EntityType<? extends CreeperEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createBreecherAttributes() {
        return CreeperEntity.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.FOLLOW_RANGE, 40);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.shouldExplode() && this.isAlive()) {
            this.explodeCreeper();
        }
    }

    /** @return True if this breecher should explode. */
    public boolean shouldExplode() {
        if (this.exploding)
            return true;

        LivingEntity target = this.getTarget();

        if (target == null)
            return false;

        double dX = target.getX() - this.getX();
        double dZ = target.getZ() - this.getZ();

        float range = (target.getBbWidth() + this.getBbWidth()) / 2.0F;

        if (target.getBoundingBox().maxY <= this.getBoundingBox().minY && dX * dX + dZ * dZ < range * range && this.xo * this.xo + this.zo * this.zo < 0.1)
            return this.exploding = true; // right above, moving slow or stopped

        if (dX < 0.0 && this.xo > 0.0 || dX > 0.0 && this.xo < 0.0 || dZ < 0.0 && this.zo > 0.0 || dZ > 0.0 && this.zo < 0.0)
            return false; // moving away, presumably to path

        double dH = Math.sqrt(dX * dX + dZ * dZ) * 2.0;
        dX /= dH;
        dZ /= dH;

        double vX = dX;
        double vZ = dZ;

        List<VoxelShape> list = this.getCommandSenderWorld().getCollisions(this, this.getBoundingBox().inflate(dX, 0.0, dZ), null).collect(Collectors.toList());

        /*
        for (VoxelShape box : list) {
            vX = box.bounds().calculateXOffset(this.getBoundingBox(), vX);
            vZ = box.bounds().calculateZOffset(this.getBoundingBox(), vZ);
        }

         */

        boolean hitX = vX != dX;
        boolean hitZ = vZ != dZ;
        if (!hitX && !hitZ)
            return false; // not even by anything to jump over

        list = this.getCommandSenderWorld().getCollisions(this, this.getBoundingBox().inflate(dX, 1.0, dZ), null).collect(Collectors.toList());
        vX = dX;
        vZ = dZ;

        /*
        for (VoxelShape box : list) {
            vX = box.bounds().calculateXOffset(this.getBoundingBox(), vX);
            vZ = box.bounds().calculateZOffset(this.getBoundingBox(), vZ);
        }

         */
        hitX = hitX && vX != dX;
        hitZ = hitZ && vZ != dZ;

        AxisAlignedBB boundingBox = this.getBoundingBox();
        AxisAlignedBB targetBoundingBox = target.getBoundingBox();

        if (!hitX && !hitZ)
            return false; // not by anything at all
        if (hitX && hitZ)
            return this.exploding = true; // in corner
        if (hitX && targetBoundingBox.maxZ > boundingBox.minZ && targetBoundingBox.minZ < boundingBox.maxZ)
            return this.exploding = true; // z-aligned
        if (hitZ && targetBoundingBox.maxX > boundingBox.minX && targetBoundingBox.minX < boundingBox.maxX)
            return this.exploding = true; // x-aligned
        return false; // blocked, but not close enough
    }
}
