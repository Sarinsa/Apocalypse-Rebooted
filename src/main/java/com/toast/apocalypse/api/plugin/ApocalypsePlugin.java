package com.toast.apocalypse.api.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to detect Apocalypse plugins.
 * Plugin classes must be annotated, or they will go unnoticed!
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface ApocalypsePlugin {
    
    /**
     * @return The ID of the mod this plugin belongs to.
     * <br><br>
     * If this plugin is either a standalone addition or doesn't depend on the mod
     * it belongs to being loaded in order to function correctly, an empty string should be returned.
     */
    String modId() default "";
}
