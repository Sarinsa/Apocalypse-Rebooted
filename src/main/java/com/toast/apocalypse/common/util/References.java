package com.toast.apocalypse.common.util;

public class References {
    
    //
    // NUMERIC CONSTANTS
    //
    public static final int LUNAR_CYCLE = 8;
    public static final int DAY_LENGTH = 24_000;
    public static final long MAX_DIFFICULTY_HARD_LIMIT = 100_000L * DAY_LENGTH;
    public static final long DEFAULT_COLOR_CHANGE = 240L * DAY_LENGTH;
    
    /** @return The number of months converted to days. */
    public static int toDays( int months ) { return months * References.LUNAR_CYCLE; }
    
    /** @return The number of months converted to days. */
    public static double toDays( double months ) { return months * References.LUNAR_CYCLE; }
}