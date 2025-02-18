package com.toast.apocalypse.common.core.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;

public class ThunderstormConfig extends AbstractConfigFile {

    public final ThunderstormConfig.General GENERAL;


    /** Builds the config spec that should be used for this config. */
    public ThunderstormConfig(ConfigManager cfgManager, String cfgName) {
        super(cfgManager, cfgName,
                "This config contains settings related to Apocalypse's thunderstorm event."
        );
        GENERAL = new ThunderstormConfig.General(this);
    }


    public static class General extends AbstractConfigCategory<ThunderstormConfig> {

        public final BooleanField enabled;
        public final BooleanField spawnsIgnoreLight;


        General(ThunderstormConfig parent) {
            super(parent, "general",
                    "General event settings");

            enabled = SPEC.define(new BooleanField("enabled", true,
                    "If true, this event is enabled and will trigger if it starts thundering in the world."));
            spawnsIgnoreLight = SPEC.define(new BooleanField("spawns_ignore_light", false,
                    "If true, natural mob spawns will ignore light level and be able to spawn in both dark and bright areas."));

            SPEC.newLine();
        }
    }
}
