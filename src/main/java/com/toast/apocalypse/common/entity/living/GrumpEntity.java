package com.toast.apocalypse.common.entity.living;

import com.toast.apocalypse.api.IFullMoonMob;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import java.util.Random;

/**
 * This is a full moon mob that is meant to be a high threat to players that are not in a safe area from them.
 * Grumps fly, have a pulling attack, and have a melee attack that can't be reduced below 2 damage and applies a
 * short gravity effect.<br>
 * Unlike most full moon mobs, this one has no means of breaking through defenses and therefore relies on the
 * player being vulnerable to attack - whether by will or by other mobs breaking through to the player.
 */
public class GrumpEntity extends GhastEntity implements IFullMoonMob {

    public GrumpEntity(EntityType<? extends GhastEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createGrumpAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FLYING_SPEED, 0.9D)
                .add(Attributes.FOLLOW_RANGE, Double.POSITIVE_INFINITY);
    }

    public static boolean checkGrumpSpawnRules(EntityType<? extends GrumpEntity> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && MobEntity.checkMobSpawnRules(entityType, world, spawnReason, pos, random);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize entitySize) {
        return 1.75F;
    }
}
