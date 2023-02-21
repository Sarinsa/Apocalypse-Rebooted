package com.toast.apocalypse.client.renderer.entity.living.fearwolf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.Fearwolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FearwolfRenderer<T extends Fearwolf> extends MobRenderer<T, FearwolfModel<T>> {

    private static final ResourceLocation TEXTURE = Apocalypse.resourceLoc("textures/entity/fearwolf/fearwolf.png");

    public FearwolfRenderer(EntityRendererProvider.Context context) {
        super(context, new FearwolfModel<>(), 1.3F);
        this.addLayer(new FearwolfEyesLayer<>(this));
    }

    @Override
    protected void scale(T fearwolf, PoseStack poseStack, float scale) {
        poseStack.scale(2.0F, 2.0F, 2.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(T destroyer) {
        return TEXTURE;
    }
}
