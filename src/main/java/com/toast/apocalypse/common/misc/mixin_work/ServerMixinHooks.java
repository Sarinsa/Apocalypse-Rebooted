package com.toast.apocalypse.common.misc.mixin_work;

import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ServerMixinHooks {

    public static void onServerWorldSetDayTime(ServerWorld serverWorld, CallbackInfo ci) {
        if (serverWorld.getServer().getPlayerCount() < 1) {
            ci.cancel();
        }
    }
}
