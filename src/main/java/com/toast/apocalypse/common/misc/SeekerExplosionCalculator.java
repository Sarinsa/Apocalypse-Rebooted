package com.toast.apocalypse.common.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.Optional;

public class SeekerExplosionCalculator extends ExplosionDamageCalculator {

    // Explode blocks even if surrounded by a fluid
    @Override
    public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, FluidState fluidState) {
        return state.isAir() ? Optional.empty() : Optional.of(state.getExplosionResistance(level, pos, explosion));
    }

    @Override
    public boolean shouldBlockExplode(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, float radius) {
        return state.getFluidState().isEmpty();
    }
}
