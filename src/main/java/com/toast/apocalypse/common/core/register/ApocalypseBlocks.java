package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.common.block.*;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ApocalypseBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Apocalypse.MODID);

    public static final RegistryObject<Block> LUNAR_PHASE_SENSOR = registerBlock("lunar_phase_sensor", LunarPhaseSensorBlock::new, CreativeModeTabs.REDSTONE_BLOCKS);
    public static final RegistryObject<Block> MIDNIGHT_STEEL_BLOCK = registerBlock("midnight_steel_block", MidnightSteelBlock::new, CreativeModeTabs.BUILDING_BLOCKS);
    public static final RegistryObject<Block> DYNAMIC_TRAP = registerBlock("dynamic_trap", DynamicTrapBlock::new, CreativeModeTabs.REDSTONE_BLOCKS, CreativeModeTabs.FUNCTIONAL_BLOCKS);
    public static final RegistryObject<Block> DEAD_GRASS = registerBlock("dead_grass", DeadPlantBlock::new, CreativeModeTabs.NATURAL_BLOCKS);
    public static final RegistryObject<Block> DEAD_PLANT = registerBlock("dead_plant", DeadPlantBlock::new, CreativeModeTabs.NATURAL_BLOCKS);
    public static final RegistryObject<Block> WET_TORCH = registerBlockNoItem("wet_torch", WetTorchBlock::new);
    public static final RegistryObject<Block> WET_WALL_TORCH = registerBlockNoItem("wet_wall_torch", WetWallTorchBlock::new);

    @SafeVarargs
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockSupplier, ResourceKey<CreativeModeTab>... creativeTabs) {
        RegistryObject<T> blockRegistryObject = BLOCKS.register(name, blockSupplier);
        ApocalypseItems.registerItem(name, () -> new BlockItem(blockRegistryObject.get(), new Item.Properties()), creativeTabs);
        return blockRegistryObject;
    }

    private static <T extends Block> RegistryObject<T> registerBlockNoItem(String name, Supplier<T> blockSupplier) {
        return BLOCKS.register(name, blockSupplier);
    }

    public static void onMissingMappings(MissingMappingsEvent event) {
    }
}
