package com.toast.apocalypse.common.mixin;

import com.toast.apocalypse.common.misc.mixin_work.ServerMixinHooks;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraftforge.common.extensions.IForgeWorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements ISeedReader, IForgeWorldServer {

    protected ServerWorldMixin(ISpawnWorldInfo spawnWorldInfo, RegistryKey<World> dimension, DimensionType dimensionType, Supplier<IProfiler> profilerSupplier, boolean clientSide, boolean debug, long seed) {
        super(spawnWorldInfo, dimension, dimensionType, profilerSupplier, clientSide, debug, seed);
    }


    @Final
    @Inject(method = "setDayTime", at = @At("HEAD"), cancellable = true)
    public void onSetDayTime(long time, CallbackInfo ci) {
        ServerMixinHooks.onServerWorldSetDayTime((ServerWorld) (Object) this, ci);
    }
}
