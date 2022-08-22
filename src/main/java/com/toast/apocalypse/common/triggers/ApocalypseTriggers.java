package com.toast.apocalypse.common.triggers;

import net.minecraft.advancements.CriteriaTriggers;

public class ApocalypseTriggers {

    public static PassedGracePeriodTrigger PASSED_GRACE_PERIOD = CriteriaTriggers.register(new PassedGracePeriodTrigger());
    public static TamedGrumpTrigger TAMED_GRUMP = CriteriaTriggers.register(new TamedGrumpTrigger());

    public static void init() {}
}
