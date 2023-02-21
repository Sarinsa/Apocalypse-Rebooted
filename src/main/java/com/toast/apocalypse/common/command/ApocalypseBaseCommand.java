package com.toast.apocalypse.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.toast.apocalypse.common.command.argument.DifficultyArgument;
import com.toast.apocalypse.common.command.argument.MaxDifficultyArgument;
import com.toast.apocalypse.common.core.mod_event.EventRegistry;
import com.toast.apocalypse.common.util.CapabilityHelper;
import com.toast.apocalypse.common.util.References;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class ApocalypseBaseCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("apocalypse")
                .then(DifficultyBaseCommand.register())
                .then(ModDebugCommand.register()));
    }

    /**
     * Base command for all difficulty related subcommands.
     */
    private static class DifficultyBaseCommand {

        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("difficulty")
                    .requires((source) -> source.hasPermission(3))
                    .then(DifficultySetCommand.register())
                    .then(DifficultySetMaxCommand.register());
        }
    }

    /** Displays a player's Apocalypse properties (Difficulty, current event etc.) */
    private static class ModDebugCommand {

        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("debug")
                    .requires((source) -> source.hasPermission(3))
                    .then(Commands.argument("target", EntityArgument.player())
                            .executes((context) -> showPlayerDebugInfo(context.getSource().source, EntityArgument.getPlayer(context, "target"))));
        }

        private static int showPlayerDebugInfo(CommandSource source, ServerPlayer playerEntity) {
            long difficulty = CapabilityHelper.getPlayerDifficulty(playerEntity);
            int scaledDifficulty = (int) difficulty / (int) References.DAY_LENGTH;
            int partialDifficulty = difficulty <= 0 ? 0 : (int) (difficulty % References.DAY_LENGTH / (References.DAY_LENGTH / 10));
            long maxDifficulty = CapabilityHelper.getMaxPlayerDifficulty(playerEntity);
            double scaledMaxDifficulty = (double) maxDifficulty / References.DAY_LENGTH;
            int eventId = CapabilityHelper.getEventId(playerEntity);

            String eventName = EventRegistry.EVENTS.get(eventId).getName();
            source.sendSystemMessage(Component.literal("Player difficulty: " + (difficulty < 0 ? ChatFormatting.YELLOW : ChatFormatting.GREEN) + scaledDifficulty + "." + partialDifficulty + ChatFormatting.WHITE + " (" +  ChatFormatting.GRAY + difficulty + " ticks" + ChatFormatting.WHITE + ")"));
            source.sendSystemMessage(Component.literal("Player max difficulty: " + ChatFormatting.GREEN + scaledMaxDifficulty + ChatFormatting.WHITE + " (" + ChatFormatting.GRAY + maxDifficulty + " ticks" + ChatFormatting.WHITE + ")"));
            source.sendSystemMessage(Component.literal("Current event: " + ChatFormatting.GREEN + eventId  + ChatFormatting.WHITE + " (" + ChatFormatting.GRAY + eventName + ChatFormatting.WHITE + ")"));
            return 1;
        }
    }

    /**
     * Setting player difficulty.
     */
    private static class DifficultySetCommand {

        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("set")
                    .then(Commands.argument("targets", EntityArgument.players())
                            .then(Commands.argument("difficulty", DifficultyArgument.difficulty())
                                    .executes((context) -> setPlayerDifficulty(context.getSource(), EntityArgument.getPlayers(context, "targets"), LongArgumentType.getLong(context, "difficulty")))));
        }

        private static int setPlayerDifficulty(CommandSourceStack source, Collection<ServerPlayer> players, long difficulty) {
            for (ServerPlayer player  : players) {
                long actualDifficulty = difficulty * References.DAY_LENGTH;
                long maxDifficulty = CapabilityHelper.getMaxPlayerDifficulty(player);

                if (difficulty > maxDifficulty) {
                    CapabilityHelper.setMaxPlayerDifficulty(player, difficulty);
                }
                CapabilityHelper.setPlayerDifficulty(player, actualDifficulty);
            }
            Component message;

            if (players.size() == 1) {
                message = Component.translatable(References.DIFFICULTY_SET_SINGLE, difficulty, players.iterator().next().getDisplayName());
            }
            else {
                message = Component.translatable(References.DIFFICULTY_SET_MULTIPLE, difficulty, players.size());
            }
            source.sendSystemMessage(message);
            return players.size();
        }
    }

    /**
     * Setting player max difficulty.
     */
    private static class DifficultySetMaxCommand {

        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("max")
                    .then(Commands.argument("targets", EntityArgument.players())
                            .then(Commands.argument("difficulty", MaxDifficultyArgument.maxDifficulty())
                                    .executes((context) -> setPlayerMaxDifficulty(context.getSource(), EntityArgument.getPlayers(context, "targets"), LongArgumentType.getLong(context, "difficulty")))));
        }

        private static int setPlayerMaxDifficulty(CommandSourceStack source, Collection<ServerPlayer> players, long maxDifficulty) {
            for (ServerPlayer player : players) {
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
            Component message;

            if (players.size() == 1) {
                message = Component.translatable(References.MAX_DIFFICULTY_SET_SINGLE, maxDifficulty, players.iterator().next().getDisplayName());
            }
            else {
                message = Component.translatable(References.MAX_DIFFICULTY_SET_MULTIPLE, maxDifficulty, players.size());
            }
            source.sendSystemMessage(message);
            return players.size();
        }
    }
}
