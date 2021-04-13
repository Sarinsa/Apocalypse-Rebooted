package com.toast.apocalypse.common.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.WorldDifficultyManager;
import com.toast.apocalypse.common.util.References;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.server.command.ConfigCommand;

public class DifficultyArgument implements ArgumentType<Long> {

    private static final DynamicCommandExceptionType ERROR_INVALID_DIFFICULTY_VALUE = new DynamicCommandExceptionType((o) -> {
        return new TranslationTextComponent(References.COMMAND_INVALID_DIFFICULTY_VALUE, o);
    });

    public static DifficultyArgument difficulty() {
        return new DifficultyArgument();
    }

    public Long parse(StringReader stringReader) throws CommandSyntaxException {
        WorldDifficultyManager difficultyManager = Apocalypse.INSTANCE.getDifficultyManager();
        String s = stringReader.readUnquotedString();
        long difficulty = Long.parseLong(s);

        long maxDifficulty = difficultyManager.getMaxDifficulty() < 0 ? References.MAX_DIFFICULTY_HARD_LIMIT : (difficultyManager.getMaxDifficulty() / References.DAY_LENGTH);
        boolean validValue = difficulty >= 0 && difficulty <= maxDifficulty;

        if (!validValue) {
            throw ERROR_INVALID_DIFFICULTY_VALUE.create(difficulty);
        }
        else {
            return difficulty;
        }
    }
}
