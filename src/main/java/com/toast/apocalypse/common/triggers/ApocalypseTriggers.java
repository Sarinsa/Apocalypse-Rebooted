package com.toast.apocalypse.common.triggers;

import net.minecraft.advancements.CriteriaTriggers;

public class ApocalypseTriggers {

    public static DifficultyChangeTrigger CHANGED_DIFFICULTY = CriteriaTriggers.register(new DifficultyChangeTrigger());


    public static void init() {}
}
