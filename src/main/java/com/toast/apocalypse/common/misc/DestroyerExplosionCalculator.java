package com.toast.apocalypse.common.misc;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Used for explosions caused by the destroyer */
public class DestroyerExplosionCalculator extends ExplosionDamageCalculator {

    /** A list of blocks that the destroyer is unable to explode */
    public static List<Block> DESTROYER_PROOF_BLOCKS = new ArrayList<>();

    @Override
    public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, FluidState fluidState) {
        return (state.isAir()
                ? Optional.empty()
                : Optional.of(Math.min(0.8F, state.getExplosionResistance(level, pos, explosion))));
    }

    @Override
    public boolean shouldBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, float radius) {
        return !DESTROYER_PROOF_BLOCKS.contains(state.getBlock()) && state.getFluidState().isEmpty();
    }
}
