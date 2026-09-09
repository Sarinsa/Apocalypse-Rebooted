package com.toast.apocalypse.common.core.config.value;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * A fuzzy key parser that can be used to wrap another to apply a validation
 * function on each parsed key, optionally overriding the format string.
 *
 * @see UsageRestrictedKeyParser UsageRestrictedKeyParser - A similar parser filtering by key usage.
 */
@SuppressWarnings( "ClassCanBeRecord" )
public class PredicateKeyParser<T> implements IFuzzyKeyParser<T> {
    
    /**
     * @param parser    The base parser to use.
     * @param validator The key validator.
     * @return A new parser that will only allow fuzzy keys that the provided validator returns true for.
     */
    public static <T> PredicateKeyParser<T> of( IFuzzyKeyParser<T> parser, Predicate<FuzzyKey<T>> validator ) {
        return new PredicateKeyParser<>( parser, validator, null );
    }
    
    /**
     * @param parser    The base parser to use.
     * @param validator The key validator.
     * @param format    The format string to print.
     * @return A new parser that will only allow fuzzy keys that the provided validator returns true for.
     */
    public static <T> PredicateKeyParser<T> of( IFuzzyKeyParser<T> parser, Predicate<FuzzyKey<T>> validator, String format ) {
        return new PredicateKeyParser<>( parser, validator, format );
    }
    
    
    public final IFuzzyKeyParser<T> wrapped;
    public final Predicate<FuzzyKey<T>> validator;
    
    @Nullable
    private final String formatOverride;
    
    protected PredicateKeyParser( IFuzzyKeyParser<T> parser, Predicate<FuzzyKey<T>> predicate, @Nullable String format ) {
        wrapped = parser;
        validator = predicate;
        formatOverride = format;
    }
    
    /** @return The key parser's type name. */
    @Override
    public String getTypeName() { return wrapped.getTypeName(); }
    
    /** @return The key parser's allowed patterns (e.g., "\"pattern_1\", \"pattern_2\", \"pattern_n\""). */
    @Override
    public String getPatterns( KeyUsage usage ) { return wrapped.getPatterns( usage ); }
    
    /** @return The value format (e.g., {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() { return formatOverride == null ? wrapped.getFormat() : formatOverride; }
    
    /**
     * Loads a key from the provided TOML string. If anything goes wrong, correct it at the lowest level possible,
     * and if the config field is not null, provide useful feedback and identify the field.
     *
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param key   The key string to parse from.
     * @return A new fuzzy key based on the key string, or null if parsing fails.
     */
    @Override
    @Nullable
    public FuzzyKey<T> parseKeyString( @Nullable IConfigField<?> field, String line, String key, boolean blacklist ) {
        return ifAllowed( wrapped.parseKeyString( field, line, key, blacklist ) );
    }
    
    /** @return The key if it is allowed by the validator; null otherwise. */
    @Nullable
    public FuzzyKey<T> ifAllowed( @Nullable FuzzyKey<T> key ) { return key != null && validator.test( key ) ? key : null; }
}