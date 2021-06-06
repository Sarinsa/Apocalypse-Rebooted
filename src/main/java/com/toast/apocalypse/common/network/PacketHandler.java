package com.toast.apocalypse.common.network;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.network.message.S2CUpdateEntityVelocity;
import com.toast.apocalypse.common.network.message.S2CUpdatePlayerDifficulty;
import com.toast.apocalypse.common.network.message.S2CUpdatePlayerDifficultyRate;
import com.toast.apocalypse.common.network.message.S2CUpdatePlayerMaxDifficulty;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;
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
        CHANNEL.registerMessage(messageIndex++, S2CUpdatePlayerDifficulty.class, S2CUpdatePlayerDifficulty::encode, S2CUpdatePlayerDifficulty::decode, S2CUpdatePlayerDifficulty::handle);
        CHANNEL.registerMessage(messageIndex++, S2CUpdatePlayerDifficultyRate.class, S2CUpdatePlayerDifficultyRate::encode, S2CUpdatePlayerDifficultyRate::decode, S2CUpdatePlayerDifficultyRate::handle);
        CHANNEL.registerMessage(messageIndex++, S2CUpdatePlayerMaxDifficulty.class, S2CUpdatePlayerMaxDifficulty::encode, S2CUpdatePlayerMaxDifficulty::decode, S2CUpdatePlayerMaxDifficulty::handle);
        CHANNEL.registerMessage(messageIndex++, S2CUpdateEntityVelocity.class, S2CUpdateEntityVelocity::encode, S2CUpdateEntityVelocity::decode, S2CUpdateEntityVelocity::handle);
    }

    /**
     * Sends the specified message to the client.
     *
     * @param message The message to send to the client.
     * @param player The player client that should receive this message.
     * @param <MSG> Packet type.
     */
    public static <MSG> void sendToClient(MSG message, ServerPlayerEntity player) {
        CHANNEL.sendTo(message, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }
}