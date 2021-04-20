package com.toast.apocalypse.common.core.config;

import net.minecraft.block.Blocks;
import net.minecraft.world.Dimension;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

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

        private static final List<String> PENALTY_DIMENSIONS = new ArrayList<>();
        private static final List<String> DESTROYER_PROOF_BLOCKS = new ArrayList<>();

        static {
            PENALTY_DIMENSIONS.add(Dimension.NETHER.location().toString());
            PENALTY_DIMENSIONS.add(Dimension.END.location().toString());

            DESTROYER_PROOF_BLOCKS.add(Blocks.BEDROCK.getRegistryName().toString());
        }

        // Rain
        private final ForgeConfigSpec.IntValue rainTickRate;
        private final ForgeConfigSpec.IntValue rainDamage;
        private final ForgeConfigSpec.BooleanValue rainDamageEnabled;

        // Difficulty
        private final ForgeConfigSpec.BooleanValue multiplayerDifficultyScaling;
        private final ForgeConfigSpec.DoubleValue difficultyRateMultiplier;
        private final ForgeConfigSpec.DoubleValue sleepPenalty;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionsPenaltyList;
        private final ForgeConfigSpec.DoubleValue dimensionPenalty;

        // Misc
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> destroyerProofBlocks;

        // Misc

        private Common(ForgeConfigSpec.Builder configBuilder) {
            configBuilder.push("rain");

            this.rainTickRate = configBuilder.comment("Determines the interval in which rain damage should be dealt in seconds. A value of 2 will inflict rain damage on players every 2 seconds.")
                    .defineInRange("rainTickRate", 3, 1, 1000);

            this.rainDamage = configBuilder.comment("The amount of damage that should be dealt to players on rain tick.")
                    .defineInRange("rainDamage", 1, 1, 10000);

            this.rainDamageEnabled = configBuilder.comment("Set to false to disable rain damage, or to true to turn it on.")
                    .define("enableRainDamage", true);

            configBuilder.pop();
            configBuilder.push("difficulty");

            this.multiplayerDifficultyScaling = configBuilder.comment("If enabled, world difficulty will increased by the configured multiplier")
                    .define("multiplayerDifficultyScaling", true);

            this.difficultyRateMultiplier = configBuilder.comment("Only relevant if multiplayer difficulty scaling is enabled. For example, a value of 0.05 will apply an additional +5% difficulty increment per online player (If only one player is online this multiplier will not be active)")
                    .defineInRange("difficultyRateMultiplier", 0.05D, 0.01D, 10.0D);

            this.sleepPenalty = configBuilder.comment("Sets the multiplier used to increase world difficulty when players sleep through a night or thunderstorm.")
                    .defineInRange("sleepPenalty", 2.0D, 0.0D, 1000.0D);

            this.dimensionsPenaltyList = configBuilder.comment("A list of dimensions that should give difficulty penalty. Difficulty increases more in these dimensions.")
                    .define("dimensionPenaltyList", PENALTY_DIMENSIONS);

            this.dimensionPenalty = configBuilder.comment("The difficulty rate multiplier used when any player on the server is in a dimension with penalty.")
                    .defineInRange("dimensionPenalty", 1.5D, 0.0D, 1000.0D);

            configBuilder.pop();
            configBuilder.comment("This section revolves around everything related to the full moon sieges.");
            configBuilder.push("full_moon");

            configBuilder.comment("The spawn weights for all the full moon mobs.");
            configBuilder.push("spawn_chance");


            configBuilder.pop();
            configBuilder.comment("The maximum amount of a specific full moon mob that can spawn during a full moon siege.");
            configBuilder.push("max_spawn_count");

            configBuilder.pop();
            configBuilder.comment("The minimum amount of a specific full moon mob that can spawn during a full moon siege.");
            configBuilder.push("min_spawn_count");

            configBuilder.pop();
            configBuilder.pop();
            configBuilder.push("misc");

            this.destroyerProofBlocks = configBuilder.comment("A list of blocks that the destroyer cannot explode. Generally speaking this list should be empty since destroyers are supposed to destroy any block, but if an exception is absolutely needed, the block in question can be whitelisted here.")
                    .define("destroyerProofBlocks", DESTROYER_PROOF_BLOCKS);

            configBuilder.pop();
        }

        //
        // RAIN DAMAGE
        //
        public int getRainTickRate() {
            return this.rainTickRate.get() * 20;
        }

        public float getRainDamage() {
            return (float) this.rainDamage.get();
        }

        public boolean rainDamageEnabled() {
            return this.rainDamageEnabled.get();
        }

        //
        // DIFFICULTY
        //
        public boolean multiplayerDifficultyScaling() {
            return this.multiplayerDifficultyScaling.get();
        }

        public double getDifficultyRateMultiplier() {
            return this.difficultyRateMultiplier.get();
        }

        public double getSleepPenalty() {
            return this.sleepPenalty.get();
        }

        public List<? extends String> getDifficultyPenaltyDimensions() {
            return this.dimensionsPenaltyList.get();
        }

        public double getDimensionPenalty() {
            return this.dimensionPenalty.get();
        }

        //
        //  MISC
        //
        public List<? extends String> getDestroyerProofBlocks() {
            return this.destroyerProofBlocks.get();
        }
    }
}
