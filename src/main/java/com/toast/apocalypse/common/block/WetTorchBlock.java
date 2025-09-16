package com.toast.apocalypse.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class WetTorchBlock extends TorchBlock {

    public WetTorchBlock() {
        super(Properties.of()
                .randomTicks()
                .noCollission()
                .instabreak()
                .lightLevel((state) -> 2)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY),
                ParticleTypes.FLAME);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(3) == 0) {
            double x = (double) pos.getX() + 0.5D;
            double y = (double) pos.getY() + 0.7D;
            double z = (double) pos.getZ() + 0.5D;

            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.isRainingAt(pos)) return;

        level.setBlockAndUpdate(pos, Blocks.TORCH.defaultBlockState());
    }
}
