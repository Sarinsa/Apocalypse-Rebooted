package com.toast.apocalypse.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.client.KeyMapping;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Apocalypse.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ApocalypseKeyBindings {

    public static final KeyMapping TOGGLE_DIFFICULTY = create("toggleDifficulty", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_C);
    public static final KeyMapping GRUMP_INTERACTION = create("launchGrumpHook", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_V);
    public static final KeyMapping GRUMP_DESCENT = create("grumpDescent", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_LCONTROL);


    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_DIFFICULTY);
        event.register(GRUMP_INTERACTION);
        event.register(GRUMP_DESCENT);
    }

    private static KeyMapping create(String name, IKeyConflictContext conflictContext, InputConstants.Type type, int keyCode) {
        return new KeyMapping("key.apocalypse." + name, conflictContext, type, keyCode, Apocalypse.MOD_NAME);
    }
}
