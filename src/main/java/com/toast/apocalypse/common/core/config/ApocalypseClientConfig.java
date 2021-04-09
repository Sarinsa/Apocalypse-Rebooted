package com.toast.apocalypse.common.core.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Apocalypse's client side config.
 */
public class ApocalypseClientConfig {

    public enum DifficultyRenderPosWidth {
        MIDDLE,
        RIGHT,
        LEFT
    }

    public enum DifficultyRenderPosHeight {
        MIDDLE,
        TOP,
        BOTTOM
    }

    public static final Client CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        Pair<Client, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT = clientPair.getLeft();
        CLIENT_SPEC = clientPair.getRight();
    }

    public static final class Client {

        private final ForgeConfigSpec.EnumValue<DifficultyRenderPosWidth> difficultyRenderPosWidth;
        private final ForgeConfigSpec.EnumValue<DifficultyRenderPosHeight> difficultyRenderPosHeight;
        private final ForgeConfigSpec.IntValue difficultyRenderXOffset;
        private final ForgeConfigSpec.IntValue difficultyRenderYOffset;

        private Client(ForgeConfigSpec.Builder configBuilder) {
            configBuilder.push("in_game_gui");

            this.difficultyRenderPosWidth = configBuilder.comment("Determines the base X position on the screen where the world difficulty count should render")
                    .defineEnum("difficultyRenderPosWidth", DifficultyRenderPosWidth.MIDDLE, DifficultyRenderPosWidth.values());

            this.difficultyRenderPosHeight = configBuilder.comment("Determines the base Y position on the screen where the world difficulty count should render")
                    .defineEnum("difficultyRenderPosHeight", DifficultyRenderPosHeight.TOP, DifficultyRenderPosHeight.values());

            this.difficultyRenderXOffset = configBuilder.comment("Additional X offset for where to render difficulty in-game")
                    .defineInRange("difficultyRenderXOffset", 0, 0, 10000);

            this.difficultyRenderYOffset = configBuilder.comment("Additional Y offset for where to render difficulty in-game")
                    .defineInRange("difficultyRenderYOffset", 0, 0, 10000);

            configBuilder.pop();
        }

        public DifficultyRenderPosWidth getDifficultyRenderPosWidth() {
            return this.difficultyRenderPosWidth.get();
        }

        public DifficultyRenderPosHeight getDifficultyRenderPosHeight() {
            return this.difficultyRenderPosHeight.get();
        }

        public int getDifficultyRenderXOffset() {
            return this.difficultyRenderXOffset.get();
        }

        public int getDifficultyRenderYOffset() {
            return this.difficultyRenderYOffset.get();
        }
    }
}
