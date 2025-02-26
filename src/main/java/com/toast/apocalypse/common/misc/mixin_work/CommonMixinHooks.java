package com.toast.apocalypse.common.misc.mixin_work;

import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import com.toast.apocalypse.common.core.difficulty.MobAttributeHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommonMixinHooks {


    public static float livingEntityHurtModifyArg(DamageSource damageSource, Player player, float originalDamage) {
        Entity entity = damageSource.getEntity();

        if (entity instanceof LivingEntity && !(entity instanceof Player)) {
            return MobAttributeHandler.getLivingDamage((LivingEntity) entity, player, originalDamage);
        }
        return originalDamage;
    }

    public static void capLingeringCloudEffectDurations(AreaEffectCloud effectCloud) {
        for (MobEffectInstance effectInstance : effectCloud.effects) {
            if (effectInstance.isInfiniteDuration())
                effectInstance.duration = 600;
        }
    }

    /**
     * Used by {@link com.toast.apocalypse.common.mixin.NaturalSpawnerMixin} to skip
     * the block state "is valid spawn" predicate.<br><br>
     * The custom check is only run if it is thundering in the world and Thunderstorm
     * event is enabled for Apocalypse.
     */
    public static void onCanSpawnAtBody(SpawnPlacements.Type type, LevelReader levelReader,
                                        BlockPos pos, EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir) {
        if (type == SpawnPlacements.Type.NO_RESTRICTIONS || type == SpawnPlacements.Type.ON_GROUND) {
            if (levelReader instanceof Level level && ApocalypseConfig.THUNDERSTORM.GENERAL.enabled.get() && level.isThundering()) {
                BlockPos belowPos = pos.below();
                BlockState state = level.getBlockState(pos);
                BlockState belowState = level.getBlockState(belowPos);
                boolean bool = !belowState.getCollisionShape(level, belowPos).isEmpty() && belowState.getFluidState().isEmpty() && state.getFluidState().isEmpty();
                cir.setReturnValue(bool);
            }
        }
    }
}
