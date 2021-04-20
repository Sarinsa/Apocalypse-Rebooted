package com.toast.apocalypse.common.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.IBlockReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Used for explosions caused by the destroyer */
public class DestroyerExplosionContext extends ExplosionContext {

    /** A list of blocks that the destroyer is unable to explode */
    public static List<Block> DESTROYER_PROOF_BLOCKS = new ArrayList<>();

    @Override
    public Optional<Float> getBlockExplosionResistance(Explosion explosion, IBlockReader world, BlockPos pos, BlockState state, FluidState fluidState) {
        return (state.getBlock().isAir(state, world, pos)
                ? Optional.empty()
                : Optional.of(Math.min(0.8F, state.getExplosionResistance(world, pos, explosion))));
    }

    @Override
    public boolean shouldBlockExplode(Explosion explosion, IBlockReader world, BlockPos pos, BlockState state, float radius) {
        return !DESTROYER_PROOF_BLOCKS.contains(state.getBlock()) && state.getFluidState().isEmpty();
    }
}
