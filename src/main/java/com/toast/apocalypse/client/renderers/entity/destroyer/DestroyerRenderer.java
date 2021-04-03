package com.toast.apocalypse.client.renderers.entity.destroyer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.DestroyerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.GhastModel;
import net.minecraft.util.ResourceLocation;

public class DestroyerRenderer<T extends DestroyerEntity> extends MobRenderer<T, GhastModel<T>> {

    private static final ResourceLocation DESTROYER_TEXTURE = Apocalypse.resourceLoc("textures/entity/destroyer/destroyer.png");
    private static final ResourceLocation DESTROYER_FIRE_TEXTURE = Apocalypse.resourceLoc("textures/entity/destroyer/destroyer_fire.png");

    public DestroyerRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new GhastModel<>(), 3.0F);
    }

    @Override
    protected void scale(T destroyer, MatrixStack matrixStack, float scale) {
        matrixStack.scale(5.0F, 5.0F, 5.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(T destroyer) {
        return destroyer.isCharging() ? DESTROYER_FIRE_TEXTURE : DESTROYER_TEXTURE;
    }
}
