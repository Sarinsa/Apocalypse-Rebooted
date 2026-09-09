package com.toast.apocalypse.common.core.config.value;

import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser;
import fathertoast.crust.api.config.common.value.collection.key.NumberKey;
import fathertoast.crust.api.config.common.value.collection.value.DoubleValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.IntValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.MultiValueCodec;

import java.lang.reflect.Field;

import static com.toast.apocalypse.common.core.config.ApocalypseConfig.LUNAR_SIEGE;

/**
 * Holds the settings for how a full moon siege mob spawn entry scales with difficulty.
 */
public class SiegeSpawnStats extends MultiValueCodec<SiegeSpawnStats> {
    private static final IFuzzyKeyParser<Double> DIFFICULTY_CODEC = NumberKey.doubleParser( DoubleValueCodec.NON_NEGATIVE );
    
    /** The standard siege spawn stats codec that defaults to 0 spawns. */
    public static final SiegeSpawnStats CODEC = new SiegeSpawnStats();
    
    /** @return New siege spawn stats active while less than or equal to a particular difficulty (in days). */
    public static SiegeSpawnStats lessOrEqual( double maxDifficulty, int min, int max, double perDiff ) {
        return new SiegeSpawnStats( NumberKey.lessOrEqual( maxDifficulty, false ), min, max, perDiff );
    }
    
    /** @return New siege spawn stats active while greater than a particular difficulty (in days). */
    public static SiegeSpawnStats greaterThan( double minDifficulty, int min, int max, double perDiff ) {
        return new SiegeSpawnStats( NumberKey.greaterThan( minDifficulty, false ), min, max, perDiff );
    }
    
    /** @return New siege spawn stats active while between or equal to two particular difficulties (in days). */
    public static SiegeSpawnStats betweenInclusive( double minDifficulty, double maxDifficulty, int min, int max, double perDiff ) {
        return new SiegeSpawnStats( NumberKey.betweenInclusive( minDifficulty, maxDifficulty, false ), min, max, perDiff );
    }
    
    
    /** The difficulty during which these spawn stats are active. */
    public final SubValue<FuzzyKey<Double>> difficulty = subValue( DIFFICULTY_CODEC, "<Difficulty>" );
    
    /** The number of mobs to spawn at the lowest active difficulty. */
    public final SubValue<Integer> minCount = subValue( IntValueCodec.POSITIVE,
            IntValueCodec.POSITIVE.getFormat( "Min Spawns" ) );
    
    /** The maximum number of mobs to spawn. */
    public final SubValue<Integer> maxCount = subValue( IntValueCodec.POSITIVE,
            IntValueCodec.POSITIVE.getFormat( "Max Spawns" ) );
    
    /** The number of additional mobs to spawn per difficulty interval above the minimum. */
    public final SubValue<Double> countPerDiff = subValue( DoubleValueCodec.NON_NEGATIVE,
            IntValueCodec.POSITIVE.getFormat( "Spawns per Diff Interval" ) );
    
    /** The constructor used to define default values. */
    public SiegeSpawnStats( NumberKey<Double> diff, int min, int max, double per ) {
        difficulty.set( diff );
        minCount.set( min );
        maxCount.set( max );
        countPerDiff.set( per );
    }
    
    /** The no-args constructor used to create the codec "singleton" and for value loading. */
    public SiegeSpawnStats() {}
    
    /** @return The number of spawns to spawn based on difficulty (in days). */
    public int getCount( double scaledDifficulty ) {
        if( !difficulty.get().matches( scaledDifficulty ) ) return 0;
        double minDiff = Math.max( 0.0, getMin( difficulty.get() ) );
        double addedCount = countPerDiff.get() * (scaledDifficulty - minDiff) /
                LUNAR_SIEGE.SIEGE_MOB_PROPS.difficultyPerIncrease.getDouble();
        return Math.min( minCount.get() + (int) addedCount, maxCount.get() );
    }
    
    //TODO replace this scary method with getMin() once added to NumberKey
    private static double getMin( FuzzyKey<Double> numKey ) {
        if( numKey instanceof NumberKey.LessThan<Double> || numKey instanceof NumberKey.LessOrEqual<Double> ) {
            return Double.NEGATIVE_INFINITY; // No minimum
        }
        try {
            // This is the "minimum value" for all other number keys we care about
            Field value = NumberKey.class.getDeclaredField( "value" );
            value.setAccessible( true );
            return (Double) value.get( numKey );
        }
        catch( ReflectiveOperationException ex ) {
            return 0.0; // Should never happen, hopefully
        }
    }
}