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
}
