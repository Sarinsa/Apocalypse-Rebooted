package com.toast.apocalypse.common.core.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;

public class AcidRainConfig extends AbstractConfigFile {

    public final General GENERAL;


    /** Builds the config spec that should be used for this config. */
    public AcidRainConfig(ConfigManager cfgManager, String cfgName) {
        super(cfgManager, cfgName,
                "This config contains settings related to Apocalypse's acid rain event."
        );
        GENERAL = new General(this);
    }


    public static class General extends AbstractConfigCategory<AcidRainConfig> {

        public final DoubleField acidRainChance;
        public final IntField damageRate;
        public final IntField rainDamage;


        General(AcidRainConfig parent) {
            super(parent, "general",
                    "General event settings");

            acidRainChance = SPEC.define(new DoubleField("acid_rain_chance", 0.5, DoubleField.Range.PERCENT,
                    "The chance of triggering an Acid Rain event when it starts raining. 1.0 = 100% chance, 0.5 = 50% etc.",
                    "Setting this to 0.0 effectively disables acid rain."));
            damageRate = SPEC.define(new IntField("damage_rate", 3, 1, 1000,
                    "Determines the interval in which acid rain damage should be dealt in seconds.",
                    "A value of 2 will inflict acid rain damage on players every 2 seconds."));
            rainDamage = SPEC.define(new IntField("rain_damage", 1, IntField.Range.NON_NEGATIVE,
                    "The amount of damage that should be dealt to players on acid rain tick.",
                    "Setting this to 0 disables acid rain damage."));


            SPEC.newLine();
        }
    }
}
