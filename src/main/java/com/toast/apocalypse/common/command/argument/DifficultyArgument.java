package com.toast.apocalypse.common.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.toast.apocalypse.common.capability.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import fathertoast.crust.api.lib.CrustCmdHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class DifficultyArgument implements ArgumentType<Long> {
    
    private static final DynamicCommandExceptionType ERROR_INVALID_DIFFICULTY_VALUE = new DynamicCommandExceptionType( ( o )
            -> Component.translatable( References.COMMAND_INVALID_DIFFICULTY_VALUE, o ) );
    
    public static DifficultyArgument difficulty() {
        return new DifficultyArgument();
    }
    
    /** A command 'argument' that accepts a long difficulty value. */
    public static RequiredArgumentBuilder<CommandSourceStack, Long> of() { return of( "difficulty" ); }
    
    /** A command 'argument' that accepts a long difficulty value. */
    public static RequiredArgumentBuilder<CommandSourceStack, Long> of( String arg ) {
        return CrustCmdHelper.argument( arg, difficulty() );
    }
    
    /** @return The long difficulty argument value. */
    public static long get( CommandContext<?> context ) { return get( context, "difficulty" ); }
    
    /** @return The long difficulty argument value. */
    public static long get( CommandContext<?> context, String arg ) {
        return LongArgumentType.getLong( context, arg );
    }
    
    
    public Long parse( StringReader stringReader ) throws CommandSyntaxException {
        final String s = stringReader.readUnquotedString();
        final long difficulty = Long.parseLong( s );
        
        boolean validValue = difficulty >= 0 && difficulty <= CapabilityHelper.divByDayLength( References.MAX_DIFFICULTY_HARD_LIMIT );
        
        if( !validValue ) {
            throw ERROR_INVALID_DIFFICULTY_VALUE.create( difficulty );
        }
        else {
            return difficulty;
        }
    }
}