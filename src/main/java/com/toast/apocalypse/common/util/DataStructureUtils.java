package com.toast.apocalypse.common.util;

import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/** Contains helper methods for manipulating data structures and arrays. */
public class DataStructureUtils {
    
    /**
     * Functions similarly to {@link Arrays#copyOf(Object[], int)}, except the resulting
     * array is padded with the given nonnull default value instead of nulls if the array is being expanded.
     */
    public static <T> T[] copyOfWithDefault( T[] original, int newLength, T defaultValue ) {
        Objects.requireNonNull( original );
        Objects.requireNonNull( defaultValue );
        final T[] copy = Arrays.copyOf( original, newLength );
        
        for( int i = original.length; i < newLength; i++ ) {
            copy[i] = defaultValue;
        }
        return copy;
    }
    
    /** @return A random entry in the given list, or null if the list is empty. */
    @Nullable
    public static <T> T getRandomListValue( Random random, List<T> list ) {
        if( list.isEmpty() ) return null;
        return list.size() == 1 ? list.get( 0 ) : list.get( random.nextInt( list.size() ) );
    }
    
    /** @return A random entry in the given list, or null if the list is empty. */
    @Nullable
    public static <T> T getRandomListValue( RandomSource random, List<T> list ) {
        if( list.isEmpty() ) return null;
        return list.size() == 1 ? list.get( 0 ) : list.get( random.nextInt( list.size() ) );
    }
}
