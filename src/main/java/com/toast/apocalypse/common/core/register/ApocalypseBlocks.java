package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.block.*;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public class ApocalypseBlocks {
    
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create( ForgeRegistries.BLOCKS, Apocalypse.MOD_ID );
    
    static {
        register( ApocalypseObjects.Blocks.LUNAR_PHASE_SENSOR, LunarPhaseSensorBlock::new, CreativeModeTabs.REDSTONE_BLOCKS );
        register( ApocalypseObjects.Blocks.MIDNIGHT_STEEL_BLOCK, MidnightSteelBlock::new, CreativeModeTabs.BUILDING_BLOCKS );
        register( ApocalypseObjects.Blocks.DYNAMIC_TRAP, DynamicTrapBlock::new, CreativeModeTabs.REDSTONE_BLOCKS, CreativeModeTabs.FUNCTIONAL_BLOCKS );
        register( ApocalypseObjects.Blocks.DEAD_GRASS, DeadPlantBlock::new, CreativeModeTabs.NATURAL_BLOCKS );
        register( ApocalypseObjects.Blocks.DEAD_PLANT, DeadPlantBlock::new, CreativeModeTabs.NATURAL_BLOCKS );
        registerNoItem( ApocalypseObjects.Blocks.WET_TORCH, WetTorchBlock::new );
        registerNoItem( ApocalypseObjects.Blocks.WET_WALL_TORCH, WetWallTorchBlock::new );
    }
    
    
    /** Registers a block with a simple block item to the deferred register. */
    @SuppressWarnings( "SameParameterValue" )
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a block with a simple block item to the deferred register. */
    @SafeVarargs
    private static void register( RegistryObject<Block> regObj, Supplier<Block> supplier, ResourceKey<CreativeModeTab>... creativeTabs ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), supplier );
        ApocalypseItems.registerBlockItem( regObj, creativeTabs );
    }
    
    /** Registers a block with no block item to the deferred register. */
    @SuppressWarnings( { "SameParameterValue", "unused" } )
    private static void registerNoItem( RegistryObject<Block> regObj, Supplier<Block> supplier ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), supplier );
    }
    
    /** Called when missing mappings are identified and need to be handled. */
    public static void onMissingMappings( MissingMappingsEvent event ) { }
}
