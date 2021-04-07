package com.toast.apocalypse.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to detect mod
 * plugins. Your mod plugin class
 * must be annotated, or it
 * will go unnoticed!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApocalypsePlugin {

    /**
     * If your plugin happens to be a standalone thing
     * that doesn't interact with anything else than
     * Apocalypse and vanilla you can leave this empty.
     *
     * If not, you should return your mod's modid.
     *
     * @return Your mod's modid or an empty String
     *         if this plugin does not depend on
     *         a mod being loaded.
     */
    String modid() default "";
}
