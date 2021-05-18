package com.toast.apocalypse.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.toast.apocalypse.common.command.argument.DifficultyArgument;
import com.toast.apocalypse.common.command.argument.MaxDifficultyArgument;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.impl.EffectCommand;
import net.minecraft.command.impl.GiveCommand;
import net.minecraft.entity.player.ServerPlayerEntity;
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
                    .then(DifficultySetCommand.register())
                    .then(DifficultySetMaxCommand.register());
        }
    }

    private static class DifficultySetCommand {

        private static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("set")
                    .then(Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("difficulty", DifficultyArgument.difficulty())
                                    .executes((context) -> setPlayerDifficulty(context.getSource(), EntityArgument.getPlayer(context, "player"), LongArgumentType.getLong(context, "difficulty")))));
        }

        private static int setPlayerDifficulty(CommandSource source, ServerPlayerEntity player, long difficulty) {
            long difficultyCalculated = difficulty * References.DAY_LENGTH;
            long maxDifficulty = CapabilityHelper.getMaxPlayerDifficulty(player);

            if (difficulty > maxDifficulty) {
                source.sendFailure(new TranslationTextComponent(References.COMMAND_INVALID_DIFFICULTY_VALUE));
                return 0;
            }
            CapabilityHelper.setPlayerDifficulty(player, difficultyCalculated);

            source.sendSuccess(new TranslationTextComponent(References.DIFFICULTY_UPDATED_MESSAGE), true);
            return 0;
        }
    }

    private static class DifficultySetMaxCommand {

        private static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("max")
                    .then(Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("difficulty", MaxDifficultyArgument.maxDifficulty())
                                    .executes((context) -> setPlayerMaxDifficulty(context.getSource(), EntityArgument.getPlayer(context, "player"), LongArgumentType.getLong(context, "difficulty")))));
        }

        private static int setPlayerMaxDifficulty(CommandSource source, ServerPlayerEntity player, long maxDifficulty) {
            TranslationTextComponent message = new TranslationTextComponent(References.MAX_DIFFICULTY_UPDATED_MESSAGE, String.format("%d", maxDifficulty));
            source.sendSuccess(message, true);

            if (maxDifficulty == -1) {
                CapabilityHelper.setMaxPlayerDifficulty(player, maxDifficulty);
            }
            else {
                long difficultyScaled = maxDifficulty * References.DAY_LENGTH;

                CapabilityHelper.setMaxPlayerDifficulty(player, difficultyScaled);

                if (CapabilityHelper.getPlayerDifficulty(player) > difficultyScaled) {
                    CapabilityHelper.setPlayerDifficulty(player, difficultyScaled);
                }
            }
            return 0;
        }
    }
}
