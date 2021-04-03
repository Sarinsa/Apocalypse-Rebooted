package com.toast.apocalypse.client.renderers.entity.grump;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.GrumpEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.GhastModel;
import net.minecraft.util.ResourceLocation;

public class GrumpRenderer<T extends GrumpEntity> extends MobRenderer<T, GhastModel<T>> {

    private static final ResourceLocation GRUMP_TEXTURE = Apocalypse.resourceLoc("textures/entity/grump/grump.png");

    public GrumpRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new GhastModel<>(), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(T grump) {
        return GRUMP_TEXTURE;
    }
}
