package com.toast.apocalypse.common.misc;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.IBlockReader;

import java.util.Optional;

/** Used for explosions caused by the destroyer */
public class DestroyerExplosionContext extends ExplosionContext {

    @Override
    public Optional<Float> getBlockExplosionResistance(Explosion explosion, IBlockReader world, BlockPos pos, BlockState state, FluidState fluidState) {
        return (state.isAir(world, pos) 
                ? Optional.empty()
                : Optional.of(Math.min(0.8F, state.getExplosionResistance(world, pos, explosion))));
    }

    @Override
    public boolean shouldBlockExplode(Explosion explosion, IBlockReader world, BlockPos pos, BlockState state, float p_230311_5_) {
        // Let's not blow up bedrock and fluids I think?
        return state.getBlock() != Blocks.BEDROCK && state.getFluidState().isEmpty();
    }
}
