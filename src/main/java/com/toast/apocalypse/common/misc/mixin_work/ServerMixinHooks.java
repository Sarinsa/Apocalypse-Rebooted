package com.toast.apocalypse.common.misc.mixin_work;

import com.toast.apocalypse.common.core.config.ApocalypseCommonConfig;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ServerMixinHooks {

    public static void onServerWorldSetDayTime(ServerWorld serverWorld, CallbackInfo ci) {
        if (ApocalypseCommonConfig.COMMON.getPauseDaylightCycle() && serverWorld.getServer().getPlayerCount() < 1) {
            ci.cancel();
        }
    }
}
