package com.toast.apocalypse.datagen.particle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.datagen.DataGenUtils;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/** The skeleton for a data provider that generates particle type data. */
public abstract class AbstractParticleProvider implements DataProvider {
    
    /** A resource type for particle textures. */
    private static final ExistingFileHelper.ResourceType TEXTURE = new ExistingFileHelper.ResourceType( PackType.CLIENT_RESOURCES, ".png", "textures/particle" );
    
    /** The list of particle entries to write to JSON. */
    private final Map<String, List<String>> entries = new HashMap<>();
    /** The pack output this provider uses when saving. */
    private final PackOutput packOutput;
    /** The ID of the mod to generate particle data for. */
    protected final String modId;
    /** An {@link ExistingFileHelper} instance to help assert the existence of files that already exist before data generation. */
    private final ExistingFileHelper fileHelper;
    
    
    public AbstractParticleProvider( PackOutput packOutput, String modId, ExistingFileHelper fileHelper ) {
        this.packOutput = packOutput;
        this.modId = modId;
        this.fileHelper = fileHelper;
    }
    
    
    /** Runs this data provider. */
    @Override // DataProvider
    public CompletableFuture<?> run( CachedOutput cache ) {
        return CompletableFuture.supplyAsync( () -> {
            addParticles();
            
            if( entries.isEmpty() ) return CompletableFuture.allOf();
            
            List<CompletableFuture<?>> futures = new ArrayList<>();
            // Write each entry to their own file
            for( Map.Entry<String, List<String>> entry : entries.entrySet() ) {
                String fileName = entry.getKey().split( ":", 2 )[1] + ".json";
                Path filePath = packOutput.getOutputFolder( PackOutput.Target.RESOURCE_PACK ).resolve( modId ).resolve( "particles" ).resolve( fileName );
                futures.add( DataProvider.saveStable( cache, entryToJson( entry ), filePath ) );
            }
            return CompletableFuture.allOf( futures.toArray( CompletableFuture[]::new ) );
        } );
    }
    
    /** @return A string that identifies this provider. */
    @Override // DataProvider
    public String getName() {
        return "Particles: " + modId;
    }
    
    
    /** Called when this data provider runs. Add particle entries here. */
    protected abstract void addParticles();
    
    /**
     * Adds a particle entry to the list of entries to generate.
     *
     * @param particleId The registry ID of the particle type to add an entry for.
     * @param textures   A list of texture locations pointing to the textures the particle can pick from.
     */
    public final void add( ResourceLocation particleId, List<ResourceLocation> textures ) {
        textures.forEach( ( rl ) -> DataGenUtils.assertFileExists( rl, fileHelper, TEXTURE ) );
        List<String> strings = textures.stream().map( ResourceLocation::toString ).toList();
        entries.put( particleId.toString(), strings );
    }
    
    /**
     * Adds a particle entry to the list of entries to generate.
     *
     * @param particleType The particle type registry object to add an entry for.
     * @param textures     A list of texture locations pointing to the textures the particle can pick from.
     */
    public final void add( RegistryObject<? extends ParticleType<?>> particleType, List<ResourceLocation> textures ) {
        add( Objects.requireNonNull( particleType.getId() ), textures );
    }
    
    /** @return A list of texture locations from the given base location, min index and max index. */
    @SuppressWarnings( "SameParameterValue" )
    protected List<ResourceLocation> spritesRanged( ResourceLocation baseLoc, int min, int max ) {
        if( min < 0 || min >= max )
            throw new IllegalArgumentException( "Range is out of bounds; min cannot be negative, and must be lower than max." );
        final List<ResourceLocation> sprites = new ArrayList<>();
        for( int i = min; i <= max; i++ ) {
            String path = baseLoc.getPath() + i;
            sprites.add( ResourceLocation.fromNamespaceAndPath( baseLoc.getNamespace(), path ) );
        }
        return sprites;
    }
    
    /** @return A resource location of the specified path, under Minecraft's namespace. */
    @SuppressWarnings( "SameParameterValue" )
    protected ResourceLocation mcLoc( String path ) {
        return ResourceLocation.withDefaultNamespace( path );
    }
    
    /** @return A resource location of the specified path, under Apocalypse's namespace. */
    protected ResourceLocation modLoc( String path ) {
        return Apocalypse.rl( path );
    }
    
    /** @return The given map entry as a JSON object. */
    private JsonObject entryToJson( Map.Entry<String, List<String>> entry ) {
        JsonArray textures = new JsonArray();
        for( String textureLoc : entry.getValue() ) {
            textures.add( textureLoc );
        }
        JsonObject particleDef = new JsonObject();
        particleDef.add( "textures", textures );
        return particleDef;
    }
}
