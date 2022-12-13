package com.toast.apocalypse.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.toast.apocalypse.common.command.argument.DifficultyArgument;
import com.toast.apocalypse.common.command.argument.MaxDifficultyArgument;
import com.toast.apocalypse.common.core.mod_event.EventRegistry;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

public class ApocalypseBaseCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("apocalypse")
                .then(DifficultyBaseCommand.register())
                .then(ModDebugCommand.register()));
    }

    /**
     * Base command for all difficulty related subcommands.
     */
    private static class DifficultyBaseCommand {

        private static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("difficulty")
                    .requires((source) -> source.hasPermission(3))
                    .then(DifficultySetCommand.register())
                    .then(DifficultySetMaxCommand.register());
        }
    }

    /** Displays a player's Apocalypse properties (Difficulty, current event etc.) */
    private static class ModDebugCommand {

        private static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("debug")
                    .requires((source) -> source.hasPermission(3))
                    .then(Commands.argument("target", EntityArgument.player())
                            .executes((context) -> showPlayerDebugInfo(context.getSource(), EntityArgument.getPlayer(context, "target"))));
        }

        private static int showPlayerDebugInfo(CommandSource source, ServerPlayerEntity playerEntity) {
            long difficulty = CapabilityHelper.getPlayerDifficulty(playerEntity);
            int scaledDifficulty = (int) difficulty / (int) References.DAY_LENGTH;
            int partialDifficulty = difficulty <= 0 ? 0 : (int) (difficulty % References.DAY_LENGTH / (References.DAY_LENGTH / 10));
            long maxDifficulty = CapabilityHelper.getMaxPlayerDifficulty(playerEntity);
            double scaledMaxDifficulty = (double) maxDifficulty / References.DAY_LENGTH;
            int eventId = CapabilityHelper.getEventId(playerEntity);

            String eventName = EventRegistry.EVENTS.get(eventId).getName();
            source.sendSuccess(new StringTextComponent("Player difficulty: " + (difficulty < 0 ? TextFormatting.YELLOW : TextFormatting.GREEN) + scaledDifficulty + "." + partialDifficulty + TextFormatting.WHITE + " (" +  TextFormatting.GRAY + difficulty + " ticks" + TextFormatting.WHITE + ")"), true);
            source.sendSuccess(new StringTextComponent("Player max difficulty: " + TextFormatting.GREEN + scaledMaxDifficulty + TextFormatting.WHITE + " (" + TextFormatting.GRAY + maxDifficulty + " ticks" + TextFormatting.WHITE + ")"), false);
            source.sendSuccess(new StringTextComponent("Current event: " + TextFormatting.GREEN + eventId  + TextFormatting.WHITE + " (" + TextFormatting.GRAY + eventName + TextFormatting.WHITE + ")"), false);
            return 1;
        }
    }

    /**
     * Setting player difficulty.
     */
    private static class DifficultySetCommand {

        private static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("set")
                    .then(Commands.argument("targets", EntityArgument.players())
                            .then(Commands.argument("difficulty", DifficultyArgument.difficulty())
                                    .executes((context) -> setPlayerDifficulty(context.getSource(), EntityArgument.getPlayers(context, "targets"), LongArgumentType.getLong(context, "difficulty")))));
        }

        private static int setPlayerDifficulty(CommandSource source, Collection<ServerPlayerEntity> players, long difficulty) {
            for (ServerPlayerEntity player  : players) {
                long actualDifficulty = difficulty * References.DAY_LENGTH;
                long maxDifficulty = CapabilityHelper.getMaxPlayerDifficulty(player);

                if (difficulty > maxDifficulty) {
                    CapabilityHelper.setMaxPlayerDifficulty(player, difficulty);
                }
                CapabilityHelper.setPlayerDifficulty(player, actualDifficulty);
            }
            TranslationTextComponent message;

            if (players.size() == 1) {
                message = new TranslationTextComponent(References.DIFFICULTY_SET_SINGLE, difficulty, players.iterator().next().getDisplayName());
            }
            else {
                message = new TranslationTextComponent(References.DIFFICULTY_SET_MULTIPLE, difficulty, players.size());
            }
            source.sendSuccess(message, true);
            return players.size();
        }
    }

    /**
     * Setting player max difficulty.
     */
    private static class DifficultySetMaxCommand {

        private static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("max")
                    .then(Commands.argument("targets", EntityArgument.players())
                            .then(Commands.argument("difficulty", MaxDifficultyArgument.maxDifficulty())
                                    .executes((context) -> setPlayerMaxDifficulty(context.getSource(), EntityArgument.getPlayers(context, "targets"), LongArgumentType.getLong(context, "difficulty")))));
        }

        private static int setPlayerMaxDifficulty(CommandSource source, Collection<ServerPlayerEntity> players, long maxDifficulty) {
            for (ServerPlayerEntity player : players) {
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
            }
            TranslationTextComponent message;

            if (players.size() == 1) {
                message = new TranslationTextComponent(References.MAX_DIFFICULTY_SET_SINGLE, maxDifficulty, players.iterator().next().getDisplayName());
            }
            else {
                message = new TranslationTextComponent(References.MAX_DIFFICULTY_SET_MULTIPLE, maxDifficulty, players.size());
            }
            source.sendSuccess(message, true);
            return players.size();
        }
    }
}
