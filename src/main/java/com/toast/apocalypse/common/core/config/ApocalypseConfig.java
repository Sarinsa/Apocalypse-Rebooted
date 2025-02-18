package com.toast.apocalypse.common.core.config;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.difficulty.MobEquipmentHandler;
import fathertoast.crust.api.config.common.ConfigManager;

public class ApocalypseConfig {


    /** Settings related to difficulty. */
    public static final DifficultyConfig DIFFICULTY = new DifficultyConfig(ConfigManager.getRequired(Apocalypse.MODID), "difficulty");
    /** Settings related to various mob buffs (equipment, potion effects, attribute mods etc.). */
    public static final MobBuffingConfig MOB_BUFFING = new MobBuffingConfig(ConfigManager.getRequired(Apocalypse.MODID), "mob_boost");
    /** Settings related to lunar siege/full moon event. */
    public static final LunarSiegeConfig LUNAR_SIEGE = new LunarSiegeConfig(ConfigManager.getRequired(Apocalypse.MODID), "events/lunar_siege");
    /** Settings related to acid rain. */
    public static final AcidRainConfig ACID_RAIN = new AcidRainConfig(ConfigManager.getRequired(Apocalypse.MODID), "events/acid_rain");
    /** Settings related to darkness event. */
    public static final COTSConfig CALL_OF_THE_SHADOWS = new COTSConfig(ConfigManager.getRequired(Apocalypse.MODID), "events/call_of_the_shadows");
    /** Settings related to thunderstorm event. */
    public static final ThunderstormConfig THUNDERSTORM = new ThunderstormConfig(ConfigManager.getRequired(Apocalypse.MODID), "events/thunderstorm");
    /** Settings that doesn't really fit elsewhere. */
    public static final MiscConfig MISC = new MiscConfig(ConfigManager.getRequired(Apocalypse.MODID), "misc");



    /** Performs initial loading of our configs. */
    public static void initialize() {
        ConfigManager manager = ConfigManager.getRequired(Apocalypse.MODID);
        manager.freezeFileWatcher = true;

        DIFFICULTY.SPEC.initialize();
        MOB_BUFFING.SPEC.initialize();
        LUNAR_SIEGE.SPEC.initialize();
        ACID_RAIN.SPEC.initialize();
        CALL_OF_THE_SHADOWS.SPEC.initialize();
        THUNDERSTORM.SPEC.initialize();
        MISC.SPEC.initialize();

        manager.freezeFileWatcher = false;

        MobEquipmentHandler.refreshArmorMaps(MOB_BUFFING.EQUIPMENT.armorTierList);
    }
}
