package com.toast.apocalypse.common.core.config.value;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser;

import javax.annotation.Nullable;

/**
 * A fuzzy key parser that can be used to wrap another to limit the keys allowed to a particular key usage.
 * Primarily intended to be used as a value codec, where the key parser isn't provided a key usage normally.
 *
 * @see PredicateKeyParser PredicateKeyParser - A similar parser allowing filtering by any predicate.
 */
@SuppressWarnings( "ClassCanBeRecord" )
public class UsageRestrictedKeyParser<T> implements IFuzzyKeyParser<T> {
    
    /**
     * @param parser The base parser to use.
     * @param usage  The key usage.
     * @return A new parser that will only allow fuzzy keys for a specific usage.
     */
    public static <T> UsageRestrictedKeyParser<T> of( IFuzzyKeyParser<T> parser, KeyUsage usage ) {
        return new UsageRestrictedKeyParser<>( parser, usage );
    }
    
    
    public final IFuzzyKeyParser<T> wrapped;
    public final KeyUsage keyUsage;
    
    protected UsageRestrictedKeyParser( IFuzzyKeyParser<T> parser, KeyUsage usage ) {
        wrapped = parser;
        keyUsage = usage;
    }
    
    /** @return The key parser's type name. */
    @Override
    public String getTypeName() { return wrapped.getTypeName(); }
    
    /** @return The key parser's allowed patterns (e.g., "\"pattern_1\", \"pattern_2\", \"pattern_n\""). */
    @Override
    public String getPatterns( KeyUsage usage ) { return wrapped.getPatterns( keyUsage ); }
    
    /** @return The value format (e.g., {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() { return wrapped.getFormat(); }
    
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
        return keyUsage.ifAllowed( wrapped.parseKeyString( field, line, key, blacklist ) );
    }
}