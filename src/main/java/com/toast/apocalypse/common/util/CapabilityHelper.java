package com.toast.apocalypse.common.util;

import com.toast.apocalypse.common.capability.ApocalypseCapabilities;
import com.toast.apocalypse.common.capability.mobwiki.IMobWikiCapability;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.network.NetworkHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.lang.reflect.Array;

/**
 * Helper class for easily manipulating capability
 * data without writing a long line every time.
 */
@SuppressWarnings("all")
public class CapabilityHelper {

    //
    // RAIN DAMAGE
    //
    public static void setRainTick(@Nonnull PlayerEntity playerEntity, int ticks) {
        playerEntity.getCapability(ApocalypseCapabilities.RAIN_TICK_CAPABILITY).orElse(ApocalypseCapabilities.RAIN_TICK_CAPABILITY.getDefaultInstance()).setTicks(ticks);
    }

    public static void addRainTick(@Nonnull PlayerEntity playerEntity) {
        playerEntity.getCapability(ApocalypseCapabilities.RAIN_TICK_CAPABILITY).orElse(ApocalypseCapabilities.RAIN_TICK_CAPABILITY.getDefaultInstance()).addTick();
    }

    public static void clearRainTicks(@Nonnull PlayerEntity playerEntity) {
        playerEntity.getCapability(ApocalypseCapabilities.RAIN_TICK_CAPABILITY).orElse(ApocalypseCapabilities.RAIN_TICK_CAPABILITY.getDefaultInstance()).clearTicks();
    }

    public static int getRainTicks(@Nonnull PlayerEntity playerEntity) {
        return playerEntity.getCapability(ApocalypseCapabilities.RAIN_TICK_CAPABILITY).orElse(ApocalypseCapabilities.RAIN_TICK_CAPABILITY.getDefaultInstance()).getRainTicks();
    }

    //
    // DIFFICULTY
    //
    public static void setPlayerDifficulty(@Nonnull ServerPlayerEntity player, long difficulty) {
        player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).orElse(ApocalypseCapabilities.DIFFICULTY_CAPABILITY.getDefaultInstance()).setDifficulty(difficulty);
        NetworkHelper.sendUpdatePlayerDifficulty(player, difficulty);
    }

    public static long getPlayerDifficulty(@Nonnull PlayerEntity player) {
        return player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).orElse(ApocalypseCapabilities.DIFFICULTY_CAPABILITY.getDefaultInstance()).getDifficulty();
    }

    public static void setMaxPlayerDifficulty(@Nonnull ServerPlayerEntity player, long maxDifficulty) {
        player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).orElse(ApocalypseCapabilities.DIFFICULTY_CAPABILITY.getDefaultInstance()).setMaxDifficulty(maxDifficulty);
        NetworkHelper.sendUpdatePlayerMaxDifficulty(player, maxDifficulty);
    }

    public static long getMaxPlayerDifficulty(@Nonnull PlayerEntity player) {
        return player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).orElse(ApocalypseCapabilities.DIFFICULTY_CAPABILITY.getDefaultInstance()).getMaxDifficulty();
    }

    public static void setPlayerDifficultyMult(@Nonnull ServerPlayerEntity player, double multiplier) {
        player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).orElse(ApocalypseCapabilities.DIFFICULTY_CAPABILITY.getDefaultInstance()).setDifficultyMult(multiplier);
        NetworkHelper.sendUpdatePlayerDifficultyMult(player, multiplier);
    }

    public static double getPlayerDifficultyMult(@Nonnull PlayerEntity player) {
        return player.getCapability(ApocalypseCapabilities.DIFFICULTY_CAPABILITY).orElse(ApocalypseCapabilities.DIFFICULTY_CAPABILITY.getDefaultInstance()).getDifficultyMult();
    }

    //
    // EVENT DATA
    //
    public static void setEventData(@Nonnull ServerPlayerEntity player, CompoundNBT data) {
        player.getCapability(ApocalypseCapabilities.EVENT_DATA_CAPABILITY).orElse(ApocalypseCapabilities.EVENT_DATA_CAPABILITY.getDefaultInstance()).setEventData(data);
    }

    public static CompoundNBT getEventData(@Nonnull ServerPlayerEntity player) {
        return player.getCapability(ApocalypseCapabilities.EVENT_DATA_CAPABILITY).orElse(ApocalypseCapabilities.EVENT_DATA_CAPABILITY.getDefaultInstance()).getEventData();
    }

    public static int getEventId(@Nonnull ServerPlayerEntity player) {
        return Apocalypse.INSTANCE.getDifficultyManager().getEventId(player);
    }

    //
    // ENTITY INIT MARK
    //
    public static void markEntity(@Nonnull LivingEntity livingEntity) {
        livingEntity.getCapability(ApocalypseCapabilities.ENTITY_MARKER_CAPABILITY).orElse(ApocalypseCapabilities.ENTITY_MARKER_CAPABILITY.getDefaultInstance()).setMarked(true);
    }

    public static boolean isEntityMarked(@Nonnull LivingEntity livingEntity) {
        return livingEntity.getCapability(ApocalypseCapabilities.ENTITY_MARKER_CAPABILITY).orElse(ApocalypseCapabilities.ENTITY_MARKER_CAPABILITY.getDefaultInstance()).getMarked();
    }

    //
    // MOB WIKI
    //
    public static void addMobWikiIndex(@Nonnull ServerPlayerEntity player, int mobIndex) {
        IMobWikiCapability mobWikiCapability = player.getCapability(ApocalypseCapabilities.MOB_WIKI_CAPABILITY).orElse(ApocalypseCapabilities.MOB_WIKI_CAPABILITY.getDefaultInstance());
        mobWikiCapability.addEntry(mobIndex);
        NetworkHelper.sendMobWikiIndexUpdate(player, mobWikiCapability.getEntries());
    }

    public static void setMobWikiIndexes(@Nonnull ServerPlayerEntity player, int[] entries) {
        player.getCapability(ApocalypseCapabilities.MOB_WIKI_CAPABILITY).orElse(ApocalypseCapabilities.MOB_WIKI_CAPABILITY.getDefaultInstance()).setEntries(entries);
        NetworkHelper.sendMobWikiIndexUpdate(player);
    }

    public static int[] getMobWikiIndexes(@Nonnull ServerPlayerEntity player) {
        return player.getCapability(ApocalypseCapabilities.MOB_WIKI_CAPABILITY).orElse(ApocalypseCapabilities.MOB_WIKI_CAPABILITY.getDefaultInstance()).getEntries();
    }
}
