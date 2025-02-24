package com.toast.apocalypse.common.mixin;

import com.toast.apocalypse.common.misc.mixin_work.CommonMixinHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {

    /**
     * The purpose of this injector is to effectively skip checking
     * if the block an entity is trying to spawn on is a valid spawn,
     * allowing mobs to spawn on pretty much any block as long as it
     * has collision.<br><br>
     * See {@link CommonMixinHooks#onCanSpawnAtBody(SpawnPlacements.Type, LevelReader, BlockPos, EntityType, CallbackInfoReturnable)} for more details.
     */
    @Inject(
            method = "isSpawnPositionOk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/SpawnPlacements$Type;canSpawnAt(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntityType;)Z"),
            cancellable = true
    )
    private static void onIsSpawnPositionOk(SpawnPlacements.Type type, LevelReader levelReader,
                                         BlockPos pos, EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir) {
        CommonMixinHooks.onCanSpawnAtBody(type, levelReader, pos, entityType, cir);
    }
}
