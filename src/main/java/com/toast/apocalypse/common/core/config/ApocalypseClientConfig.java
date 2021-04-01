package com.toast.apocalypse.common.core.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Apocalypse's client side config.
 */
public class ApocalypseClientConfig {

    public static final Client CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        Pair<Client, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT = clientPair.getLeft();
        CLIENT_SPEC = clientPair.getRight();
    }

    public static final class Client {


        private Client(ForgeConfigSpec.Builder configBuilder) {

        }
    }
}
