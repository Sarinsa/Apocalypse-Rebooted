package com.toast.apocalypse.common.core.config.value;

import fathertoast.crust.api.config.common.value.collection.FuzzyMap;
import fathertoast.crust.api.config.common.value.collection.ItemStackList;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser;
import fathertoast.crust.api.config.common.value.collection.key.ItemStackKey;
import fathertoast.crust.api.config.common.value.collection.key.NumberKey;
import fathertoast.crust.api.config.common.value.collection.value.DoubleValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;

/**
 * A fuzzy map used to associate lists of item stacks with difficulty levels.
 *
 * @see NumberKey
 * @see ItemStackKey
 * @see com.toast.apocalypse.common.core.config.field.ItemsByDifficultyMapField
 */
@SuppressWarnings( "unused" )
public class ItemsByDifficultyMap extends FuzzyMap<Double, List<FuzzyKey<ItemStack>>> {
    
    public static final IFuzzyKeyParser<Double> PARSER = NumberKey.doubleParser( DoubleValueCodec.NON_NEGATIVE );
    public static final ListValueCodec<FuzzyKey<ItemStack>> CODEC = ListValueCodec.of( ItemStackKey.PARSER );
    
    /** Constructs an empty map. Use this if you want to {@link #load} a map from file/NBT. */
    public ItemsByDifficultyMap() { super( PARSER, CODEC ); }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link ItemsByDifficultyMap.Builder} is much easier.
     */
    @SafeVarargs
    public ItemsByDifficultyMap( FuzzyEntry<Double, List<FuzzyKey<ItemStack>>>... keys ) {
        super( PARSER, CODEC, keys );
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link ItemsByDifficultyMap.Builder} is much easier.
     */
    public ItemsByDifficultyMap( Collection<FuzzyEntry<Double, List<FuzzyKey<ItemStack>>>> keys ) {
        super( PARSER, CODEC, keys );
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public ItemsByDifficultyMap makeNew() { return new ItemsByDifficultyMap(); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing item stack maps smoother. */
    public static class Builder<B extends Builder<B>> extends AbstractBuilder<Double, List<FuzzyKey<ItemStack>>, ItemsByDifficultyMap, B> {
        
        public Builder() { super( CODEC ); }
        
        /** @return A new fuzzy map reflecting the current state of this builder. */
        @Override
        public ItemsByDifficultyMap build() { return new ItemsByDifficultyMap( list ); }
        
        
        // ---- "Less Than" Keys ---- //
        
        /** Adds a key-value pair based on the value. Matches all values that are lower than the given value. */
        public SubBuilder lessThan( double max ) { return new SubBuilder( NumberKey.lessThan( max, false ) ); }
        
        /** Adds a blacklist key based on the value. Matches all values that are lower than the given value. */
        public B lessThanBlacklist( double max ) { return putBlacklist( NumberKey.lessThan( max, true ) ); }
        
        
        // ---- "Greater Than" Keys ---- //
        
        /** Adds a key-value pair based on the value. Matches all values greater than the given value. */
        public SubBuilder greaterThan( double min ) { return new SubBuilder( NumberKey.greaterThan( min, false ) ); }
        
        /** Adds a blacklist key based on the value. Matches all values greater than the given value. */
        public B greaterThanBlacklist( double min ) { return putBlacklist( NumberKey.greaterThan( min, true ) ); }
        
        
        // ---- "Less or Equal" Keys ---- //
        
        /** Adds a key-value pair based on the value. Matches all values that are lower or equal to the given value. */
        public SubBuilder lessOrEq( double max ) { return new SubBuilder( NumberKey.lessOrEqual( max, false ) ); }
        
        /** Adds a blacklist key based on the value. Matches all values that are lower or equal to the given value. */
        public B lessOrEqBlacklist( double max ) { return putBlacklist( NumberKey.lessOrEqual( max, true ) ); }
        
        
        // ---- "Greater or Equal" Keys ---- //
        
        /** Adds a key-value pair based on the value. Matches all values that are greater than or equal to the given value. */
        public SubBuilder greaterOrEq( double min ) { return new SubBuilder( NumberKey.greaterOrEqual( min, false ) ); }
        
        /** Adds a blacklist key based on the value. Matches all values that are greater than or equal to the given value. */
        public B greaterOrEqBlacklist( double min ) { return putBlacklist( NumberKey.greaterOrEqual( min, true ) ); }
        
        
        // ---- "Between Inclusive" Keys ---- //
        
        /** Adds a key-value pair based on the value. Matches all values between the specified minimum and maximum (inclusive) values. */
        public SubBuilder betweenInclusive( double min, double max ) {
            if( !NumberKey.isValidRange( min, max ) )
                throw new IllegalArgumentException( "Min value must be less than max value!" );
            return new SubBuilder( NumberKey.betweenInclusive( min, max, false ) );
        }
        
        /** Adds a blacklist key based on the value. Matches all values between the specified minimum and maximum (inclusive) values. */
        public B betweenInclusiveBlacklist( double min, double max ) {
            if( !NumberKey.isValidRange( min, max ) )
                throw new IllegalArgumentException( "Min value must be less than max value!" );
            return putBlacklist( NumberKey.betweenInclusive( min, max, true ) );
        }
        
        
        public class SubBuilder extends ItemStackList.Builder<SubBuilder> {
            private final NumberKey<Double> root;
            
            SubBuilder( NumberKey<Double> key ) { root = key; }
            
            /** Finishes out the sub-builder entry. */
            public B buildSub() {
                return Builder.this.put( root, super.build().getList() );
            }
            
            /** @return A new fuzzy list reflecting the current state of this builder. */
            @Override
            public ItemStackList build() { throw new UnsupportedOperationException( "Use #buildSub() to build!" ); }
        }
    }
}