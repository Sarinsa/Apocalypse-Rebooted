package com.toast.apocalypse.common.network;

import com.toast.apocalypse.common.network.message.S2CUpdateEntityVelocity;
import com.toast.apocalypse.common.network.message.S2CUpdatePlayerDifficulty;
import com.toast.apocalypse.common.network.message.S2CUpdatePlayerDifficultyRate;
import com.toast.apocalypse.common.network.message.S2CUpdatePlayerMaxDifficulty;
import com.toast.apocalypse.common.util.CapabilityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

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
}
