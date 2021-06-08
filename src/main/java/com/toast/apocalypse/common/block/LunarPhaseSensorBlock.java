package com.toast.apocalypse.common.block;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.tile.LunarPhaseSensorTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.DaylightDetectorTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class LunarPhaseSensorBlock extends ContainerBlock {

    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;

    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);

    public LunarPhaseSensorBlock() {
        super(AbstractBlock.Properties.of(Material.METAL).strength(1.0F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0).setValue(INVERTED, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext selectionContext) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction direction) {
        return state.getValue(POWER);
    }

    public static void updateSignalStrength(BlockState state, World world, BlockPos pos) {
        if (world.dimension() == World.OVERWORLD) {
            int power = MathHelper.floor(15 * world.getMoonBrightness());
            boolean inverted = state.getValue(INVERTED);

            if (inverted) {
                power = 15 - power;
            }
            power = MathHelper.clamp(power, 0, 15);

            if (state.getValue(POWER) != power) {
                world.setBlock(pos, state.setValue(POWER, power), 3);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if (player.mayBuild()) {

            if (world.isClientSide) {
                return ActionResultType.SUCCESS;
            }
            else {
                BlockState blockstate = state.cycle(INVERTED);
                world.setBlock(pos, blockstate, 4);
                updateSignalStrength(blockstate, world, pos);
                return ActionResultType.CONSUME;
            }
        }
        else {
            return super.use(state, world, pos, player, hand, result);
        }
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader world) {
        return new LunarPhaseSensorTileEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(POWER, INVERTED);
    }
}
