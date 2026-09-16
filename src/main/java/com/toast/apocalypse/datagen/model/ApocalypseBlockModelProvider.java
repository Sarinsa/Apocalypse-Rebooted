package com.toast.apocalypse.datagen.model;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.block.DynamicTrapBlock;
import com.toast.apocalypse.common.block.LunarPhaseSensorBlock;
import com.toast.apocalypse.common.block.WetTorchBlock;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public class ApocalypseBlockModelProvider extends BlockStateProvider {
    
    // Render type IDs
    private static final ResourceLocation R_TRANSLUCENT = ResourceLocation.withDefaultNamespace( "translucent" );
    private static final ResourceLocation CUTOUT = ResourceLocation.withDefaultNamespace( "cutout" );
    
    /** A string pointing to the model template folder. */
    private static final String TEMPLATE_FOLDER = BlockModelProvider.BLOCK_FOLDER + "/template";
    
    
    public ApocalypseBlockModelProvider( DataGenerator dataGen, ExistingFileHelper existingFileHelper ) {
        super( dataGen.getPackOutput(), Apocalypse.MOD_ID, existingFileHelper );
    }
    
    @Override
    protected void registerStatesAndModels() {
        // Individual blocks
        simpleBlockWithItem( ApocalypseObjects.Blocks.MIDNIGHT_STEEL_BLOCK );
        crossBlock( ApocalypseObjects.Blocks.DEAD_GRASS, CUTOUT );
        crossBlock( ApocalypseObjects.Blocks.DEAD_PLANT, CUTOUT );
        dynamicTrap();
        lunarPhaseSensor();
        
        // Auto-gen
        wetTorches();
    }
    
    /** Generates a "cube all" model for the given block and an item model with it as parent. */
    private void simpleBlockWithItem( RegistryObject<Block> regObj ) {
        cubeAll( regObj.get() );
        simpleBlockItem( regObj );
    }
    
    private void simpleBlockItem( RegistryObject<Block> regObj ) {
        final Block block = regObj.get();
        itemModels().withExistingParent( blockName( block ), blockTexture( block ) );
    }
    
    /** Generates a cross block model for the given block. */
    private void crossBlock( RegistryObject<Block> regObj, ResourceLocation renderType ) {
        final Block block = regObj.get();
        final String name = blockName( block );
        
        getVariantBuilder( block ).partialState().setModels( new ConfiguredModel(
                models().cross( name, blockTexture( block ) ).renderType( renderType ) ) );
        
        itemModels().getBuilder( name )
                .parent( new ModelFile.UncheckedModelFile( "item/generated" ) )
                .texture( "layer0", blockTexture( block ) );
    }
    
    /** Generates the block and item model for the lunar phase sensor. */
    private void lunarPhaseSensor() {
        final RegistryObject<Block> regObj = ApocalypseObjects.Blocks.LUNAR_PHASE_SENSOR;
        final Block block = regObj.get();
        
        getVariantBuilder( block ).forAllStatesExcept( ( state ) -> {
            final boolean inverted = state.getValue( LunarPhaseSensorBlock.INVERTED );
            final ResourceLocation sideTexture = extendId( regObj, (inverted ? "_inverted_top" : "_top") );
            final ResourceLocation topTexture = extendId( regObj, "_side" );
            final String modelName = inverted ? blockName( block ) + "_inverted" : blockName( block );
            
            return ConfiguredModel.builder().modelFile( models()
                    .withExistingParent( modelName, mcLoc( "block/template_daylight_detector" ) )
                    .texture( "top", sideTexture )
                    .texture( "side", topTexture ) ).build();
        }, LunarPhaseSensorBlock.POWER );
        simpleBlockItem( regObj );
    }
    
    /** Generates the block and item model for the dynamic trap. */
    private void dynamicTrap() {
        final RegistryObject<Block> regObj = ApocalypseObjects.Blocks.DYNAMIC_TRAP;
        final Block block = regObj.get();
        
        getVariantBuilder( block ).forAllStates( ( state ) -> {
            final Direction facing = state.getValue( DynamicTrapBlock.FACING );
            final DynamicTrapBlock.TrapState trapState = state.getValue( DynamicTrapBlock.TRAP_STATE );
            final ResourceLocation sideTexture = extendId( regObj, "_side_" + trapState.getSerializedName() );
            final ResourceLocation topTexture = extendId( regObj, "_top_" + trapState.getSerializedName() );
            final ResourceLocation bottomTexture = extendId( regObj, "_bottom" );
            final String modelName = blockName( block ) + "_" + trapState.getSerializedName();
            
            ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile( models().cube( modelName,
                    bottomTexture, topTexture, sideTexture, sideTexture, sideTexture, sideTexture ) );
            
            return withFacing( builder, facing ).build();
        } );
        simpleBlockItem( block, new ModelFile.UncheckedModelFile( extendId( regObj, "_idle" ) ) );
    }
    
    /** Iterates through all wet torch types and generates models for them. */
    private void wetTorches() {
        final ResourceLocation torchTemplate = mcLoc( "block/template_torch" );
        final ResourceLocation wallTorchTemplate = mcLoc( "block/template_torch_wall" );
        
        for( WetTorchBlock.Type type : WetTorchBlock.Type.values() ) {
            getVariantBuilder( type.torchBlock() ).partialState().modelForState().modelFile( models().withExistingParent(
                            type.torchId(), torchTemplate )
                    .renderType( CUTOUT )
                    .texture( "torch", blockTexture( type.torchBlock() ) ) ).addModel();
            getVariantBuilder( type.wallTorchBlock() ).partialState().modelForState().modelFile( models().withExistingParent(
                            type.wallTorchId(), wallTorchTemplate )
                    .renderType( CUTOUT )
                    .texture( "torch", blockTexture( type.torchBlock() ) ) ).addModel();
        }
    }
    
    /** @return The given model builder with rotation set based on the given facing. */
    private ConfiguredModel.Builder<?> withFacing( ConfiguredModel.Builder<?> modelBuilder, Direction facing ) {
        switch( facing ) {
            case NORTH -> modelBuilder.rotationX( 90 );
            case EAST -> {
                modelBuilder.rotationX( 90 );
                modelBuilder.rotationY( 90 );
            }
            case SOUTH -> {
                modelBuilder.rotationX( 90 );
                modelBuilder.rotationY( 180 );
            }
            case WEST -> {
                modelBuilder.rotationX( 90 );
                modelBuilder.rotationY( 270 );
            }
            case DOWN -> modelBuilder.rotationX( 180 );
            case UP -> { }
        }
        return modelBuilder;
    }
    
    /** @return The ID of the given block as a block texture/model folder file pointer with a suffix appended to it. */
    private ResourceLocation extendId( RegistryObject<Block> regObj, String suffix ) {
        ResourceLocation name = Objects.requireNonNull( regObj.getId() );
        return ResourceLocation.fromNamespaceAndPath( name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath() + suffix );
    }
    
    private String blockName( Supplier<Block> block ) {
        return Objects.requireNonNull( ForgeRegistries.BLOCKS.getKey( block.get() ) ).getPath();
    }
    
    private String blockName( Block block ) {
        return Objects.requireNonNull( ForgeRegistries.BLOCKS.getKey( block ) ).getPath();
    }
}