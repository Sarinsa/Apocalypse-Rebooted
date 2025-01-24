package com.toast.apocalypse.common.core.config;

import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.EntityListField;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.value.EntityEntry;
import fathertoast.crust.api.config.common.value.EntityList;
import fathertoast.crust.api.config.common.value.EnvironmentEntry;
import fathertoast.crust.api.config.common.value.EnvironmentList;
import fathertoast.crust.api.config.common.value.environment.dimension.DimensionTypeEnvironment;
import net.minecraft.world.level.Level;

public class DifficultyConfig extends AbstractConfigFile {

    public final General GENERAL;


    /** Builds the config spec that should be used for this config. */
    public DifficultyConfig(ConfigManager cfgManager, String cfgName) {
        super(cfgManager, cfgName,
                "This config contains settings related to Apocalypse difficulty."
        );
        SPEC.fileOnlyNewLine();
        SPEC.describeRegistryEntryList();
        SPEC.fileOnlyNewLine();
        SPEC.describeEnvironmentListPart1of2();
        SPEC.describeEnvironmentListPart2of2();
        SPEC.fileOnlyNewLine();

        GENERAL = new General(cfgManager, this);
    }


    public static class General extends AbstractConfigCategory<DifficultyConfig> {


        public final DoubleField multiplayerMultiplier;
        public final DoubleField sleepPenaltyMultiplier;

        public final EnvironmentListField dimensionPenaltyList;

        public final EntityListField mobSpawnDifficulties;


        General(ConfigManager cfgManager, DifficultyConfig parent) {
            super(parent, "general",
                    "General difficulty settings.");

            multiplayerMultiplier = SPEC.define(new DoubleField("multiplayer_multiplier", 1.0, 1.0, 10.0,
                    "This is a difficulty multiplier used for when more than one player is online.",
                    "For every additional player online, the rate at which difficulty increases for everyone is multiplied by this value.",
                    "Setting this to 1.0 essentially disables this feature."));
            sleepPenaltyMultiplier = SPEC.define(new DoubleField("sleep_penalty_multiplier", 2.0, 1.0, 1000.0,
                    "This is a difficulty multiplier used for punishing the player for sleeping through the night.",
                    "When this value is greater than 1.0, it causes players who sleep through the night to have their difficulty go higher " +
                            "than if they had just stayed up the whole night.",
                    "For example, a value of 1.5 would equal a 50% increase."));

            SPEC.newLine();

            dimensionPenaltyList = SPEC.define(new EnvironmentListField("dimension_penalty_list", new EnvironmentList(
                    new EnvironmentEntry(1.5,
                            new DimensionTypeEnvironment(cfgManager, Level.NETHER, false)),
                    new EnvironmentEntry(1.5,
                            new DimensionTypeEnvironment(cfgManager, Level.END, false))
            ).setRange(1.0D, 100.0D), "A list of difficulty multipliers linked to dimension types.",
                    "Used for dimensions that should make the player's difficulty rise 'quicker' than normal.",
                    "Additional conditions other than dimension type can be appended here, but intended use is dimension type check only."));

            SPEC.newLine();

            mobSpawnDifficulties = SPEC.define(new EntityListField("mob_spawn_difficulty_levels", new EntityList(
                    new EntityEntry(ApocalypseEntities.FEARWOLF.get(), 30.0D)
            ).setSingleValue().setRangePos(),
                    "A list of entity types linked with a difficulty value,",
                    "representing the difficulty level required for them to start spawning naturally in the world.",
                    "Used when checking the difficulty of the player closest to the attempted mob spawn."));

            SPEC.newLine();
        }
    }
}
