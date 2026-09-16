package com.toast.apocalypse.datagen.model;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.client.ItemModelProps;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class ApocalypseItemModelProvider extends ItemModelProvider {
    
    /** A set of items to keep track of what items have been generated models for. */
    private final Set<Item> processed = new HashSet<>();
    
    public ApocalypseItemModelProvider( DataGenerator dataGen, ExistingFileHelper existingFileHelper ) {
        super( dataGen.getPackOutput(), Apocalypse.MOD_ID, existingFileHelper );
    }
    
    @Override
    protected void registerModels() {
        // Special cases
        lunarClock( ApocalypseObjects.Items.LUNAR_CLOCK );
        
        // Simple items and spawn eggs
        autoGenModels();
    }
    
    /** Automatically generates models for simple items. */
    private void autoGenModels() {
        final ResourceLocation spawnEggParent = mcLoc( ITEM_FOLDER + "/template_spawn_egg" );
        
        for( RegistryObject<Item> ro : ApocalypseItems.REGISTRY.getEntries() ) {
            // Skip items we have already generated for
            if( processed.contains( ro.get() ) ) continue;
            final Item item = ro.get();
            // Assume all models for block items have been handled in the block model provider
            if( item instanceof BlockItem ) continue;
            final ResourceLocation id = ro.getId();
            
            // Spawn eggs
            if( item instanceof SpawnEggItem ) {
                withExistingParent( Objects.requireNonNull( id ).getPath(), spawnEggParent );
            }
            // Assume the rest should be treated as simple items
            else {
                final String name = Objects.requireNonNull( id ).getPath();
                withExistingParent( name, mcLoc( ITEM_FOLDER + "/generated" ) )
                        .texture( "layer0", modItemTexture( name ) );
            }
        }
    }
    
    /** Generates a basic 'item/generated' model for the given item. */
    @Override
    public ItemModelBuilder basicItem( Item item ) {
        processed.add( item );
        return super.basicItem( item );
    }
    
    /** Generates a basic 'item/generated' model for the given item ID. */
    @Override
    public ItemModelBuilder basicItem( ResourceLocation itemId ) {
        processed.add( Objects.requireNonNull( ForgeRegistries.ITEMS.getValue( itemId ) ) );
        return super.basicItem( itemId );
    }
    
    /** Generates the model files for the lunar clock item. */
    @SuppressWarnings( "SameParameterValue" )
    private void lunarClock( RegistryObject<Item> item ) {
        processed.add( item.get() );
        final ResourceLocation id = Objects.requireNonNull( item.getId() );
        final ItemModelBuilder builder = getBuilder( id.toString() )
                .parent( new ModelFile.UncheckedModelFile( "item/generated" ) )
                .texture( "layer0", modItemTexture( id.getPath() + "_0" ) );
        
        for( int i = 0; i < 8; i++ ) {
            ItemModelBuilder subModelBuilder = getBuilder( id + "_" + i )
                    .parent( new ModelFile.UncheckedModelFile( "item/generated" ) )
                    .texture( "layer0", modItemTexture( id.getPath() + "_" + i ) );
            
            builder.override().predicate( ItemModelProps.MOON_PHASE_PROPERTY, i )
                    .model( new ModelFile.UncheckedModelFile( subModelBuilder.getUncheckedLocation() ) );
        }
    }
    
    private String modItemTexture( String path ) {
        return ResourceLocation.fromNamespaceAndPath( Apocalypse.MOD_ID, ModelProvider.ITEM_FOLDER + "/" + path ).toString();
    }
    
    private String mcItemTexture( String path ) {
        return ResourceLocation.withDefaultNamespace( ModelProvider.ITEM_FOLDER + "/" + path ).toString();
    }
    
    private String itemName( Supplier<Item> item ) {
        return Objects.requireNonNull( ForgeRegistries.ITEMS.getKey( item.get() ) ).getPath();
    }
    
    private String itemName( Item item ) {
        return Objects.requireNonNull( ForgeRegistries.ITEMS.getKey( item ) ).getPath();
    }
}