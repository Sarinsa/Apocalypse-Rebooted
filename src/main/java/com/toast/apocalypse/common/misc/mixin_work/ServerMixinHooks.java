package com.toast.apocalypse.common.misc.mixin_work;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ServerMixinHooks {

    public static void onServerWorldSetDayTime(ServerLevel serverLevel, CallbackInfo ci) {
        if (ApocalypseConfig.MISC.OTHER.pauseDaylightCycle.get() && serverLevel.getServer().getPlayerCount() < 1) {
            ci.cancel();
        }
    }

    public static void onTickChunk(ServerLevel serverLevel, LevelChunk levelChunk, int randomTickSpeed, CallbackInfo ci) {
        if (!ApocalypseConfig.ACID_RAIN.WORLD_DEGRADATION.enableBlockDegradation.get()) return;
        if (ApocalypseConfig.ACID_RAIN.WORLD_DEGRADATION.blockTransformations.isEmpty()) return;
        if (!Apocalypse.INSTANCE.getDifficultyManager().isRainingAcid(serverLevel)) return;
        if (serverLevel.random.nextInt(15) != 0) return;

        final int minChunkX = levelChunk.getPos().getMinBlockX();
        final int minChunkZ = levelChunk.getPos().getMinBlockZ();

        // Pick the below position of a random xz position in the chunk that is exposed to the sky
        BlockPos pos = serverLevel.getHeightmapPos(
                Heightmap.Types.WORLD_SURFACE,
                serverLevel.getBlockRandomPos(minChunkX, 0, minChunkZ, 15)
        ).below();

        // Make sure we don't accidentally load neighboring chunks
        if (!serverLevel.isAreaLoaded(pos, 1)) return;

        Biome biome = serverLevel.getBiome(pos).value();

        if (biome.getPrecipitationAt(pos) != Biome.Precipitation.NONE) {
            BlockState currentState = serverLevel.getBlockState(pos);
            BlockState resultState = ApocalypseConfig.ACID_RAIN.WORLD_DEGRADATION.blockTransformations.getResultFor(currentState);

            if (resultState != null) {
                serverLevel.setBlockAndUpdate(pos, resultState);
            }
        }
    }
}
