package com.toast.apocalypse.client.renderer.entity.living.fearwolf;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.FearwolfEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class FearwolfRenderer<T extends FearwolfEntity> extends MobRenderer<T, FearwolfModel<T>> {

    private static final ResourceLocation TEXTURE = Apocalypse.resourceLoc("textures/entity/fearwolf/fearwolf.png");

    public FearwolfRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new FearwolfModel<>(), 1.3F);
        this.addLayer(new FearwolfEyesLayer<>(this));
    }

    @Override
    protected void scale(T destroyer, MatrixStack matrixStack, float scale) {
        matrixStack.scale(2.0F, 2.0F, 2.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(T destroyer) {
        return TEXTURE;
    }
}
