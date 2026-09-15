package com.toast.apocalypse.common.block;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class WetWallTorchBlock extends WallTorchBlock {
    
    /** This wet wall torch block's torch type. */
    private final WetTorchBlock.Type type;
    
    
    public WetWallTorchBlock( WetTorchBlock.Type type, BlockBehaviour.Properties properties, ParticleOptions flameParticle ) {
        super( properties, flameParticle );
        this.type = type;
    }
    
    @Override
    public ItemStack getCloneItemStack( BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player ) {
        return new ItemStack( type.parentTorchBlock() );
    }
    
    @Override
    public void animateTick( BlockState state, Level level, BlockPos pos, RandomSource random ) {
        Direction facing = state.getValue( FACING );
        double x = (double) pos.getX() + 0.5D;
        double y = (double) pos.getY() + 0.7D;
        double z = (double) pos.getZ() + 0.5D;
        double vOffset = 0.22D;
        double hOffset = 0.27D;
        Direction oppositeDir = facing.getOpposite();
        
        level.addParticle(
                ParticleTypes.SMOKE,
                x + vOffset * (double) oppositeDir.getStepX(),
                y + hOffset,
                z + vOffset * (double) oppositeDir.getStepZ(),
                0.0D, 0.0D, 0.0D );
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public void randomTick( BlockState state, ServerLevel level, BlockPos pos, RandomSource random ) {
        if( level.isRainingAt( pos ) ) return;
        BlockState vanillaTorch = type.parentWallTorchBlock().defaultBlockState();
        
        try {
            vanillaTorch = vanillaTorch.setValue( FACING, state.getValue( FACING ) );
        }
        catch( Exception ignored ) {
            Apocalypse.LOGGER.warn( "Failed to copy block state facing property of wet wall torch to a parent wall torch. " +
                    "This should normally work!" );
        }
        level.setBlockAndUpdate( pos, vanillaTorch );
    }
}
