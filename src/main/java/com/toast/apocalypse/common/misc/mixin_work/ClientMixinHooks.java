package com.toast.apocalypse.common.misc.mixin_work;

import com.toast.apocalypse.client.ClientUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ClientMixinHooks {

    public static void onRenderSnowAndRain(Level level, int ticks, LightTexture lightTexture, float partialTick, double x, double y, double z, CallbackInfo ci) {
        if (ClientUtil.ACID_RAIN_TICKER.renderRain(level, ticks, lightTexture, partialTick, x, y, z))
            ci.cancel();
    }

    public static void onTickRain(Level level, int ticks, Camera camera, CallbackInfo ci) {
        if (ClientUtil.ACID_RAIN_TICKER.tickRain(level, ticks, camera))
            ci.cancel();
    }
}
