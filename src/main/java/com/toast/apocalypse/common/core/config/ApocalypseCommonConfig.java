package com.toast.apocalypse.common.core.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Apocalypse's common config, synced between client and server.
 */
public class ApocalypseCommonConfig {

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        Pair<Common, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonPair.getLeft();
        COMMON_SPEC = commonPair.getRight();
    }

    public static final class Common {

        // Rain
        private final ForgeConfigSpec.IntValue rainTickRate;
        private final ForgeConfigSpec.IntValue rainDamage;
        private final ForgeConfigSpec.BooleanValue rainDamageEnabled;

        // Difficulty
        private final ForgeConfigSpec.DoubleValue maxDifficulty;
        private final ForgeConfigSpec.IntValue difficultyIncreaseRate;

        private Common(ForgeConfigSpec.Builder configBuilder) {
            configBuilder.push("rain");

            this.rainTickRate = configBuilder.comment("Determines the interval in which rain damage should be dealt in seconds. A value of 2 will inflict rain damage on players every 2 seconds.")
                    .defineInRange("rainTickRate", 6, 1, 1000);

            this.rainDamage = configBuilder.comment("The amount of damage that should be dealt to players on rain tick.")
                    .defineInRange("rainDamage", 1, 1, 10000);

            this.rainDamageEnabled = configBuilder.comment("Set to false to disable rain damage, or to true to turn it on.")
                    .define("enableRainDamage", true);

            configBuilder.pop();
            configBuilder.push("difficulty");

            this.maxDifficulty = configBuilder.comment("Sets the max difficulty that can be reached before it stops increasing.")
                    .defineInRange("maxDifficulty", 150.0D, 0.0D, 10000.0D);

            this.difficultyIncreaseRate = configBuilder.comment("Sets the rate at which difficulty increases over time in minutes. A value of 5 will make the difficulty increase every 5 minutes.")
                    .defineInRange("difficultyIncreaseRate", 4, 1, 100);

            configBuilder.pop();
        }

        // Rain
        public int getRainTickRate() {
            return this.rainTickRate.get() * 20;
        }

        public float getRainDamage() {
            return (float) this.rainDamage.get();
        }

        public boolean rainDamageEnabled() {
            return this.rainDamageEnabled.get();
        }

        // Difficulty
        public double getMaxDifficulty() {
            return this.maxDifficulty.get();
        }

        public int getDifficultyIncreaseRate() {
            return this.difficultyIncreaseRate.get();
        }
    }
}
