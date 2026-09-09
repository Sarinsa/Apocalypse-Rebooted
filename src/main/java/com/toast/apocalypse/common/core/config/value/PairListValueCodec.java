package com.toast.apocalypse.common.core.config.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.FuzzyValueList;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A pair-list value codec. Supports either lists of specified length, or lists of length >= 1.
 * Pair can consist of any two element types, each with a single-argument value codec (no whitespace allowed).
 * <p>
 * Note that the length specified is for the list value; in other words, expected arguments is two times the length.
 * <p>
 * Lists parsed from this codec are unmodifiable.
 *
 * @param <L> The left type (even-index arguments).
 * @param <R> The right type (odd-index arguments).
 */
@SuppressWarnings( "ClassCanBeRecord" )
public class PairListValueCodec<L, R> implements IValueCodec<List<PairListValueCodec.Pair<L, R>>> {
    
    /** A codec for a list value with length >= 1. */
    public static <L, R> PairListValueCodec<L, R> of( IValueCodec<L> leftCodec, IValueCodec<R> rightCodec ) {
        return of( 0, leftCodec, rightCodec );
    }
    
    /** @param length If >0, the list value will have exactly this length. Otherwise, its length will be >=1. */
    public static <L, R> PairListValueCodec<L, R> of( int length, IValueCodec<L> leftCodec, IValueCodec<R> rightCodec ) {
        return new PairListValueCodec<>( length, leftCodec, rightCodec );
    }
    
    
    // ---- Instance Methods ---- //
    
    public final int length;
    public final IValueCodec<L> leftCodec;
    public final IValueCodec<R> rightCodec;
    
    protected PairListValueCodec( int l, IValueCodec<L> codecL, IValueCodec<R> codecR ) {
        length = l;
        leftCodec = codecL;
        rightCodec = codecR;
    }
    
    /** @return True if this list accepts one or more arguments, rather than a fixed number of arguments. */
    public boolean varArgs() { return length < 1; }
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() {
        return leftCodec.getFormat() + FuzzyKey.ARG_SEPARATOR + leftCodec.getFormat() +
                " x " + (varArgs() ? "1+" : length) + " Times";
    }
    
    /** @return The value, converted to a single-line string. */
    @Override
    public String toTomlString( List<Pair<L, R>> value ) {
        // Determine argument count
        int actualPairs = value.size();
        int expectedPairs;
        if( varArgs() ) expectedPairs = Math.max( 1, actualPairs );
        else expectedPairs = length;
        
        // Build the TOML string
        final StringBuilder str = new StringBuilder();
        for( int i = 0; i < expectedPairs; i++ ) {
            Pair<L, R> pair = i < actualPairs ? value.get( i ) : null;
            str.append( FuzzyKey.ARG_SEPARATOR )
                    .append( pair == null ? leftCodec.getDefaultValue() : leftCodec.toTomlString( pair.left() ) )
                    .append( FuzzyKey.ARG_SEPARATOR )
                    .append( pair == null ? rightCodec.getDefaultValue() : rightCodec.toTomlString( pair.right() ) );
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
    public List<Pair<L, R>> parseTomlString( @Nullable IConfigField<?> field, String line, @Nullable String value ) {
        String[] args = IValueCodec.getArgs( value );
        
        // Determine argument count
        int actualArgs = args.length;
        int expectedPairs;
        if( varArgs() ) expectedPairs = Math.max( 1, -Math.floorDiv( actualArgs, -2 ) );
        else expectedPairs = length;
        
        if( field != null ) {
            int expectedArgs = expectedPairs << 1;
            if( actualArgs < expectedArgs ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Entry value has too few arguments! Expected {}, but found {}. Filling in missing args with the pair {} {}. Entry: {}",
                        varArgs() ? "at least two args" : expectedArgs + " args", actualArgs,
                        leftCodec.getDefaultValue(), rightCodec.getDefaultValue(), line );
            }
            else if( actualArgs > expectedArgs ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Entry value has too many arguments! Expected {} args, but found {}. Deleting excess args. Entry: {}",
                        expectedArgs, actualArgs, line );
            }
        }
        
        // Parse the arguments
        ArrayList<Pair<L, R>> v = new ArrayList<>( expectedPairs );
        for( int i = 0; i < expectedPairs; i++ ) {
            int l = i << 1;
            int r = l | 1;
            String leftArg = l < actualArgs ? args[l] : null;
            String rightArg = r < actualArgs ? args[r] : null;
            v.add( new Pair<>( leftCodec.parseTomlString( field, line, leftArg ),
                    rightCodec.parseTomlString( field, line, rightArg ) ) );
        }
        v.trimToSize();
        return Collections.unmodifiableList( v );
    }
    
    
    /** Just used to return value pairs. */
    public record Pair<L, R>( L left, R right ) {}
    
    
    /**
     * @return The list, converted from fuzzy entries to pairs.
     * Handy for building values from fuzzy value lists where the left pair value is a fuzzy key.
     */
    public static <L, R> List<Pair<FuzzyKey<L>, R>> fromEntries( FuzzyValueList<L, R> list ) { return fromEntries( list.getList() ); }
    
    /**
     * @return The list, converted from fuzzy entries to pairs.
     * Handy for building values from fuzzy value lists where the left pair value is a fuzzy key.
     */
    public static <L, R> List<Pair<FuzzyKey<L>, R>> fromEntries( List<FuzzyEntry<L, R>> list ) {
        ArrayList<Pair<FuzzyKey<L>, R>> v = new ArrayList<>( list.size() );
        for( FuzzyEntry<L, R> entry : list ) {
            v.add( new Pair<>( entry.wrappedKey(), entry.get() ) );
        }
        v.trimToSize();
        return Collections.unmodifiableList( v );
    }
}