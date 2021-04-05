package com.toast.apocalypse.common.network;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.network.message.S2CUpdateWorldDifficulty;
import com.toast.apocalypse.common.network.message.S2CUpdateWorldDifficultyRate;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_NAME = "APOCALYPSE";
    /** The network channel our mod will be
     *  using when sending messages. */
    public static final SimpleChannel CHANNEL = createChannel();

    private int messageIndex;

    private static SimpleChannel createChannel() {
        return NetworkRegistry.ChannelBuilder
                .named(Apocalypse.resourceLoc("channel"))
                .serverAcceptedVersions(PROTOCOL_NAME::equals)
                .clientAcceptedVersions(PROTOCOL_NAME::equals)
                .networkProtocolVersion(() -> PROTOCOL_NAME)
                .simpleChannel();
    }

    public void registerMessages() {
        CHANNEL.registerMessage(messageIndex++, S2CUpdateWorldDifficulty.class, S2CUpdateWorldDifficulty::encode, S2CUpdateWorldDifficulty::decode, S2CUpdateWorldDifficulty::handle);
        CHANNEL.registerMessage(messageIndex++, S2CUpdateWorldDifficultyRate.class, S2CUpdateWorldDifficultyRate::encode, S2CUpdateWorldDifficultyRate::decode, S2CUpdateWorldDifficultyRate::handle);
    }
}