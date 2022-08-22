package com.toast.apocalypse.common.entity.living.goals;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.GameRules;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/** A copy of HurtByTargetGoal except it is not restricted to creature entities */
public class MobEntityAttackedByTargetGoal extends TargetGoal {

    private static final EntityPredicate HURT_BY_TARGETING = (new EntityPredicate()).allowUnseeable().ignoreInvisibilityTesting();
    private boolean alertSameType;
    private int timestamp;
    private final Class<?>[] toIgnoreDamage;
    private Class<?>[] toIgnoreAlert;

    public MobEntityAttackedByTargetGoal(MobEntity mobEntity, Class<?>... toIgnoreDamage) {
        super(mobEntity, true);
        this.toIgnoreDamage = toIgnoreDamage;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        int i = this.mob.getLastHurtByMobTimestamp();
        LivingEntity livingentity = this.mob.getLastHurtByMob();

        if (i != this.timestamp && livingentity != null) {
            if (livingentity.getType() == EntityType.PLAYER && this.mob.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                return false;
            }
            else {
                for(Class<?> clazz : this.toIgnoreDamage) {
                    if (clazz.isAssignableFrom(livingentity.getClass())) {
                        return false;
                    }
                }
                return this.canAttack(livingentity, HURT_BY_TARGETING);
            }
        }
        else {
            return false;
        }
    }

    public MobEntityAttackedByTargetGoal setAlertOthers(Class<?>... toIgnoreAlert) {
        this.alertSameType = true;
        this.toIgnoreAlert = toIgnoreAlert;
        return this;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.mob.getLastHurtByMob());
        this.targetMob = this.mob.getTarget();
        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;

        if (this.alertSameType) {
            this.alertOthers();
        }
        super.start();
    }

    protected void alertOthers() {
        double followDistance = this.getFollowDistance();
        AxisAlignedBB axisalignedbb = AxisAlignedBB.unitCubeFromLowerCorner(this.mob.position()).inflate(followDistance, 10.0D, followDistance);
        List<MobEntity> list = this.mob.level.getLoadedEntitiesOfClass(this.mob.getClass(), axisalignedbb);
        Iterator<MobEntity> iterator = list.iterator();

        while(true) {
            MobEntity mobentity;
            while(true) {
                if (!iterator.hasNext()) {
                    return;
                }

                mobentity = iterator.next();
                if (this.mob != mobentity && mobentity.getTarget() == null && (!(this.mob instanceof TameableEntity) || ((TameableEntity)this.mob).getOwner() == ((TameableEntity)mobentity).getOwner()) && !mobentity.isAlliedTo(this.mob.getLastHurtByMob())) {
                    if (this.toIgnoreAlert == null) {
                        break;
                    }

                    boolean flag = false;

                    for(Class<?> clazz : this.toIgnoreAlert) {
                        if (mobentity.getClass() == clazz) {
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        break;
                    }
                }
            }

            this.alertOther(mobentity, this.mob.getLastHurtByMob());
        }
    }

    protected void alertOther(MobEntity mobEntity, LivingEntity livingEntity) {
        mobEntity.setTarget(livingEntity);
    }
}
