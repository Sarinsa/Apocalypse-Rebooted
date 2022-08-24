package com.toast.apocalypse.client;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ApocalypseKeyBindings {

    public static final KeyBinding TOGGLE_DIFFICULTY = create("toggleDifficulty", KeyConflictContext.IN_GAME, InputMappings.getKey("key.keyboard.c"));
    public static final KeyBinding GRUMP_INTERACTION = create("launchGrumpHook", KeyConflictContext.IN_GAME, InputMappings.getKey("key.keyboard.v"));

    protected static void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(TOGGLE_DIFFICULTY);
    }

    private static KeyBinding create(String name, IKeyConflictContext conflictContext, InputMappings.Input input) {
        return new KeyBinding("key.apocalypse." + name, conflictContext, input, Apocalypse.MOD_NAME);
    }
}
