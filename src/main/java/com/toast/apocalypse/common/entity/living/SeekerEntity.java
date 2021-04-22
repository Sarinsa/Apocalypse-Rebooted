package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.common.entity.IFullMoonMob;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.World;

/**
 * This is a full moon mob whose entire goal in life is to break through your defenses. It is similar to a ghast, only
 * it has unlimited target range that ignores line of sight and shoots creeper-sized fireballs when it does not have a
 * clear line of sight. When it does have direct vision, it shoots much weaker fireballs that can be easily reflected
 * back at the seeker. The seeker also alerts nearby monsters of the player's whereabouts when in it's direct line of sight.
 */
public class SeekerEntity extends GhastEntity implements IMob, IFullMoonMob {

    public SeekerEntity(EntityType<? extends GhastEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createSeekerAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.FOLLOW_RANGE, Double.POSITIVE_INFINITY);
    }
}
