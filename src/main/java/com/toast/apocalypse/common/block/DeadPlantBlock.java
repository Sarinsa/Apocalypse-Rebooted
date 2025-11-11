package com.toast.apocalypse.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DeadBushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class DeadPlantBlock extends DeadBushBlock {
    
    public DeadPlantBlock() {
        super( Properties.of()
                .mapColor( MapColor.WOOD )
                .replaceable()
                .noCollission()
                .instabreak()
                .sound( SoundType.MOSS )
                .ignitedByLava()
                .offsetType( BlockBehaviour.OffsetType.XYZ )
                .pushReaction( PushReaction.DESTROY )
        );
    }
    
    @Override
    protected boolean mayPlaceOn( BlockState state, BlockGetter level, BlockPos pos ) {
        return state.isSolidRender( level, pos );
    }
    
    @Override
    public int getFlammability( BlockState state, BlockGetter level, BlockPos pos, Direction direction ) {
        return 100;
    }
    
    @Override
    public int getFireSpreadSpeed( BlockState state, BlockGetter level, BlockPos pos, Direction direction ) {
        return 60;
    }
}
