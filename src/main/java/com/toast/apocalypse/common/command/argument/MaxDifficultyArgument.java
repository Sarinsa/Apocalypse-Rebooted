package com.toast.apocalypse.common.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.toast.apocalypse.common.capability.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.network.chat.Component;

public class MaxDifficultyArgument implements ArgumentType<Long> {
    
    private static final DynamicCommandExceptionType ERROR_INVALID_DIFFICULTY_VALUE = new DynamicCommandExceptionType( ( o )
            -> Component.translatable( References.COMMAND_INVALID_MAX_DIFFICULTY_VALUE, o ) );
    
    public static MaxDifficultyArgument maxDifficulty() {
        return new MaxDifficultyArgument();
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