package com.toast.apocalypse.common.util;

public class References {

    // How many ticks there are in one day
    public static final long DAY_LENGTH = 24000L;
    public static final long MAX_DIFFICULTY_HARD_LIMIT = 100000L * DAY_LENGTH;
    public static final long DEFAULT_COLOR_CHANGE = 240L * DAY_LENGTH;

    // Translation keys
    public static final String FATHERLY_TOAST_DESC = "apocalypse.item_desc.fatherly_toast";
    public static final String BUCKET_HELM_DESC = "apocalypse.item_desc.bucket_helm";

    public static final String TRY_SLEEP_FULL_MOON = "title.bed.apocalypse.full_moon";

    public static final String SLEEP_PENALTY = "event.apocalypse.sleep_penalty";
    public static final String FULL_MOON = "event.apocalypse.full_moon";
    public static final String THUNDERSTORM = "event.apocalypse.thunderstorm";

    public static final String COMMAND_INVALID_DIFFICULTY_VALUE = "apocalypse.command.argument.difficulty.invalid_value";
    public static final String COMMAND_INVALID_MAX_DIFFICULTY_VALUE = "apocalypse.command.argument.max_difficulty.invalid_value";
    public static final String DIFFICULTY_SET_SINGLE = "apocalypse.command.difficulty.set_message.single";
    public static final String DIFFICULTY_SET_MULTIPLE = "apocalypse.command.difficulty.set_message.multiple";
    public static final String MAX_DIFFICULTY_SET_SINGLE = "apocalypse.command.difficulty.max_message.single";
    public static final String MAX_DIFFICULTY_SET_MULTIPLE = "apocalypse.command.difficulty.max_message.multiple";

    public static final String APOCALYPSE_WORLD_CREATE_BUTTON = "apocalypse.screen.button.create_world_config";
    public static final String APOCALYPSE_WORLD_CREATE_CONFIG_TITLE = "apocalypse.screen.title.create_world_config";
    public static final String MAX_DIFFICULTY_CONFIG_FIELD = "apocalypse.screen.text_field.create_world_config.max_difficulty";
    public static final String GRACE_PERIOD_CONFIG_FIELD = "apocalypse.screen.text_field.create_world_config.grace_period";
}
