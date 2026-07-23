package com.toast.apocalypse.common.misc.mixin_work;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.config.ApocalypseConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ServerMixinHooks {
    
    public static void onServerWorldSetDayTime( ServerLevel serverLevel, CallbackInfo ci ) {
        if( ApocalypseConfig.MISC.OTHER.pauseDaylightCycle.get() && serverLevel.getServer().getPlayerCount() < 1 ) {
            ci.cancel();
        }
    }
    
    public static int modifySnowAccumulation( ServerLevel serverLevel, BlockPos pos, int original ) {
        Biome biome = serverLevel.getBiome( pos ).get();
        
        if( biome.shouldSnow( serverLevel, pos ) && !ApocalypseConfig.ACID_RAIN.GENERAL.acidSnowAccumulates.get() ) {
            return 0;
        }
        return original;
    }
    
    public static void onTickChunk( ServerLevel serverLevel, LevelChunk levelChunk, CallbackInfo ci ) {
        final int minChunkX = levelChunk.getPos().getMinBlockX();
        final int minChunkZ = levelChunk.getPos().getMinBlockZ();
        
        // Pick random XZ coordinates in the chunk and
        // get the top position in the column as well as the highest solid block position
        BlockPos basePos = serverLevel.getBlockRandomPos( minChunkX, 0, minChunkZ, 15 );
        
        BlockPos topPos = serverLevel.getHeightmapPos(
                Heightmap.Types.WORLD_SURFACE,
                basePos
        ).below();
        
        // If its raining and wet torches are enabled, check if we are on a torch block and fizzle it out
        if( ApocalypseConfig.MISC.OTHER.rainFizzlesTorches.get() && serverLevel.isRainingAt( topPos ) ) {
            maybeFizzleTorch( serverLevel, topPos );
        }
        
        // Proceed to check if we should do acid rain stuff
        if( !ApocalypseConfig.ACID_RAIN.WORLD_DEGRADATION.enableBlockDegradation.get() ) return;
        if( ApocalypseConfig.ACID_RAIN.WORLD_DEGRADATION.blockTransformations.isEmpty() ) return;
        if( !Apocalypse.INSTANCE.getDifficultyManager().isRainingAcid( serverLevel ) ) return;
        if( serverLevel.random.nextInt( 15 ) != 0 ) return;
        
        BlockPos topSolidPos = serverLevel.getHeightmapPos(
                Heightmap.Types.MOTION_BLOCKING,
                basePos
        ).below();
        
        // Make sure we don't accidentally load neighboring chunks
        if( !serverLevel.isAreaLoaded( topPos, 1 ) ) return;
        
        // Corrode top block
        corrodeBlock( serverLevel, topPos );
        
        // If the first block was not motion-blocking,
        // and we have a solid block underneath somewhere,
        // try and corrode at that position as well.
        if( !topPos.equals( topSolidPos ) ) {
            corrodeBlock( serverLevel, topSolidPos );
        }
    }
    
    private static void corrodeBlock( ServerLevel level, BlockPos pos ) {
        Biome biome = level.getBiome( pos ).value();
        
        if( biome.getPrecipitationAt( pos ) == Biome.Precipitation.NONE ) return;
        
        // If we are in a snowy place but acid snow isn't enabled, return
        if( biome.getPrecipitationAt( pos ) == Biome.Precipitation.SNOW && !ApocalypseConfig.ACID_RAIN.GENERAL.acidSnow.get() )
            return;
        
        BlockState currentState = level.getBlockState( pos );
        BlockState resultState = ApocalypseConfig.ACID_RAIN.WORLD_DEGRADATION.blockTransformations.getResultFor( currentState );
        
        if( resultState != null ) {
            level.setBlockAndUpdate( pos, resultState );
        }
    }
    
    private static void maybeFizzleTorch( ServerLevel serverLevel, BlockPos pos ) {
        BlockState stateAtPos = serverLevel.getBlockState( pos );
        
        if( stateAtPos.is( Blocks.TORCH ) ) {
            serverLevel.setBlockAndUpdate( pos, ApocalypseObjects.Blocks.WET_TORCH.get().defaultBlockState() );
        }
        else if( stateAtPos.is( Blocks.WALL_TORCH ) ) {
            BlockState wallTorch = ApocalypseObjects.Blocks.WET_WALL_TORCH.get().defaultBlockState();
            
            try {
                wallTorch = wallTorch.setValue( WallTorchBlock.FACING, stateAtPos.getValue( WallTorchBlock.FACING ) );
            }
            catch( Exception ignored ) {
                Apocalypse.LOGGER.warn( "Failed to copy block state facing property of wet wall torch to a vanilla wall torch. " +
                        "This should normally work!" );
            }
            serverLevel.setBlockAndUpdate( pos, wallTorch );
        }
    }
}
