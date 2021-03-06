package com.toast.apocalypse.common.core.config;

import com.toast.apocalypse.common.util.References;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/** Apocalypse's server/per world config */
public class ApocalypseServerConfig {

    public static final Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        Pair<Server, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER = serverPair.getLeft();
        SERVER_SPEC = serverPair.getRight();
    }

    public static final class Server {

        private final ForgeConfigSpec.DoubleValue defaultPlayerGracePeriod;
        private final ForgeConfigSpec.DoubleValue defaultPlayerMaxDifficulty;

        private Server(ForgeConfigSpec.Builder configBuilder) {
            configBuilder.push("difficulty");

            this.defaultPlayerGracePeriod = configBuilder.comment("This is the amount of time that must pass before a player's difficulty starts increasing. Only relevant for players that join a world or server for the first time. A value of 1 is equal to a whole Minecraft day.")
                    .defineInRange("defaultPlayerGracePeriod", 1.0D, 0.0D, 1000.0D);

            this.defaultPlayerMaxDifficulty = configBuilder.comment("The default max difficulty for players. Only relevant for players that join a world or server for the first time.")
                    .defineInRange("defaultPlayerMaxDifficulty", 150.0D, 0.0D, (double) References.MAX_DIFFICULTY_HARD_LIMIT / References.DAY_LENGTH);

            configBuilder.pop();
        }

        public double getPlayerGracePeriod() {
            return this.defaultPlayerGracePeriod.get();
        }

        public double getDefaultPlayerMaxDifficulty() {
            return this.defaultPlayerMaxDifficulty.get();
        }
    }
}
