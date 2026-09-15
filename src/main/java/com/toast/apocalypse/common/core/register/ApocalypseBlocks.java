package com.toast.apocalypse.common.core.register;

import com.mojang.datafixers.util.Pair;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class ApocalypseBlocks {
    
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create( ForgeRegistries.BLOCKS, Apocalypse.MOD_ID );
    
    public static final List<Pair<RegistryObject<Block>, RegistryObject<Block>>> WET_TORCHES;
    
    static {
        register( ApocalypseObjects.Blocks.LUNAR_PHASE_SENSOR, LunarPhaseSensorBlock::new, CreativeModeTabs.REDSTONE_BLOCKS );
        register( ApocalypseObjects.Blocks.MIDNIGHT_STEEL_BLOCK, MidnightSteelBlock::new, CreativeModeTabs.BUILDING_BLOCKS );
        register( ApocalypseObjects.Blocks.DYNAMIC_TRAP, DynamicTrapBlock::new, CreativeModeTabs.REDSTONE_BLOCKS, CreativeModeTabs.FUNCTIONAL_BLOCKS );
        register( ApocalypseObjects.Blocks.DEAD_GRASS, DeadPlantBlock::new, CreativeModeTabs.NATURAL_BLOCKS );
        register( ApocalypseObjects.Blocks.DEAD_PLANT, DeadPlantBlock::new, CreativeModeTabs.NATURAL_BLOCKS );
        
        
        final ArrayList<Pair<RegistryObject<Block>, RegistryObject<Block>>> wetTorches = new ArrayList<>();
        for( WetTorchBlock.Type type : WetTorchBlock.Type.values() ) {
            RegistryObject<Block> torch = register( type.torchId(), type::torchSupplier, CreativeModeTabs.BUILDING_BLOCKS );
            RegistryObject<Block> wallTorch = register( type.wallTorchId(), type::wallTorchSupplier, CreativeModeTabs.BUILDING_BLOCKS );
            wetTorches.add( Pair.of( torch, wallTorch ) );
        }
        wetTorches.trimToSize();
        WET_TORCHES = Collections.unmodifiableList( wetTorches );
    }
    
    
    /** Registers a block with a simple block item to the deferred register. */
    @SuppressWarnings( "SameParameterValue" )
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a block with a simple block item to the deferred register. */
    @SafeVarargs
    private static <T extends Block> RegistryObject<T> register( String name, Supplier<T> supplier, ResourceKey<CreativeModeTab>... creativeTabs ) {
        RegistryObject<T> regObj = REGISTRY.register( name, supplier );
        ApocalypseItems.registerBlockItem( regObj, creativeTabs );
        return regObj;
    }
    
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
    
    
    private ApocalypseBlocks() { }
}
