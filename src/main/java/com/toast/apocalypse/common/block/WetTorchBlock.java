package com.toast.apocalypse.common.block;

import com.toast.apocalypse.common.core.register.ApocalypseBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public class WetTorchBlock extends TorchBlock {
    
    public enum Type {
        NORMAL( "normal", Blocks.TORCH, Blocks.WALL_TORCH ),
        SOUL( "soul", Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH );
        
        private final String ID;
        private final TorchBlock PARENT_TORCH;
        private final TorchBlock PARENT_WALL_TORCH;
        
        Type( String id, Block parentTorch, Block parentWallTorch ) {
            ID = id;
            PARENT_TORCH = (TorchBlock) parentTorch;
            PARENT_WALL_TORCH = (TorchBlock) parentWallTorch;
        }
        
        /** @return The block id for this type's wet torch. */
        public String torchId() { return "wet_" + ID + "_torch"; }
        
        /** @return The block id for this type's wet wall torch. */
        public String wallTorchId() { return "wet_" + ID + "_wall_torch"; }
        
        /** @return A new standing torch block for this type. */
        public Block torchSupplier() {
            return new WetTorchBlock( this, Properties.copy( PARENT_TORCH ), PARENT_TORCH.flameParticle );
        }
        
        /** @return A new wall torch block for this type. */
        public Block wallTorchSupplier() {
            return new WetWallTorchBlock( this, Properties.copy( PARENT_WALL_TORCH ), PARENT_WALL_TORCH.flameParticle );
        }
        
        /** @return The 'parent torch block' of this type's wet torch block. */
        public Block parentTorchBlock() { return PARENT_TORCH; }
        
        /** @return The 'parent wall torch block' of this type's wet wall torch block. */
        public TorchBlock parentWallTorchBlock() { return PARENT_WALL_TORCH; }
        
        /** @return The 'wet torch block' of this type. */
        public Block torchBlock() { return ApocalypseBlocks.WET_TORCHES.get( ordinal() ).getFirst().get(); }
        
        /** @return The 'wet wall torch block' of this type. */
        public Block wallTorchBlock() { return ApocalypseBlocks.WET_TORCHES.get( ordinal() ).getSecond().get(); }
        
        /** @return The wet torch type that is associated with the given block, or null if no match is found. */
        @Nullable
        public static Type forVanillaTorch( Block block ) {
            for( Type type : Type.values() ) {
                if( type.parentWallTorchBlock() == block || type.parentTorchBlock() == block ) {
                    return type;
                }
            }
            return null;
        }
    }
    
    /** This wet torch block's torch type. */
    private final Type type;
    
    public WetTorchBlock( Type type, BlockBehaviour.Properties properties, ParticleOptions flameParticle ) {
        super( properties, flameParticle );
        this.type = type;
    }
    
    @Override
    public ItemStack getCloneItemStack( BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player ) {
        return new ItemStack( type.parentTorchBlock() );
    }
    
    @Override
    public void animateTick( BlockState state, Level level, BlockPos pos, RandomSource random ) {
        if( random.nextInt( 3 ) == 0 ) {
            double x = (double) pos.getX() + 0.5D;
            double y = (double) pos.getY() + 0.7D;
            double z = (double) pos.getZ() + 0.5D;
            level.addParticle( ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D );
        }
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public void randomTick( BlockState state, ServerLevel level, BlockPos pos, RandomSource random ) {
        if( level.isRainingAt( pos ) ) return;
        level.setBlockAndUpdate( pos, type.parentTorchBlock().defaultBlockState() );
    }
}
