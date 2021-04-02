package com.toast.apocalypse.client.renderers.entity.ghost;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.GhostEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.util.ResourceLocation;

public class GhostRenderer<T extends GhostEntity> extends MobRenderer<T, GhostModel<T>> {

    public static final ResourceLocation GHOST_TEXTURE = Apocalypse.resourceLoc("textures/entity/ghost/ghost.png");

    public GhostRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new GhostModel<>(), 0.0F);
        this.addLayer(new GhostEyesLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(T ghost) {
        return GHOST_TEXTURE;
    }
}
