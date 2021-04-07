package com.toast.apocalypse.common.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.toast.apocalypse.common.core.WorldDifficultyManager;
import com.toast.apocalypse.common.util.References;
import net.minecraft.util.text.TranslationTextComponent;

public class DifficultyArgument implements ArgumentType<Long> {

    private static final DynamicCommandExceptionType ERROR_INVALID_DIFFICULTY_VALUE = new DynamicCommandExceptionType((o) -> {
        return new TranslationTextComponent(References.COMMAND_INVALID_DIFFICULTY_VALUE, o);
    });

    public static DifficultyArgument difficulty() {
        return new DifficultyArgument();
    }

    public Long parse(StringReader stringReader) throws CommandSyntaxException {
        String s = stringReader.readUnquotedString();
        long difficulty = Long.parseLong(s);

        boolean validValue = difficulty > 0L && difficulty <= WorldDifficultyManager.MAX_DIFFICULTY / References.DAY_LENGTH;

        if (!validValue) {
            throw ERROR_INVALID_DIFFICULTY_VALUE.create(difficulty);
        }
        else {
            return difficulty;
        }
    }
}
