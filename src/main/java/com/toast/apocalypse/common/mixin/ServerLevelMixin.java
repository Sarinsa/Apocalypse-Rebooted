package com.toast.apocalypse.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.toast.apocalypse.common.misc.mixin_work.ServerMixinHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements WorldGenLevel {


    protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> levelId, RegistryAccess registryAccess,
                               Holder<DimensionType> dimensionType, Supplier<ProfilerFiller> profilerFiller, boolean isClient, boolean isDebug, long seed, int maxChainedNeighborUpdates) {
        super(levelData, levelId, registryAccess, dimensionType, profilerFiller, isClient, isDebug, seed, maxChainedNeighborUpdates);
    }


    @Final
    @Inject(
            method = "setDayTime",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onSetDayTime(long time, CallbackInfo ci) {
        ServerMixinHooks.onServerWorldSetDayTime((ServerLevel) (Object) this, ci);
    }

    @Inject(
            method = "tickChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    ordinal = 0
            )
    )
    public void onTickChunk(LevelChunk levelChunk, int randomTickSpeed, CallbackInfo ci) {
        ServerMixinHooks.onTickChunk((ServerLevel) (Object) this, levelChunk, ci);
    }

    @ModifyExpressionValue(
            method = "tickChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/GameRules;getInt(Lnet/minecraft/world/level/GameRules$Key;)I"
            )
    )
    public int tickChunk_modify_snow_accumulation(int original, @Local(index = 8, ordinal = 0) BlockPos pos) {
        return ServerMixinHooks.modifySnowAccumulation((ServerLevel)(Object) this, pos, original);
    }
}
