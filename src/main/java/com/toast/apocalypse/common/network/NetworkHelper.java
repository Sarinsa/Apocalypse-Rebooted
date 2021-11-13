package com.toast.apocalypse.common.network;

import com.toast.apocalypse.common.network.message.*;
import com.toast.apocalypse.common.util.CapabilityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;

/** Helper class for easily sending messages */
public class NetworkHelper {

    /**
     * Sends a message from the server to client
     * to inform of a change in world difficulty rate.
     *
     * @param multiplier The new difficulty multiplier.
     */
    public static void sendUpdatePlayerDifficultyMult(@Nonnull ServerPlayerEntity player, double multiplier) {
        PacketHandler.sendToClient(new S2CUpdatePlayerDifficultyRate(multiplier), player);
    }

    public static void sendUpdatePlayerDifficultyMult(@Nonnull ServerPlayerEntity player) {
        PacketHandler.sendToClient(new S2CUpdatePlayerDifficultyRate(CapabilityHelper.getPlayerDifficultyMult(player)), player);
    }

    /**
     * Sends a message from the server to client
     * to inform that the player's difficulty changed.
     *
     * @param difficulty The player's new difficulty.
     */
    public static void sendUpdatePlayerDifficulty(@Nonnull ServerPlayerEntity player, long difficulty) {
        PacketHandler.sendToClient(new S2CUpdatePlayerDifficulty(difficulty), player);
    }

    public static void sendUpdatePlayerDifficulty(@Nonnull ServerPlayerEntity player) {
        PacketHandler.sendToClient(new S2CUpdatePlayerDifficulty(CapabilityHelper.getPlayerDifficulty(player)), player);
    }

    /**
     * Sends a message from the server to client
     * to inform that the player's max difficulty changed.
     *
     * @param maxDifficulty The player's new max difficulty.
     */
    public static void sendUpdatePlayerMaxDifficulty(@Nonnull ServerPlayerEntity player, long maxDifficulty) {
        PacketHandler.sendToClient(new S2CUpdatePlayerMaxDifficulty(maxDifficulty), player);
    }

    public static void sendUpdatePlayerMaxDifficulty(@Nonnull ServerPlayerEntity player) {
        PacketHandler.sendToClient(new S2CUpdatePlayerMaxDifficulty(CapabilityHelper.getMaxPlayerDifficulty(player)), player);
    }

    /**
     * Sends a message from the server to client
     * to inform that the specified entity's
     * velocity has changed.
     *
     * @param entity The entity to update velocity for.
     * @param deltaMovement The new velocity vector.
     */
    public static void sendEntityVelocityUpdate(@Nonnull ServerPlayerEntity player, Entity entity, Vector3d deltaMovement) {
        PacketHandler.sendToClient(new S2CUpdateEntityVelocity(entity, deltaMovement), player);
    }

    /**
     * Sends a message from the server to client
     * to update the overworld moon phase
     * value in {@link com.toast.apocalypse.client.ClientUtil}
     */
    public static void sendMoonPhaseUpdate(@Nonnull ServerPlayerEntity player, ServerWorld overworld) {
        PacketHandler.sendToClient(new S2CUpdateMoonPhase(overworld.dimensionType().moonPhase(overworld.getDayTime())), player);
    }

    /**
     * Sends a message from the server to client
     * to update unlocked mob wiki indexes.
     */
    public static void sendMobWikiIndexUpdate(@Nonnull ServerPlayerEntity player, int[] unlockedIndexes) {
        PacketHandler.sendToClient(new S2CUpdateMobWikiIndexes(unlockedIndexes), player);
    }

    /**
     * Used when a player joins the world.
     */
    public static void sendMobWikiIndexUpdate(@Nonnull ServerPlayerEntity player) {
        PacketHandler.sendToClient(new S2CUpdateMobWikiIndexes(CapabilityHelper.getMobWikiIndexes(player)), player);
    }

    /**
     * Sends a message from the server to client
     * to open the mob wiki screen.
     */
    public static void openMobWikiScreen(@Nonnull ServerPlayerEntity player) {
        PacketHandler.sendToClient(new S2COpenMobWikiScreen(player.getUUID()), player);
    }
}
