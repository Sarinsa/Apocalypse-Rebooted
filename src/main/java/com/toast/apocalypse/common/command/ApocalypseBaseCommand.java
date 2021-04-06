package com.toast.apocalypse.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.toast.apocalypse.common.command.argument.DifficultyArgument;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.network.NetworkHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

public class ApocalypseBaseCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("apocalypse")
                .then(DifficultyBaseCommand.register()));
    }

    private static class DifficultyBaseCommand {

        private static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("difficulty")
                    .requires((source) -> source.hasPermission(3))
                    .then(DifficultySetCommand.register());
        }
    }

    private static class DifficultySetCommand {

        private static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("set")
                    .requires((source) -> source.hasPermission(3))
                    .then(Commands.argument("difficulty", DifficultyArgument.difficulty()))
                    .executes((context) -> setWorldDifficulty(context.getSource(), LongArgumentType.getLong(context, "difficulty")));
        }

        private static int setWorldDifficulty(CommandSource source, long difficulty) {
            long difficultyCalculated = difficulty * References.DAY_LENGTH;
            Apocalypse.INSTANCE.getDifficultyManager().setWorldDifficulty(difficultyCalculated);
            NetworkHelper.sendUpdateWorldDifficulty(difficultyCalculated);

            source.getServer().getPlayerList().getPlayers().forEach((playerEntity) -> {
                playerEntity.sendMessage(new TranslationTextComponent(References.DIFFICULTY_UPDATED_MESSAGE), Util.NIL_UUID);
            });
            return 0;
        }
    }
}
