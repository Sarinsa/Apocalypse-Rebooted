package com.toast.apocalypse.common.misc;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.IBlockReader;

import java.util.Optional;

public class SeekerExplosionContext extends ExplosionContext {

    // Explode blocks even if surrounded by a fluid
    @Override
    public Optional<Float> getBlockExplosionResistance(Explosion explosion, IBlockReader world, BlockPos pos, BlockState state, FluidState fluidState) {
        return state.getBlock().isAir(state, world, pos) ? Optional.empty() : Optional.of(state.getExplosionResistance(world, pos, explosion));
    }

    @Override
    public boolean shouldBlockExplode(Explosion explosion, IBlockReader world, BlockPos pos, BlockState state, float radius) {
        return state.getFluidState().isEmpty();
    }
}
