package com.toast.apocalypse.common.network;

import com.toast.apocalypse.common.network.message.S2CUpdateWorldDifficulty;
import com.toast.apocalypse.common.network.message.S2CUpdateWorldDifficultyRate;
import com.toast.apocalypse.common.network.message.S2CUpdateWorldMaxDifficulty;
import net.minecraftforge.fml.network.PacketDistributor;

/** Helper class for easily sending messages */
public class NetworkHelper {

    /**
     * Sends a message from the server to client
     * to inform of a change in world difficulty rate.
     *
     * @param rate The new world difficulty rate.
     */
    public static void sendUpdateWorldDifficultyRate(double rate) {
        PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new S2CUpdateWorldDifficultyRate(rate));
    }

    /**
     * Sends a message from the server to client
     * to inform of a change in world difficulty.
     *
     * @param difficulty The new world difficulty.
     */
    public static void sendUpdateWorldDifficulty(long difficulty) {
        PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new S2CUpdateWorldDifficulty(difficulty));
    }

    public static void sendUpdateWorldMaxDifficulty(long maxDifficulty) {
        PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new S2CUpdateWorldMaxDifficulty(maxDifficulty));
    }
}
