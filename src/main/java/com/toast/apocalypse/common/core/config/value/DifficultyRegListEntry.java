package com.toast.apocalypse.common.core.config.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class DifficultyRegListEntry<T> implements Comparable<DifficultyRegListEntry<T>> {
    
    private final AbstractConfigField FIELD;
    
    /** The difficulty level for this entry. */
    public final int DIFFICULTY_LEVEL;
    /** The registry entry key values for this entry. */
    public final ResourceLocation[] VALUES;
    
    /**
     * The cached registry objects associated with the registry keys in this entry.
     * Not assigned until target registry is populated.
     */
    private T[] registryObjects;
    
    
    /** Creates an entry with the specified values. */
    public DifficultyRegListEntry( @Nullable AbstractConfigField field, int difficultyLevel, ResourceLocation... values ) {
        FIELD = field;
        DIFFICULTY_LEVEL = difficultyLevel;
        VALUES = values;
    }
    
    /** Creates an entry with the specified values. */
    public DifficultyRegListEntry( @Nullable AbstractConfigField field, int difficultyLevel, List<ResourceLocation> values ) {
        FIELD = field;
        DIFFICULTY_LEVEL = difficultyLevel;
        VALUES = values.toArray( new ResourceLocation[0] );
    }
    
    /** @return Loads the registry objects from registry. Returns true if successful. */
    @SuppressWarnings( "unchecked" )
    private boolean validate( IForgeRegistry<T> registry, @Nullable Predicate<T> predicate ) {
        if( registryObjects == null ) {
            registryObjects = (T[]) new Object[VALUES.length];
            
            for( int i = 0; i < VALUES.length; i++ ) {
                if( !registry.containsKey( VALUES[i] ) ) {
                    ConfigUtil.LOG.warn( "Invalid entry for {} \"{}\"! Invalid entry: {}",
                            FIELD.getClass(), FIELD.getKey(), VALUES[i].toString() );
                    return false;
                }
                else {
                    if( predicate != null ) {
                        if( !predicate.test( registry.getValue( VALUES[i] ) ) ) {
                            ConfigUtil.LOG.warn( "Invalid entry for {} \"{}\" does not pass custom predicate! Invalid entry: {}",
                                    FIELD.getClass(), FIELD.getKey(), VALUES[i].toString() );
                            return false;
                        }
                    }
                    registryObjects[i] = registry.getValue( VALUES[i] );
                }
            }
        }
        return true;
    }
    
    /**
     * @return An unmodifiable list of the registry entries in this entry.
     * Returns null if entries haven't been validated.
     */
    @Nullable
    public List<T> getRegistryEntries( IForgeRegistry<T> registry, @Nullable Predicate<T> customPredicate ) {
        if( !validate( registry, customPredicate ) ) return null;
        
        return registryObjects == null ? null : List.of( registryObjects );
    }
    
    /**
     * @return The string representation of this difficulty reg-list entry, as it would appear in a config file.
     * <p>
     * Format is "difficulty_level value0 value1 ...".
     */
    @Override
    public String toString() {
        // Start with the difficulty level
        StringBuilder str = new StringBuilder( String.valueOf( DIFFICULTY_LEVEL ) );
        
        // Append values array
        if( VALUES != null && VALUES.length > 0 ) {
            for( ResourceLocation value : VALUES ) {
                str.append( ' ' ).append( value.toString() );
            }
        }
        return str.toString();
    }
    
    @Override
    public int compareTo( @NotNull DifficultyRegListEntry entry ) {
        return Integer.compare( DIFFICULTY_LEVEL, entry.DIFFICULTY_LEVEL );
    }
}
