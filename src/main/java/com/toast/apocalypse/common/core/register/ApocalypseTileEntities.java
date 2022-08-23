package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.tile.LunarPhaseSensorTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ApocalypseTileEntities {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Apocalypse.MODID);


    public static final RegistryObject<TileEntityType<LunarPhaseSensorTileEntity>> LUNAR_PHASE_SENSOR = register("lunar_phase_sensor", () -> TileEntityType.Builder.of(LunarPhaseSensorTileEntity::new, ApocalypseBlocks.LUNAR_PHASE_SENSOR.get()).build(null));


    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<TileEntityType<T>> tileEntityTypeSupplier) {
        return TILE_ENTITIES.register(name, tileEntityTypeSupplier);
    }
}
