package com.toast.apocalypse.common.core.config;

import com.toast.apocalypse.common.entity.living.*;
import com.toast.apocalypse.common.util.References;
import net.minecraft.block.Blocks;
import net.minecraft.world.Dimension;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
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
        private final ForgeConfigSpec.BooleanValue averageGroupDifficulty;

        // Full moon stuff
        private final ForgeConfigSpec.DoubleValue fullMoonSiegeChance;
        private final ForgeConfigSpec.BooleanValue denySleep;
        private final ForgeConfigSpec.DoubleValue difficultyUntilNextIncrease;
        private final HashMap<Class<? extends IFullMoonMob>, ForgeConfigSpec.DoubleValue> spawnWeights = new HashMap<>();
        private final HashMap<Class<? extends IFullMoonMob>, ForgeConfigSpec.LongValue> moonMobStartDifficulties = new HashMap<>();
        private final HashMap<Class<? extends IFullMoonMob>, ForgeConfigSpec.DoubleValue> moonMobAdditionalCount = new HashMap<>();
        private final HashMap<Class<? extends IFullMoonMob>, ForgeConfigSpec.DoubleValue> moonMobMinCount = new HashMap<>();

        // Attributes and potions
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> healthBlacklist;
        private final ForgeConfigSpec.DoubleValue healthLunarFlatBonus;
        private final ForgeConfigSpec.DoubleValue healthLunarMultBonus;
        private final ForgeConfigSpec.DoubleValue healthTimeSpan;
        private final ForgeConfigSpec.DoubleValue healthFlatBonus;
        private final ForgeConfigSpec.DoubleValue healthFlatBonusMax;
        private final ForgeConfigSpec.DoubleValue healthMultBonus;
        private final ForgeConfigSpec.DoubleValue healthMultBonusMax;

        private final ForgeConfigSpec.ConfigValue<List<? extends String>> damageBlacklist;
        private final ForgeConfigSpec.DoubleValue damageLunarFlatBonus;
        private final ForgeConfigSpec.DoubleValue damageLunarMultBonus;
        private final ForgeConfigSpec.DoubleValue damageTimeSpan;
        private final ForgeConfigSpec.DoubleValue damageFlatBonus;
        private final ForgeConfigSpec.DoubleValue damageFlatBonusMax;
        private final ForgeConfigSpec.DoubleValue damageMultBonus;
        private final ForgeConfigSpec.DoubleValue damageMultBonusMax;

        private final ForgeConfigSpec.ConfigValue<List<? extends String>> speedBlacklist;
        private final ForgeConfigSpec.DoubleValue speedLunarMultBonus;
        private final ForgeConfigSpec.DoubleValue speedTimeSpan;
        private final ForgeConfigSpec.DoubleValue speedMultBonus;
        private final ForgeConfigSpec.DoubleValue speedMultBonusMax;

        // Misc
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> destroyerProofBlocks;
        private final ForgeConfigSpec.IntValue grumpBucketHelmetChance;

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

            this.averageGroupDifficulty = configBuilder.comment("If enabled, players that are close to eachother will have the average of their difficulty added together used instead of the nearby player with the highest difficulty.")
                    .define("averageGroupDifficulty", false);

            configBuilder.pop();
            configBuilder.comment("This section revolves around everything related to the full moon sieges.");
            configBuilder.push("full_moon");

            this.fullMoonSiegeChance = configBuilder.comment("The chance for a full moon to trigger a full moon siege event. Default value is 1.0 (100% chance)")
                    .defineInRange("full_moon_siege_chance", 1.0, 0.0, 1.0);

            this.denySleep = configBuilder.comment("If enabled, players will not be able to sleep during nights when it is a full moon.")
                    .define("deny_sleep", true);

            this.difficultyUntilNextIncrease = configBuilder.comment("How many levels of difficulty must pass before the additional full moon mob counts increases. For example, a value of 30.5 will increase the number of full moon mobs spawning during sieges for every 30.5 levels of difficulty passed.")
                    .defineInRange("difficulty_until_next_increase", 40.0D, 0.1D, 100000.0D);

            configBuilder.comment("The spawn weights for all the full moon mobs.");
            configBuilder.push("spawn_chance");
            createSpawnWeight(BreecherEntity.class, "breecher", 4.0D, configBuilder);
            createSpawnWeight(GhostEntity.class, "ghost", 8.0D, configBuilder);
            createSpawnWeight(DestroyerEntity.class, "destroyer", 2.0D, configBuilder);
            createSpawnWeight(SeekerEntity.class, "seeker", 2.0D, configBuilder);
            createSpawnWeight(GrumpEntity.class, "grump", 3.0D, configBuilder);
            configBuilder.pop();

            configBuilder.comment("The difficulty at which a specific type of full moon mob can start to spawn during sieges (It might be smart to let at least one type spawn at 0).");
            configBuilder.push("spawn_start_difficulties");
            createStartDifficulty(BreecherEntity.class, "breecher", 10, configBuilder);
            createStartDifficulty(GhostEntity.class, "ghost", 0, configBuilder);
            createStartDifficulty(DestroyerEntity.class, "destroyer", 75, configBuilder);
            createStartDifficulty(SeekerEntity.class, "seeker", 50, configBuilder);
            createStartDifficulty(GrumpEntity.class, "grump", 20, configBuilder);
            configBuilder.pop();

            configBuilder.comment("The additional amount of a specific full moon mob that can spawn during a full moon siege.");
            configBuilder.push("max_spawn_count");
            createMobAdditionalCount(BreecherEntity.class, "breecher", 2.0D, configBuilder);
            createMobAdditionalCount(GhostEntity.class, "ghost", 6.0D, configBuilder);
            createMobAdditionalCount(DestroyerEntity.class, "destroyer", 1.0D, configBuilder);
            createMobAdditionalCount(SeekerEntity.class, "seeker", 1.0D, configBuilder);
            createMobAdditionalCount(GrumpEntity.class, "grump", 4.0D, configBuilder);
            configBuilder.pop();

            configBuilder.comment("The minimum amount of a specific full moon mob that can spawn during a full moon siege.");
            configBuilder.push("min_spawn_count");
            createMobMinCount(BreecherEntity.class, "breecher", 4.0D, configBuilder);
            createMobMinCount(GhostEntity.class, "ghost", 4.0D, configBuilder);
            createMobMinCount(DestroyerEntity.class, "destroyer", 1.0D, configBuilder);
            createMobMinCount(SeekerEntity.class, "seeker", 1.0D, configBuilder);
            createMobMinCount(GrumpEntity.class, "grump", 1.5D, configBuilder);
            configBuilder.pop();
            configBuilder.pop();

            configBuilder.push("attributes_and_potions");
            configBuilder.comment("This section contains everything related to mob attributes and potion effects.");

            configBuilder.push("health");
            this.healthBlacklist = configBuilder.comment("A list of entity types that do not gain any health bonuses. Empty by default. Example: [\"minecraft:creeper\", \"abundance:screecher\"]")
                    .define("health_blacklist", new ArrayList<>());

            this.healthLunarFlatBonus = configBuilder.comment("The flat bonus gained from a full moon in percentage.")
                    .defineInRange("health_lunar_flat_bonus", 10.0D, 0.0D, 10000.0D);

            this.healthLunarMultBonus = configBuilder.comment("The multiplier bonus gained from a full moon in percentage.")
                    .defineInRange("health_lunar_mult_bonus", 0.5D, 0.0D, 10000.0D);

            this.healthTimeSpan = configBuilder.comment("The difficulty value for each application of the below values.")
                    .defineInRange("health_time_span", 40.0D, 0.1D, 10000.0D);

            this.healthFlatBonus = configBuilder.comment("The flat bonus given each \"_time_span\" days.")
                    .defineInRange("health_flat_bonus", 1.0D, 0.0D, 10000.0D);

            this.healthFlatBonusMax = configBuilder.comment("The maximum flat bonus that can be given over time. Default is -1.0 (no limit).")
                    .defineInRange("health_flat_bonus_max", -1.0D, -1.0D, 10000.0D);

            this.healthMultBonus = configBuilder.comment("The multiplier bonus given for each \"_time_span\" days. Default is 0.8 (+80%).")
                    .defineInRange("health_mult_bonus", 0.8D, 0.0D, 10000.0D);

            this.healthMultBonusMax = configBuilder.comment("The maximum multiplier bonus that can be given over time. Default is -1.0 (no limit).")
                    .defineInRange("health_mult_bonus_max", -1.0D, -1.0D, 10000.0D);
            configBuilder.pop();

            configBuilder.push("damage");
            this.damageBlacklist = configBuilder.comment("A list of entity types that do not gain any damage bonuses. Empty by default. Example: [\"minecraft:creeper\", \"abundance:screecher\"]")
                    .define("damage_blacklist", new ArrayList<>());

            this.damageLunarFlatBonus = configBuilder.comment("The flat bonus gained from a full moon in percentage.")
                    .defineInRange("damage_lunar_flat_bonus", 1.0D, 0.0D, 10000.0D);

            this.damageLunarMultBonus = configBuilder.comment("The multiplier bonus gained from a full moon in percentage.")
                    .defineInRange("damage_lunar_mult_bonus", 0.2D, 0.0D, 10000.0D);

            this.damageTimeSpan = configBuilder.comment("The difficulty value for each application of the below values.")
                    .defineInRange("damage_time_span", 40.0D, 0.1D, 10000.0D);

            this.damageFlatBonus = configBuilder.comment("The flat bonus given each \"_time_span\" days.")
                    .defineInRange("damage_flat_bonus", 1.0D, 0.0D, 10000.0D);

            this.damageFlatBonusMax = configBuilder.comment("The maximum flat bonus that can be given over time. Default is -1.0 (no limit).")
                    .defineInRange("damage_flat_bonus_max", -1.0D, -1.0D, 10000.0D);

            this.damageMultBonus = configBuilder.comment("The multiplier bonus given for each \"_time_span\" days. Default is 0.3 (+30%).")
                    .defineInRange("damage_mult_bonus", 0.3D, 0.0D, 10000.0D);

            this.damageMultBonusMax = configBuilder.comment("The maximum multiplier bonus that can be given over time. Default is 5.0 (+500%).")
                    .defineInRange("damage_mult_bonus_max", 5.0D, -1.0D, 10000.0D);
            configBuilder.pop();

            configBuilder.push("movement_speed");
            this.speedBlacklist = configBuilder.comment("A list of entity types that do not gain any speed bonuses. Empty by default. Example: [\"minecraft:creeper\", \"abundance:screecher\"]")
                    .define("speed_blacklist", new ArrayList<>());

            this.speedLunarMultBonus = configBuilder.comment("The multiplier bonus gained from a full moon in percentage. Default is 0.1 (+10%)")
                    .defineInRange("speed_lunar_mult_bonus", 0.1D, 0.0D, 1000.0D);

            this.speedTimeSpan = configBuilder.comment("The difficulty value for each application of the below values.")
                    .defineInRange("speed_time_span", 40.0D, 0.1D, 10000.0D);

            this.speedMultBonus = configBuilder.comment("The multiplier bonus given for each \"_time_span\" days. Default is 0.05 (+5%).")
                    .defineInRange("damage_mult_bonus", 0.05D, 0.0D, 10000.0D);

            this.speedMultBonusMax = configBuilder.comment("The maximum multiplier bonus that can be given over time. Default is 0.2 (+20%).")
                    .defineInRange("damage_mult_bonus_max", 0.2D, -1.0D, 10000.0D);
            configBuilder.pop();

            /*
       Properties.add(config, Properties.SPEED, "_blacklist", "", "Comma-separated list of string entity ids that will not gain any of these bonuses. Default is none.");
        Properties.add(config, Properties.SPEED, "_lunar_mult_bonus", 0.1, "The multiplier bonus gained from a full moon. Default is 0.1 (+10% on full moon).");
        Properties.add(config, Properties.SPEED, "_time_span", 40.0, "The number of days for each application of the below values. Default is 40.0.");
        Properties.add(config, Properties.SPEED, "mult_bonus", 0.05, "The multiplier bonus given for each \"_time_span\" days. Default is 0.05 (+5%).");
        Properties.add(config, Properties.SPEED, "mult_bonus_max", 0.2, "The maximum multiplier bonus that can be given over time. Default is 0.2 (+20%).");
             */

            configBuilder.pop();
            configBuilder.push("misc");
            this.destroyerProofBlocks = configBuilder.comment("A list of blocks that the destroyer cannot explode. Generally speaking this list should be empty since destroyers are supposed to destroy any block, but if an exception is absolutely needed, the block in question can be whitelisted here.")
                    .define("destroyerProofBlocks", DESTROYER_PROOF_BLOCKS);

            this.grumpBucketHelmetChance = configBuilder.comment("This is the chance in percentage for grumps to spawn with a bucket helmet equipped. Grumps with bucket helmets are heavily armored against arrows.")
                    .defineInRange("grumpBucketHelmetChance", 5, 0, 100);

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

        public boolean getAverageGroupDifficulty() {
            return this.averageGroupDifficulty.get();
        }


        //
        // FULL MOON
        //
        public double getFullMoonSiegeChance() {
            return this.fullMoonSiegeChance.get();
        }

        public boolean denySleep() {
            return this.denySleep.get();
        }

        public double getDifficultyUntilNextIncrease() {
            return this.difficultyUntilNextIncrease.get();
        }

        public double getFullMoonMobSpawnWeight(Class<? extends IFullMoonMob> entityClass) {
            return this.spawnWeights.containsKey(entityClass) ? this.spawnWeights.get(entityClass).get() : 0.0D;
        }

        public long getMoonMobStartDifficulty(Class<? extends IFullMoonMob> entityClass) {
            return this.moonMobStartDifficulties.containsKey(entityClass) ? (this.moonMobStartDifficulties.get(entityClass).get() * References.DAY_LENGTH) : 0;
        }

        public double getMoonMobAdditionalCount(Class<? extends IFullMoonMob> entityClass) {
            return this.moonMobAdditionalCount.containsKey(entityClass) ? this.moonMobAdditionalCount.get(entityClass).get() : 0.0D;
        }

        public double getMoonMobMinCount(Class<? extends IFullMoonMob> entityClass) {
            return this.moonMobMinCount.containsKey(entityClass) ? this.moonMobMinCount.get(entityClass).get() : 0.0D;
        }


        //
        // ATTRIBUTES AND POTIONS
        //
        public List<? extends String> getHealthBlacklist() {
            return this.healthBlacklist.get();
        }

        public double getHealthLunarFlatBonus() {
            return this.healthLunarFlatBonus.get();
        }

        public double getHealthLunarMultBonus() {
            return this.healthLunarMultBonus.get();
        }

        public double getHealthTimeSpan() {
            return this.healthTimeSpan.get() * References.DAY_LENGTH;
        }

        public double getHealthFlatBonus() {
            return this.healthFlatBonus.get();
        }

        public double getHealthFlatBonusMax() {
            return this.healthFlatBonusMax.get();
        }

        public double getHealthMultBonus() {
            return this.healthMultBonus.get();
        }

        public double getHealthMultBonusMax() {
            return this.healthMultBonusMax.get();
        }

        public List<? extends String> getDamageBlacklist() {
            return this.damageBlacklist.get();
        }

        public double getDamageLunarFlatBonus() {
            return this.damageLunarFlatBonus.get();
        }

        public double getDamageLunarMultBonus() {
            return this.damageLunarMultBonus.get();
        }

        public double getDamageTimeSpan() {
            return this.damageTimeSpan.get() * References.DAY_LENGTH;
        }

        public double getDamageFlatBonus() {
            return this.damageFlatBonus.get();
        }

        public double getDamageFlatBonusMax() {
            return this.damageFlatBonusMax.get();
        }

        public double getDamageMultBonus() {
            return this.damageMultBonus.get();
        }

        public double getDamageMultBonusMax() {
            return this.damageMultBonusMax.get();
        }

        public List<? extends String> getSpeedBlacklist() {
            return this.speedBlacklist.get();
        }

        public double getSpeedLunarMultBonus() {
            return this.speedLunarMultBonus.get();
        }

        public double getSpeedTimeSpan() {
            return this.speedTimeSpan.get() * References.DAY_LENGTH;
        }

        public double getSpeedMultBonus() {
            return this.speedMultBonus.get();
        }

        public double getSpeedMultBonusMax() {
            return this.speedMultBonusMax.get();
        }


        //
        //  MISC
        //
        public List<? extends String> getDestroyerProofBlocks() {
            return this.destroyerProofBlocks.get();
        }

        public int getGrumpBucketHelmetChance() {
            return this.grumpBucketHelmetChance.get();
        }


        /*
         * Helper methods and whatnot
         */
        private void createSpawnWeight(Class<? extends IFullMoonMob> entityClass, String name, double defaultWeight, ForgeConfigSpec.Builder configBuilder) {
            this.spawnWeights.put(entityClass, configBuilder.defineInRange(name, defaultWeight, 0, 100));
        }

        private void createStartDifficulty(Class<? extends IFullMoonMob> entityClass, String name, long defaultStart, ForgeConfigSpec.Builder configBuilder) {
            this.moonMobStartDifficulties.put(entityClass, configBuilder.defineInRange(name, defaultStart, 0, 100000));
        }

        private void createMobAdditionalCount(Class<? extends IFullMoonMob> entityClass, String name, double defaultAdditional, ForgeConfigSpec.Builder configBuilder) {
            this.moonMobAdditionalCount.put(entityClass, configBuilder.defineInRange(name, defaultAdditional, 0, 100));
        }

        private void createMobMinCount(Class<? extends IFullMoonMob> entityClass, String name, double defaultMin, ForgeConfigSpec.Builder configBuilder) {
            this.moonMobMinCount.put(entityClass, configBuilder.defineInRange(name, defaultMin, 0, 100));
        }
    }
}
