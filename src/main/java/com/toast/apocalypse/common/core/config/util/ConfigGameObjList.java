package com.toast.apocalypse.common.core.config.util;

import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;

public class ConfigGameObjList<E> extends ArrayList<String> {

    private final IForgeRegistry<E> registry;

    public ConfigGameObjList(IForgeRegistry<E> registry) {
        this.registry = registry;
    }

    public void addElement(E element) {
        if (!registry.containsValue(element))
            return;

        add(registry.getKey(element).toString());
    }

    @SafeVarargs
    public final void addElements(E... elements) {
        for (E element : elements) {
            addElement(element);
        }
    }
}
