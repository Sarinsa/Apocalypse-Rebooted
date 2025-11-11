package com.toast.apocalypse.common.core.config.value;

import com.toast.apocalypse.common.util.References;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.IStringArray;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * A list of difficulty levels linked to each their set of registry entries
 * in the specified registry.
 */
public class DifficultyRegistryEntryList<T> implements IStringArray {
    
    /** The registry this list acts as a subset of. */
    private final IForgeRegistry<T> REGISTRY;
    /** The entries in this list, ordered by difficulty level. */
    protected final TreeSet<DifficultyRegListEntry<T>> UNDERLYING_SET = new TreeSet<>();
    /** The list used to write back to file. */
    protected final List<String> PRINT_LIST = new ArrayList<>();
    
    
    protected DifficultyRegistryEntryList( IForgeRegistry<T> registry ) { REGISTRY = registry; }
    
    /**
     * Create a new registry entry list from an array of entries. Used for creating default configs.
     */
    @SafeVarargs
    public DifficultyRegistryEntryList( IForgeRegistry<T> registry, DifficultyRegListEntry<T>... entries ) {
        this( registry );
        
        for( DifficultyRegListEntry<T> entry : entries ) {
            if( UNDERLYING_SET.add( entry ) )
                PRINT_LIST.add( entry.toString() );
        }
    }
    
    
    /**
     * Create a new registry entry list from a list of registry key strings.
     */
    public DifficultyRegistryEntryList( AbstractConfigField field, @Nullable Predicate<T> predicate, IForgeRegistry<T> registry, List<String> entries ) {
        this( registry );
        
        // Track difficulty levels in every entry so we can discard
        // any entries with identical difficult levels.
        List<Integer> occupiedLevels = new ArrayList<>();
        
        for( String line : entries ) {
            String[] args = line.split( " " );
            
            // Are there even enough args for a valid list?
            if( args.length < 2 ) {
                ConfigUtil.LOG.warn( "Invalid entry for {} \"{}\"! Deleting entry. Invalid entry: {}",
                        field.getClass(), field.getKey(), line );
                continue;
            }
            
            // Make sure first argument is a valid integer.
            int difficultyLevel;
            try {
                difficultyLevel = Integer.parseInt( args[0] );
            }
            catch( NumberFormatException e ) {
                ConfigUtil.LOG.warn( "{} entry in \"{}\" has invalid difficulty level! Deleting entry. Invalid entry: {}",
                        field.getClass(), field.getKey(), line );
                continue;
            }
            
            // Difficulty level can not be negative.
            if( difficultyLevel < 0 || occupiedLevels.contains( difficultyLevel ) ) {
                ConfigUtil.LOG.warn( "{} entry in \"{}\" has invalid difficulty level; either already exists in the list or is negative! Deleting entry. Invalid entry: {}",
                        field.getClass(), field.getKey(), line );
                continue;
            }
            List<ResourceLocation> registryKeys = new ArrayList<>();
            
            // Try parse the remaining arguments as registry keys/resource locations
            for( int i = 1; i < args.length; i++ ) {
                ResourceLocation regKey = ResourceLocation.tryParse( args[i] );
                if( regKey != null ) {
                    // Does the ID exist in the target registry?
                    if( registry.containsKey( regKey ) ) {
                        registryKeys.add( regKey );
                    }
                    else {
                        ConfigUtil.LOG.warn( "{} entry in \"{}\" has an invalid registry key value \"{}\", does not exist in target registry! Skipping value. Problematic entry: {}",
                                field.getClass(), field.getKey(), args[i], line );
                    }
                }
                else {
                    ConfigUtil.LOG.warn( "{} entry in \"{}\" has an invalid registry key value \"{}\", must be a resource location! Skipping value. Problematic entry: {}",
                            field.getClass(), field.getKey(), args[i], line );
                }
            }
            // Success!
            UNDERLYING_SET.add( new DifficultyRegListEntry<>( field, difficultyLevel, registryKeys ) );
            PRINT_LIST.add( line );
            occupiedLevels.add( difficultyLevel );
        }
    }
    
    
    /** @return The registry this list draws from. */
    public IForgeRegistry<T> getRegistry() { return REGISTRY; }
    
    /** @return The entries in this list. */
    public Set<DifficultyRegListEntry<T>> getEntries() { return Collections.unmodifiableSet( UNDERLYING_SET ); }
    
    /** @return A string representation of this object. */
    @Override
    public String toString() { return TomlHelper.toLiteral( PRINT_LIST.toArray() ); }
    
    /** @return Returns true if this object has the same value as another object. */
    @Override
    public boolean equals( @Nullable Object other ) {
        if( !(other instanceof DifficultyRegistryEntryList) ) return false;
        // Compare by the registries used and string list view of the object
        return getRegistry() == ((DifficultyRegistryEntryList<?>) other).getRegistry() &&
                toStringList().equals( ((DifficultyRegistryEntryList<?>) other).toStringList() );
    }
    
    /** @return A list of strings that will represent this object when written to a toml file. */
    @Override
    public List<String> toStringList() { return PRINT_LIST; }
    
    /** @return Returns true if there are no entries in this list. */
    public boolean isEmpty() { return UNDERLYING_SET.isEmpty(); }
    
    /**
     * @param registry        The registry to check against.
     * @param customPredicate Custom predicate for additional validation checks.
     * @param difficulty      The raw Apocalypse difficulty of a player (not divided by day length).
     * @return Returns an unmodifiable List of the registry entries contained in the list entry
     * with the difficulty level closest to the specified difficulty level.
     */
    @Nullable
    public List<T> getClosestValues( IForgeRegistry<T> registry, @Nullable Predicate<T> customPredicate, long difficulty ) {
        Iterator<DifficultyRegListEntry<T>> iterator = UNDERLYING_SET.descendingIterator();
        final int level = (int) (difficulty / References.DAY_LENGTH);
        
        while( iterator.hasNext() ) {
            DifficultyRegListEntry<T> entry = iterator.next();
            
            if( level >= entry.DIFFICULTY_LEVEL )
                return entry.getRegistryEntries( registry, customPredicate );
        }
        return null;
    }
    
    /**
     * @param registry        The registry to check against.
     * @param customPredicate Custom predicate for additional validation checks.
     * @param difficulty      The raw Apocalypse difficulty of a player (not divided by day length).
     * @return Returns an unmodifiable List of the registry entries contained in every entry in this list
     * with a difficulty level lower or equal to the given difficulty.
     */
    @Nullable
    public List<T> getAllUntil( IForgeRegistry<T> registry, @Nullable Predicate<T> customPredicate, long difficulty ) {
        List<T> items = new ArrayList<>();
        
        Iterator<DifficultyRegListEntry<T>> iterator = UNDERLYING_SET.iterator();
        final int level = (int) (difficulty / References.DAY_LENGTH);
        
        while( iterator.hasNext() ) {
            DifficultyRegListEntry<T> entry = iterator.next();
            
            if( entry.DIFFICULTY_LEVEL <= level ) {
                List<T> list = entry.getRegistryEntries( registry, customPredicate );
                
                if( list != null && !list.isEmpty() )
                    items.addAll( list );
            }
        }
        return items;
    }
}
