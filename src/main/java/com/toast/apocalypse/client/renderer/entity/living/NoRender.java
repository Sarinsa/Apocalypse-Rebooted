package com.toast.apocalypse.client.renderer.entity.living;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Used for entities that should not render
 * or for testing out entities that does not
 * have a model yet.
 */
public class NoRender<T extends Entity> extends EntityRenderer<T> {

    // No texture
    private static final ResourceLocation TEXTURE = new ResourceLocation("");

    public NoRender(EntityRendererManager rendererManager) {
        super(rendererManager);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
