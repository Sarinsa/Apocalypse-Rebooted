package com.toast.apocalypse.common.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.toast.apocalypse.common.capability.CapabilityHelper;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.util.References;
import com.toast.apocalypse.datagen.lang.TranslationKey;
import fathertoast.crust.api.lib.CrustCmdHelper;
import net.minecraft.commands.CommandSourceStack;

public class MaxDifficultyArgument implements ArgumentType<Long> {
    
    public static final TranslationKey COMMAND_INVALID_MAX_DIFFICULTY_VALUE = Apocalypse.tk( "apocalypse.command.argument.max_difficulty.invalid_value",
            "Value can not be less than -1 or exceed 100,000. -1 equals no limit" );
    
    private static final DynamicCommandExceptionType ERROR_INVALID_DIFFICULTY_VALUE = new DynamicCommandExceptionType( COMMAND_INVALID_MAX_DIFFICULTY_VALUE::withArgs );
    
    
    public static MaxDifficultyArgument maxDifficulty() {
        return new MaxDifficultyArgument();
    }
    
    /** A command 'argument' that accepts a long maximum difficulty value. */
    public static RequiredArgumentBuilder<CommandSourceStack, Long> of() { return of( "max difficulty" ); }
    
    /** A command 'argument' that accepts a long maximum difficulty value. */
    public static RequiredArgumentBuilder<CommandSourceStack, Long> of( String arg ) {
        return CrustCmdHelper.argument( arg, maxDifficulty() );
    }
    
    /** @return The long maximum difficulty argument value. */
    public static long get( CommandContext<?> context ) { return get( context, "max difficulty" ); }
    
    /** @return The long maximum difficulty argument value. */
    public static long get( CommandContext<?> context, String arg ) {
        return LongArgumentType.getLong( context, arg );
    }
    
    
    public Long parse( StringReader stringReader ) throws CommandSyntaxException {
        final String s = stringReader.readUnquotedString();
        final long maxDifficulty = Long.parseLong( s );
        
        boolean validValue = maxDifficulty >= -1L && maxDifficulty <= CapabilityHelper.divByDayLength( References.MAX_DIFFICULTY_HARD_LIMIT );
        
        if( !validValue ) {
            throw ERROR_INVALID_DIFFICULTY_VALUE.create( maxDifficulty );
        }
        else {
            return maxDifficulty;
        }
    }
}