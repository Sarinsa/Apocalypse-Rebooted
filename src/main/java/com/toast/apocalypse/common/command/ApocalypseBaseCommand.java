package com.toast.apocalypse.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.toast.apocalypse.common.capability.CapabilityHelper;
import com.toast.apocalypse.common.command.argument.DifficultyArgument;
import com.toast.apocalypse.common.command.argument.MaxDifficultyArgument;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.mod_event.EventType;
import com.toast.apocalypse.common.util.References;
import fathertoast.crust.api.lib.CrustCmdHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Set;

public class ApocalypseBaseCommand {
    
    public static void register( CommandDispatcher<CommandSourceStack> dispatcher ) {
        // apocalypse (difficulty|nextFullMoon|debug) ...
        dispatcher.register( CrustCmdHelper.literal( "apocalypse" )
                .then( DifficultyBaseCommand.register() )
                .then( NextFullMoonCommand.register() )
                .then( ModDebugCommand.register() )
        );
    }
    
    /**
     * Base command for all difficulty related subcommands.
     */
    private static class DifficultyBaseCommand {
        
        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            return CrustCmdHelper.literal( "difficulty" )
                    .requires( CrustCmdHelper::isModerator )
                    // apocalypse difficulty (set|max) [<players>] <difficulty>
                    .then( DifficultySetCommand.register() )
                    .then( DifficultySetMaxCommand.register() );
        }
    }
    
    /**
     * Setting player difficulty.
     */
    private static class DifficultySetCommand {
        
        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            // apocalypse difficulty set [<players>] <difficulty>
            return CrustCmdHelper.literal( "set" )
                    .then( DifficultyArgument.of()
                            .executes( context -> setPlayerDifficulty( context.getSource(),
                                    CrustCmdHelper.players( context ), DifficultyArgument.get( context ) ) ) )
                    .then( CrustCmdHelper.argumentPlayers( "targets" )
                            .then( DifficultyArgument.of()
                                    .executes( context -> setPlayerDifficulty( context.getSource(),
                                            CrustCmdHelper.players( context, "targets" ), DifficultyArgument.get( context ) ) ) ) );
        }
        
        private static int setPlayerDifficulty( CommandSourceStack source, Collection<ServerPlayer> players, long difficulty ) {
            for( ServerPlayer player : players ) {
                final long actualDifficulty = CapabilityHelper.mulByDayLength( difficulty );
                final long maxDifficulty = CapabilityHelper.getMaxDifficulty( player );
                
                if( difficulty > maxDifficulty ) {
                    CapabilityHelper.setMaxDifficulty( player, difficulty );
                }
                CapabilityHelper.setDifficulty( player, actualDifficulty );
            }
            
            final Component message;
            if( players.size() == 1 ) {
                message = Component.translatable( References.DIFFICULTY_SET_SINGLE, difficulty, players.iterator().next().getDisplayName() );
            }
            else {
                message = Component.translatable( References.DIFFICULTY_SET_MULTIPLE, difficulty, players.size() );
            }
            source.sendSystemMessage( message );
            return players.size();
        }
    }
    
    /**
     * Setting player max difficulty.
     */
    private static class DifficultySetMaxCommand {
        
        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            // apocalypse difficulty max [<players>] <difficulty>
            return CrustCmdHelper.literal( "max" )
                    .then( MaxDifficultyArgument.of()
                            .executes( context -> setPlayerMaxDifficulty( context.getSource(),
                                    CrustCmdHelper.players( context ), MaxDifficultyArgument.get( context ) ) ) )
                    .then( CrustCmdHelper.argumentPlayers( "targets" )
                            .then( MaxDifficultyArgument.of()
                                    .executes( context -> setPlayerMaxDifficulty( context.getSource(),
                                            CrustCmdHelper.players( context, "targets" ), MaxDifficultyArgument.get( context ) ) ) ) );
        }
        
        private static int setPlayerMaxDifficulty( CommandSourceStack source, Collection<ServerPlayer> players, long maxDifficulty ) {
            for( ServerPlayer player : players ) {
                if( maxDifficulty == -1 ) {
                    CapabilityHelper.setMaxDifficulty( player, maxDifficulty );
                }
                else {
                    final long scaledMaxDiff = CapabilityHelper.mulByDayLength( maxDifficulty );
                    CapabilityHelper.setMaxDifficulty( player, scaledMaxDiff );
                    
                    if( CapabilityHelper.getDifficulty( player ) > scaledMaxDiff ) {
                        CapabilityHelper.setDifficulty( player, scaledMaxDiff );
                    }
                }
            }
            
            final Component message;
            if( players.size() == 1 ) {
                message = Component.translatable( References.MAX_DIFFICULTY_SET_SINGLE, maxDifficulty, players.iterator().next().getDisplayName() );
            }
            else {
                message = Component.translatable( References.MAX_DIFFICULTY_SET_MULTIPLE, maxDifficulty, players.size() );
            }
            source.sendSystemMessage( message );
            return players.size();
        }
    }
    
    /**
     * Advances the world time to right before the next full moon night.
     */
    private static class NextFullMoonCommand {
        
        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            // apocalypse nextFullMoon
            return CrustCmdHelper.literal( "nextFullMoon" )
                    .requires( CrustCmdHelper::isModerator )
                    .executes( context ->
                            advanceToNextFullMoon( context.getSource() ) );
        }
        
        private static int advanceToNextFullMoon( CommandSourceStack source ) {
            ServerLevel level = source.getLevel();
            long timeToSet;
            long currentTime = level.getDayTime();
            long currentDay = CapabilityHelper.divByDayLength( currentTime );
            if( Apocalypse.INSTANCE.getDifficultyManager().isFullMoon() && currentTime % References.DAY_LENGTH < 12_980L ) {
                timeToSet = currentDay * References.DAY_LENGTH + 12_980L;
            }
            else {
                long monthToSet = currentDay / References.LUNAR_CYCLE + 1L;
                timeToSet = monthToSet * References.LUNAR_CYCLE * References.DAY_LENGTH + 12_980L;
            }
            level.setDayTime( timeToSet );
            source.sendSystemMessage( Component.translatable(
                    "apocalypse.command.next_full_moon.message", timeToSet ) );
            return 1;
        }
    }
    
    /**
     * Displays a player's Apocalypse properties (Difficulty, current events, etc.).
     */
    private static class ModDebugCommand {
        
        private static ArgumentBuilder<CommandSourceStack, ?> register() {
            // apocalypse debug [<player>]
            return CrustCmdHelper.literal( "debug" )
                    .requires( CrustCmdHelper::canCheat )
                    .executes( context ->
                            sendPlayerDebugInfo( context.getSource(), CrustCmdHelper.player( context ) ) )
                    .then( CrustCmdHelper.argumentPlayer( "target" )
                            .executes( ( context ) -> sendPlayerDebugInfo(
                                    context.getSource(), CrustCmdHelper.player( context, "target" ) ) ) );
        }
        
        private static int sendPlayerDebugInfo( CommandSourceStack source, ServerPlayer playerEntity ) {
            final long difficulty = CapabilityHelper.getDifficulty( playerEntity );
            final double scaledDifficulty = CapabilityHelper.fractalDivByDayLength( difficulty );
            final long maxDifficulty = CapabilityHelper.getMaxDifficulty( playerEntity );
            final double scaledMaxDifficulty = CapabilityHelper.fractalDivByDayLength( maxDifficulty );
            final Set<EventType<?>> eventTypes = Apocalypse.INSTANCE.getDifficultyManager().getEventTypes( playerEntity );
            
            source.sendSystemMessage( Component.literal(
                    "Player difficulty: " + (difficulty < 0 ? ChatFormatting.YELLOW : ChatFormatting.GREEN) +
                            scaledDifficulty + ChatFormatting.WHITE +
                            " (" + ChatFormatting.GRAY + difficulty + " ticks" + ChatFormatting.WHITE + ")" ) );
            source.sendSystemMessage( Component.literal(
                    "Player max difficulty: " + ChatFormatting.GREEN +
                            scaledMaxDifficulty + ChatFormatting.WHITE +
                            " (" + ChatFormatting.GRAY + maxDifficulty + " ticks" + ChatFormatting.WHITE + ")" ) );
            
            if( eventTypes == null || eventTypes.isEmpty() ) {
                source.sendSystemMessage( Component.literal( "Current events: " + ChatFormatting.GRAY + "none" ) );
            }
            else {
                source.sendSystemMessage( Component.literal( "Current events: " ) );
                for( EventType<?> eventType : eventTypes ) {
                    final String s = String.valueOf( ChatFormatting.GREEN ) +
                            eventType.getId() + ChatFormatting.WHITE +
                            "(" + ChatFormatting.GRAY + eventType.getName() + ChatFormatting.WHITE + ")";
                    source.sendSystemMessage( Component.literal( s ) );
                }
            }
            return 1;
        }
    }
}