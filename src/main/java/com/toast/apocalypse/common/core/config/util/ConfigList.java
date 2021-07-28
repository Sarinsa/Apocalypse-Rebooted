package com.toast.apocalypse.common.core.config.util;

import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;

public class ConfigList<T extends IForgeRegistryEntry<T>> extends ArrayList<String> {

    public void add(T element) {
        if (element.getRegistryName() == null)
            return;

        this.add(element.getRegistryName().toString());
    }

    @SafeVarargs
    public final void addElements(T... elements) {
        for (T element : elements) {
            if (element.getRegistryName() == null)
                return;

            this.add(element.getRegistryName().toString());
        }
    }
}
