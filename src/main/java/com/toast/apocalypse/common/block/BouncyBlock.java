package com.toast.apocalypse.common.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BouncyBlock extends Block {

    public BouncyBlock() {
        super(AbstractBlock.Properties.of(Material.STONE).instabreak());
    }

    @Override
    public void stepOn(World world, BlockPos pos, Entity entity) {
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.2, 1.1D, 1.2));
        super.stepOn(world, pos, entity);
    }
}
