package com.toast.apocalypse.common.command.argument;

import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;

public class ApocalypseArgumentTypes {

    public static void register() {
        ArgumentTypes.register("apocalypse:difficulty", DifficultyArgument.class, new ArgumentSerializer<>(DifficultyArgument::difficulty));
    }
}
