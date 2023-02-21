package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.blockentity.LunarPhaseSensorTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ApocalypseBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Apocalypse.MODID);


    public static final RegistryObject<BlockEntityType<LunarPhaseSensorTileEntity>> LUNAR_PHASE_SENSOR = register("lunar_phase_sensor", () -> BlockEntityType.Builder.of(LunarPhaseSensorTileEntity::new, ApocalypseBlocks.LUNAR_PHASE_SENSOR.get()).build(null));


    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> tileEntityTypeSupplier) {
        return BLOCK_ENTITIES.register(name, tileEntityTypeSupplier);
    }
}
