package com.toast.apocalypse.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class MidnightSteelBlock extends Block {

    public MidnightSteelBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GRAY)
                .strength(2.0F)
                .sound(SoundType.METAL)
                .lightLevel((state) -> 2)
                .requiresCorrectToolForDrops());
    }
}
