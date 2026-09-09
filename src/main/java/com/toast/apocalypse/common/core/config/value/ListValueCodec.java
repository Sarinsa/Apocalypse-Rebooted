package com.toast.apocalypse.common.core.config.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A list value codec. Supports either lists of specified length, or lists of length >= 1.
 * Element type can be any element with a single-argument value codec (no whitespace allowed).
 * <p>
 * Lists parsed from this codec are unmodifiable.
 *
 * @param <V> The list type parameter.
 */
@SuppressWarnings( "ClassCanBeRecord" )
public class ListValueCodec<V> implements IValueCodec<List<V>> {
    
    /** A codec for a list value with length >= 1. */
    public static <T> ListValueCodec<T> of( IValueCodec<T> codec ) { return of( 0, codec ); }
    
    /** @param length If >0, the list value will have exactly this length. Otherwise, its length will be >=1. */
    public static <T> ListValueCodec<T> of( int length, IValueCodec<T> codec ) { return new ListValueCodec<>( length, codec ); }
    
    
    // ---- Instance Methods ---- //
    
    public final int length;
    public final IValueCodec<V> elementCodec;
    
    protected ListValueCodec( int l, IValueCodec<V> codec ) {
        length = l;
        elementCodec = codec;
    }
    
    /** @return True if this list accepts one or more arguments, rather than a fixed number of arguments. */
    public boolean varArgs() { return length < 1; }
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() { return elementCodec.getFormat() + " x " + (varArgs() ? "1+" : length) + " Times"; }
    
    /** @return The value, converted to a single-line string. */
    @Override
    public String toTomlString( List<V> value ) {
        // Determine argument count
        int actualArgs = value.size();
        int expectedArgs;
        if( varArgs() ) { expectedArgs = Math.max( 1, actualArgs ); }
        else expectedArgs = length;
        
        // Build the TOML string
        final StringBuilder str = new StringBuilder();
        for( int i = 0; i < expectedArgs; i++ ) {
            V arg = i < actualArgs ? value.get( i ) : null;
            str.append( FuzzyKey.ARG_SEPARATOR ).append( arg == null ? elementCodec.getDefaultValue() :
                    elementCodec.toTomlString( arg ) );
        }
        return str.substring( FuzzyKey.ARG_SEPARATOR.length() );
    }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value string to parse from.
     * @return A new value based on the value string. If the parse fails, returns a non-null default value.
     */
    @Override
    public List<V> parseTomlString( @Nullable IConfigField<?> field, String line, @Nullable String value ) {
        String[] args = IValueCodec.getArgs( value );
        
        // Determine argument count
        int actualArgs = args.length;
        int expectedArgs;
        if( varArgs() ) { expectedArgs = Math.max( 1, actualArgs ); }
        else expectedArgs = length;
        
        if( field != null ) {
            if( actualArgs < expectedArgs ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Entry value has too few arguments! Expected {}, but found {}. Replacing missing args with {}. Entry: {}",
                        varArgs() ? "at least one arg" : expectedArgs + " args", actualArgs,
                        elementCodec.getDefaultValue(), line );
            }
            else if( actualArgs > expectedArgs ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Entry value has too many arguments! Expected {} args, but found {}. Deleting excess args. Entry: {}",
                        expectedArgs, actualArgs, line );
            }
        }
        
        // Parse the arguments
        ArrayList<V> v = new ArrayList<>( expectedArgs );
        for( int i = 0; i < expectedArgs; i++ ) {
            String arg = i < actualArgs ? args[i] : null;
            v.add( elementCodec.parseTomlString( field, line, arg ) );
        }
        v.trimToSize();
        return Collections.unmodifiableList( v );
    }
}