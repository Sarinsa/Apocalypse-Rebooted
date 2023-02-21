package com.toast.apocalypse.common.entity.living.ai;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/** A copy of HurtByTargetGoal except it is not restricted to creature entities */
public class MobEntityAttackedByTargetGoal extends TargetGoal {

    private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    private boolean alertSameType;
    private int timestamp;
    private final Class<?>[] toIgnoreDamage;
    private Class<?>[] toIgnoreAlert;

    public MobEntityAttackedByTargetGoal(Mob mob, Class<?>... toIgnoreDamage) {
        super(mob, true);
        this.toIgnoreDamage = toIgnoreDamage;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        int i = mob.getLastHurtByMobTimestamp();
        LivingEntity livingentity = mob.getLastHurtByMob();

        if (i != timestamp && livingentity != null) {
            if (livingentity.getType() == EntityType.PLAYER && mob.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                return false;
            }
            else {
                for(Class<?> clazz : toIgnoreDamage) {
                    if (clazz.isAssignableFrom(livingentity.getClass())) {
                        return false;
                    }
                }
                return canAttack(livingentity, HURT_BY_TARGETING);
            }
        }
        else {
            return false;
        }
    }

    public MobEntityAttackedByTargetGoal setAlertOthers(Class<?>... toIgnoreAlert) {
        alertSameType = true;
        this.toIgnoreAlert = toIgnoreAlert;
        return this;
    }

    @Override
    public void start() {
        mob.setTarget(mob.getLastHurtByMob());
        targetMob = mob.getTarget();
        timestamp = mob.getLastHurtByMobTimestamp();
        unseenMemoryTicks = 300;

        if (alertSameType) {
            alertOthers();
        }
        super.start();
    }

    protected void alertOthers() {
        double followDistance = getFollowDistance();
        AABB aabb = AABB.unitCubeFromLowerCorner(mob.position()).inflate(followDistance, 10.0D, followDistance);
        Iterator<? extends Mob> iterator = mob.level.getEntitiesOfClass(mob.getClass(), aabb).iterator();

        while(true) {
            Mob mobEntity;
            while(true) {
                if (!iterator.hasNext()) {
                    return;
                }
                mobEntity = iterator.next();
                if (mob != mobEntity && mobEntity.getTarget() == null && (!(mob instanceof TamableAnimal) || ((TamableAnimal) mobEntity).getOwner() == ((TamableAnimal) mobEntity).getOwner()) && !mobEntity.isAlliedTo(mob.getLastHurtByMob())) {
                    if (toIgnoreAlert == null) {
                        break;
                    }

                    boolean flag = false;

                    for(Class<?> clazz : toIgnoreAlert) {
                        if (mobEntity.getClass() == clazz) {
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        break;
                    }
                }
            }
            alertOther(mobEntity, mob.getLastHurtByMob());
        }
    }

    protected void alertOther(Mob mob, LivingEntity livingEntity) {
        mob.setTarget(livingEntity);
    }
}
