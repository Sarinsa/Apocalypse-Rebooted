package com.toast.apocalypse.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ApocalypseKeyBindings {

    public static final KeyBinding TOGGLE_DIFFICULTY = create("key.apocalypse.toggleDifficulty", KeyConflictContext.IN_GAME, InputMappings.getKey("key.keyboard.c"));

    protected static void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(TOGGLE_DIFFICULTY);
    }

    private static KeyBinding create(String name, IKeyConflictContext conflictContext, InputMappings.Input input) {
        return new KeyBinding(name, conflictContext, input, "Apocalypse Rebooted");
    }
}
