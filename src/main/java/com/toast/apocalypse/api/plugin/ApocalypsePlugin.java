package com.toast.apocalypse.api.plugin;

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
     * @return Your mod's modid or an empty String
     *         if this plugin does not depend on
     *         a mod being loaded.
     *         <br><br>
     *         This is used to make sure plugins doesn't
     *         get loaded if the mod that adds them failed to load
     *         itself.
     */
    String modid() default "";
}
