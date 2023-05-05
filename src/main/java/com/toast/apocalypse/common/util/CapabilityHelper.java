package com.toast.apocalypse.common.util;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import com.toast.apocalypse.common.capability.difficulty.DifficultyProvider;
import com.toast.apocalypse.common.capability.event_data.EventDataProvider;
import com.toast.apocalypse.common.capability.mobwiki.IMobWikiCapability;
import com.toast.apocalypse.common.capability.mobwiki.MobWikiProvider;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.network.NetworkHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;

/**
 * Helper class for easily manipulating capability
 * data without writing a long line every time.
 */
@SuppressWarnings("all")
public class CapabilityHelper {

    //
    // DIFFICULTY
    //
    public static void setPlayerDifficulty(@Nonnull ServerPlayer player, long difficulty) {
        player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).ifPresent((capability) ->
        {
            capability.setDifficulty(difficulty);
            NetworkHelper.sendUpdatePlayerDifficulty(player, difficulty);
        });
    }

    public static long getPlayerDifficulty(@Nonnull Player player) {
        return player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).orElse(DifficultyProvider.SUPPLIER.get()).getDifficulty();
    }

    public static void setMaxPlayerDifficulty(@Nonnull ServerPlayer player, long maxDifficulty) {
        player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).ifPresent((capability) ->
        {
            capability.setMaxDifficulty(maxDifficulty);
            NetworkHelper.sendUpdatePlayerMaxDifficulty(player, maxDifficulty);
        });
    }

    public static long getMaxPlayerDifficulty(@Nonnull Player player) {
        return player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).orElse(DifficultyProvider.SUPPLIER.get()).getMaxDifficulty();
    }

    public static void setPlayerDifficultyMult(@Nonnull ServerPlayer player, double multiplier) {
        player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).ifPresent((capability) ->
        {
            capability.setDifficultyMult(multiplier);
            NetworkHelper.sendUpdatePlayerDifficultyMult(player, multiplier);
        });
    }

    public static double getPlayerDifficultyMult(@Nonnull Player player) {
        return player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).orElse(DifficultyProvider.SUPPLIER.get()).getDifficultyMult();
    }

    //
    // EVENT DATA
    //
    public static void setEventData(@Nonnull ServerPlayer player, CompoundTag data) {
        player.getCapability(ApocalypseCapabilities.EVENT_DATA_CAPABILITY).ifPresent((capability) ->
        {
            capability.setEventData(data);
        });
    }

    public static CompoundTag getEventData(@Nonnull ServerPlayer player) {
        return player.getCapability(ApocalypseCapabilities.EVENT_DATA_CAPABILITY).orElse(EventDataProvider.SUPPLIER.get()).getEventData();
    }

    public static int getEventId(@Nonnull ServerPlayer player) {
        return Apocalypse.INSTANCE.getDifficultyManager().getEventId(player);
    }

    //
    // MOB WIKI
    //
    public static void addMobWikiIndex(@Nonnull ServerPlayer player, int mobIndex) {
        player.getCapability(ApocalypseCapabilities.MOB_WIKI_CAPABILITY).ifPresent((capability) ->
        {
            capability.addEntry(mobIndex);
            NetworkHelper.sendMobWikiIndexUpdate(player, capability.getEntries());
        });
    }

    public static void setMobWikiIndexes(@Nonnull ServerPlayer player, int[] entries) {
        player.getCapability(ApocalypseCapabilities.MOB_WIKI_CAPABILITY).ifPresent((capability) -> {
            capability.setEntries(entries);
            NetworkHelper.sendMobWikiIndexUpdate(player);
        });
    }

    public static int[] getMobWikiIndexes(@Nonnull ServerPlayer player) {
        return player.getCapability(ApocalypseCapabilities.MOB_WIKI_CAPABILITY).orElse(MobWikiProvider.SUPPLIER.get()).getEntries();
    }
}
