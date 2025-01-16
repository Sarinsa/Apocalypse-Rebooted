package com.toast.apocalypse.common.misc.mixin_work;

import com.toast.apocalypse.client.ClientUtil;
import com.toast.apocalypse.client.screen.misc.ApocalypseWCTab;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

public class ClientMixinHooks {

    public static void onRenderSnowAndRain(Level level, int ticks, LightTexture lightTexture, float partialTick, double x, double y, double z, CallbackInfo ci) {
        if (ClientUtil.ACID_RAIN_TICKER.renderRain(level, ticks, lightTexture, partialTick, x, y, z))
            ci.cancel();
    }

    public static void onTickRain(Level level, int ticks, Camera camera, CallbackInfo ci) {
        if (ClientUtil.ACID_RAIN_TICKER.tickRain(level, ticks, camera))
            ci.cancel();
    }

    public static Tab[] createWorldScreenModifyTabs(Tab[] originalTabs) {
        Tab apocalypseTab = new ApocalypseWCTab();

        Tab[] newTabs = new Tab[originalTabs.length + 1];

        System.arraycopy(originalTabs, 0, newTabs, 0, originalTabs.length);
        newTabs[newTabs.length - 1] = apocalypseTab;

        return newTabs;
    }
}
