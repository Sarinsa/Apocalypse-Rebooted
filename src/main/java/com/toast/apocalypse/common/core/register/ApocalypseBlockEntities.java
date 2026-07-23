package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.blockentity.DynamicTrapBlockEntity;
import com.toast.apocalypse.common.blockentity.LunarPhaseSensorBlockEntity;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Objects;

public class ApocalypseBlockEntities {
    
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.BLOCK_ENTITY_TYPES, Apocalypse.MOD_ID );
    
    static {
        register( ApocalypseObjects.BlockEntities.LUNAR_PHASE_SENSOR, LunarPhaseSensorBlockEntity::new, List.of( ApocalypseObjects.Blocks.LUNAR_PHASE_SENSOR ) );
        register( ApocalypseObjects.BlockEntities.DYNAMIC_TRAP, DynamicTrapBlockEntity::new, List.of( ApocalypseObjects.Blocks.DYNAMIC_TRAP ) );
    }
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a block entity type to the deferred register. */
    @SuppressWarnings( { "SameParameterValue", "ConstantConditions" } )
    private static void register( RegistryObject<BlockEntityType<?>> regObj, BlockEntityType.BlockEntitySupplier<?> supplier, List<RegistryObject<Block>> block ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), () -> BlockEntityType.Builder.of( supplier, toBlockArray( block ) ).build( null ) );
    }
    
    /** Convenience method for returning a list of block registry objects as an array of blocks. */
    private static Block[] toBlockArray( List<RegistryObject<Block>> blocks ) {
        // Sanity checks
        Objects.requireNonNull( blocks );
        if( blocks.isEmpty() ) {
            throw new IllegalArgumentException( "Attempted to convert empty list of block registry objects into block array! Boo." );
        }
        // Collect in array and return
        Block[] blockArray = new Block[blocks.size()];
        for( int i = 0; i < blocks.size(); i++ ) {
            blockArray[i] = blocks.get( i ).get();
        }
        return blockArray;
    }
}
