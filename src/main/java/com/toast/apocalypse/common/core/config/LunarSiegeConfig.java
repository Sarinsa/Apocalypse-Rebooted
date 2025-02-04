package com.toast.apocalypse.common.core.config;

import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.EntityListField;
import fathertoast.crust.api.config.common.field.RegistryEntryValueListField;
import fathertoast.crust.api.config.common.value.EntityEntry;
import fathertoast.crust.api.config.common.value.EntityList;
import fathertoast.crust.api.config.common.value.RegistryEntryValueList;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class LunarSiegeConfig extends AbstractConfigFile {

    public final General GENERAL;
    public final SiegeMobProperties SIEGE_MOB_PROPS;


    /** Builds the config spec that should be used for this config. */
    public LunarSiegeConfig(ConfigManager cfgManager, String cfgName) {
        super(cfgManager, cfgName,
                "This config contains settings related to Lunar Sieges / Full moon events."
        );
        SPEC.fileOnlyNewLine();
        SPEC.describeRegistryEntryList();
        SPEC.fileOnlyNewLine();

        GENERAL = new General(this);
        SIEGE_MOB_PROPS = new SiegeMobProperties(this);
    }

    public static class General extends AbstractConfigCategory<LunarSiegeConfig> {

        public final BooleanField enableLunarSieges;

        public final BooleanField despawnMobsOnDeath;


        General(LunarSiegeConfig parent) {
            super(parent, "general",
                    "General settings.");

            enableLunarSieges = SPEC.define(new BooleanField("enable_lunar_sieges", true,
                    "If enabled, every full moon night a 'lunar siege' will start, spawning stronger monsters and full moon monsters.",
                    "More settings related to this can be found further down in this config."));

            despawnMobsOnDeath = SPEC.define(new BooleanField("despawn_mobs_on_death", true,
                    "If enabled, any mobs that are still alive that were summoned by X player's Lunar Siege event will despawn if their target player dies.",
                    "Can help prevent horrible spawn-camping"));

            SPEC.newLine();
        }
    }


    public static class SiegeMobProperties extends AbstractConfigCategory<LunarSiegeConfig> {

        public final DoubleField difficultyPerIncrease;

        public final RegistryEntryValueListField<EntityType<?>> mobSpawnSettings;


        SiegeMobProperties(LunarSiegeConfig parent) {
            super(parent, "siege_mob_properties",
                    "Various properties of full moon mobs, such as spawn amount, starting difficulty etc.");

            difficultyPerIncrease = SPEC.define(new DoubleField("difficulty_per_increase", 20, 1.0, 10000.0D,
                    "The amount of difficulty levels the player must pass for additional full moon mob counts to increase.",
                    "(This refers to the 4th value in the entries in the 'mob_spawn_settings' list below)",
                    "For example, a value of 30.0 means that for every time the player passes another 30.0 levels of difficulty," +
                            " the amount of additional full moon mobs increases."));

            SPEC.newLine();

            mobSpawnSettings = SPEC.define( new RegistryEntryValueListField<>( "mob_spawn_settings", new RegistryEntryValueList<>( () -> ForgeRegistries.ENTITY_TYPES,
                    new RegistryValueEntry<>(
                            SiegeMobProperties.this.mobSpawnSettings,
                            ForgeRegistries.ENTITY_TYPES.getKey(ApocalypseEntities.BREECHER.get()),
                            5.0, 4, 20, 2.0
                    ),
                    new RegistryValueEntry<>(
                            SiegeMobProperties.this.mobSpawnSettings,
                            ForgeRegistries.ENTITY_TYPES.getKey(ApocalypseEntities.GHOST.get()),
                            45.0, 4, 25, 6.0
                    ),
                    new RegistryValueEntry<>(
                            SiegeMobProperties.this.mobSpawnSettings,
                            ForgeRegistries.ENTITY_TYPES.getKey(ApocalypseEntities.GRUMP.get()),
                            20.0, 2, 18, 2.5
                    ),
                    new RegistryValueEntry<>(
                            SiegeMobProperties.this.mobSpawnSettings,
                            ForgeRegistries.ENTITY_TYPES.getKey(ApocalypseEntities.SEEKER.get()),
                            70.0, 1, 8, 1.0
                    ),
                    new RegistryValueEntry<>(
                            SiegeMobProperties.this.mobSpawnSettings,
                            ForgeRegistries.ENTITY_TYPES.getKey(ApocalypseEntities.DESTROYER.get()),
                            100.0, 1, 6, 1.0
                    )
            )
            .setMultiValue(4)
            .setRangePos(),
                    "Contains various spawn settings for Full Moon siege mobs.",
                    "Entity types from other mods or vanilla can be added here too, if wanted.",
                    "This list should only contain entity type specific entries. Any tags, namespace wildcards or default values will not be used.",
                    "1st value: The difficulty level required for the given mob to start spawning in sieges.",
                    "2nd value: The minimum amount of the given mob type that will spawn in a full moon siege.",
                    "3rd value: The maximum amount of the given mob type that can spawn in a full moon siege.",
                    "4th value: Additional spawn count for the given mob type (works in conjunction with 'difficulty_per_additional_increase')."));

            SPEC.newLine();


        }
    }
}
