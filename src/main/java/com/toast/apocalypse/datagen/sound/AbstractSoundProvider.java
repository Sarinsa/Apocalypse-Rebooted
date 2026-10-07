package com.toast.apocalypse.datagen.sound;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.datagen.DataGenUtils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/** The skeleton for a data provider that generates a sound event definitions JSON for the specified mod ID. */
public abstract class AbstractSoundProvider implements DataProvider {
    
    /** A resource type for OGG sound files. */
    private static final ExistingFileHelper.ResourceType SOUND = new ExistingFileHelper.ResourceType( PackType.CLIENT_RESOURCES, ".ogg", "sounds" );
    
    /** The list of sound entries to write to JSON. */
    private final List<Entry> entries = new ArrayList<>();
    /** The pack output this provider uses when saving. */
    private final PackOutput packOutput;
    /** The ID of the mod to run this provider for. */
    protected final String modId;
    /** An {@link ExistingFileHelper} instance to help assert the existence of files that already exist before data generation. */
    protected final ExistingFileHelper fileHelper;
    
    
    /**
     * Creates a new provider instance.
     *
     * @param packOutput The pack output to use for this provider.
     * @param modId      The ID of the mod to generate data for.
     */
    public AbstractSoundProvider( PackOutput packOutput, String modId, ExistingFileHelper fileHelper ) {
        this.packOutput = packOutput;
        this.modId = modId;
        this.fileHelper = fileHelper;
    }
    
    
    /** Called when this data provider runs. Add sound entries here. */
    protected abstract void addSoundEvents();
    
    /** Runs this data provider. */
    @Override // DataProvider
    public CompletableFuture<?> run( CachedOutput cache ) {
        addSoundEvents();
        
        if( !entries.isEmpty() )
            return save( cache, packOutput.getOutputFolder( PackOutput.Target.RESOURCE_PACK ).resolve( modId ).resolve( "sounds.json" ) );
        return CompletableFuture.allOf();
    }
    
    /** Writes the contents of {@link #entries} to JSON. */
    private CompletableFuture<?> save( CachedOutput cache, Path target ) {
        JsonObject json = new JsonObject();
        entries.forEach( entry -> json.add( entry.getName(), entry.toJson() ) );
        return DataProvider.saveStable( cache, json, target );
    }
    
    /**
     * Adds a sound event entry to the list of entries to generate.
     *
     * @param soundEventName The name of the sound event registry object to add an entry for.
     * @param subtitleKey    The translation key of the sound event's subtitle.
     * @param soundFiles     A list of resource locations pointing to the sound event's sound files.
     */
    public final void add( String soundEventName, String subtitleKey, List<ResourceLocation> soundFiles ) {
        soundFiles.forEach( ( rl ) -> DataGenUtils.assertFileExists( rl, fileHelper, SOUND ) );
        entries.add( new Entry( soundEventName, subtitleKey, soundFiles ) );
    }
    
    /**
     * Adds a sound event entry to the list of entries to generate.
     *
     * @param soundEvent  The sound event registry object to add an entry for.
     * @param subtitleKey The translation key of the sound event's subtitle.
     * @param soundFiles  A list of resource locations pointing to the sound event's sound files.
     */
    public final void add( RegistryObject<SoundEvent> soundEvent, String subtitleKey, List<ResourceLocation> soundFiles ) {
        soundFiles.forEach( ( rl ) -> DataGenUtils.assertFileExists( rl, fileHelper, SOUND ) );
        entries.add( new Entry( soundEvent, subtitleKey, soundFiles ) );
    }
    
    /** @return A list of sound file locations from the given base location, min index and max index. */
    @SuppressWarnings( "SameParameterValue" )
    protected List<ResourceLocation> soundsRanged( ResourceLocation baseLoc, int min, int max ) {
        if( min < 0 || min >= max )
            throw new IllegalArgumentException( "Range is out of bounds; min cannot be negative, and must be lower than max." );
        final List<ResourceLocation> sounds = new ArrayList<>();
        for( int i = min; i <= max; i++ ) {
            String path = baseLoc.getPath() + i;
            sounds.add( ResourceLocation.fromNamespaceAndPath( baseLoc.getNamespace(), path ) );
        }
        return sounds;
    }
    
    /** @return A resource location of the specified path, under Minecraft's namespace. */
    protected ResourceLocation mcLoc( String path ) {
        return ResourceLocation.withDefaultNamespace( path );
    }
    
    /** @return A resource location of the specified path, under Apocalypse's namespace. */
    protected ResourceLocation modLoc( String path ) {
        return Apocalypse.rl( path );
    }
    
    /** @return A string that identifies this provider. */
    @Override // DataProvider
    public String getName() {
        return "Sound events: " + modId;
    }
    
    /**
     * Holds the necessary data for a sound event.
     *
     * @param soundEventName The name of the sound event registry object represented by this entry.
     * @param subtitle       The subtitle translation key for this entry.
     * @param soundFiles     The locations of the sound files for this entry.
     */
    private record Entry(String soundEventName, String subtitle, List<ResourceLocation> soundFiles) {
        
        private Entry {
            if( soundFiles.isEmpty() )
                throw new IllegalArgumentException( "Sound event entries must have at least one sound file location specified!" );
        }
        
        Entry( RegistryObject<SoundEvent> soundEvent, String subtitle, List<ResourceLocation> soundFiles ) {
            this( Objects.requireNonNull( soundEvent.getId() ).getPath(), subtitle, soundFiles );
        }
        
        /** @return The name of this entry's sound event registry object. */
        public String getName() {
            return soundEventName;
        }
        
        /** @return This entry as a JSON object. */
        public JsonObject toJson() {
            JsonArray sounds = new JsonArray();
            for( ResourceLocation soundFile : soundFiles ) {
                sounds.add( soundFile.toString() );
            }
            JsonObject entry = new JsonObject();
            entry.addProperty( "subtitle", subtitle );
            entry.add( "sounds", sounds );
            return entry;
        }
    }
}
