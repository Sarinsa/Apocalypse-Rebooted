package com.toast.apocalypse.common.misc.mixin_work;

import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ServerMixinHooks {

    public static void onServerWorldSetDayTime(ServerLevel serverLevel, CallbackInfo ci) {
        if (ApocalypseCommonConfig.COMMON.getPauseDaylightCycle() && serverLevel.getServer().getPlayerCount() < 1) {
            ci.cancel();
        }
    }
}
