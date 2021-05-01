package com.toast.apocalypse.common.entity.living;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.world.World;

/**
 * This is a full moon mob identical to a creeper in almost every way, except that it has a much farther aggro range
 * that ignores line of sight, moves slightly faster and will explode when they detect that they can't get any closer to the player.<br>
 * Visually, the ony difference is that their eyes are entranced by the moon's power.
 */
public class BreecherEntity extends CreeperEntity {

    public BreecherEntity(EntityType<? extends CreeperEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createBreecherAttributes() {
        return CreeperEntity.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.FOLLOW_RANGE, 40);
    }
}
