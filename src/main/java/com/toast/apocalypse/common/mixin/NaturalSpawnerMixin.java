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

@Mixin( NaturalSpawner.class )
public abstract class NaturalSpawnerMixin {
    
    /**
     * The purpose of this injector is to omit the block state check when the natural spawner looks
     * for a valid position to spawn a mob ({@link net.minecraftforge.common.extensions.IForgeBlockState#isValidSpawn(LevelReader, BlockPos, SpawnPlacements.Type, EntityType)})
     * <br><br>
     * Instead, we only check if the below block state has a collision box and the fluid state is empty, and return true if so.
     * <br><br>
     * See {@link CommonMixinHooks#onCanSpawnAtBody(SpawnPlacements.Type, LevelReader, BlockPos, EntityType, CallbackInfoReturnable)} for more details.
     */
    @Inject(
            method = "isSpawnPositionOk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/SpawnPlacements$Type;canSpawnAt(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntityType;)Z" ),
            cancellable = true
    )
    private static void onIsSpawnPositionOk( SpawnPlacements.Type type, LevelReader levelReader,
                                             BlockPos pos, EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir ) {
        CommonMixinHooks.onCanSpawnAtBody( type, levelReader, pos, entityType, cir );
    }
}
