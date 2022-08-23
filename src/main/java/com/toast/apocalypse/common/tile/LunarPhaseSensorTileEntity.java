package com.toast.apocalypse.common.tile;

import com.toast.apocalypse.common.block.LunarPhaseSensorBlock;
import com.toast.apocalypse.common.core.register.ApocalypseTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class LunarPhaseSensorTileEntity extends TileEntity implements ITickableTileEntity {

    public LunarPhaseSensorTileEntity() {
        super(ApocalypseTileEntities.LUNAR_PHASE_SENSOR.get());
    }

    @Override
    public void tick() {
        if (this.level != null && !this.level.isClientSide && this.level.getGameTime() % 20L == 0L) {
            BlockState state = this.getBlockState();
            Block block = state.getBlock();

            if (block instanceof LunarPhaseSensorBlock) {
                LunarPhaseSensorBlock.updateSignalStrength(state, this.level, this.worldPosition);
            }
        }
    }
}
