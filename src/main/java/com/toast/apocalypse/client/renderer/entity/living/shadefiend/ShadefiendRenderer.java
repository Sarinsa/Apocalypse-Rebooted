package com.toast.apocalypse.client.renderer.entity.living.shadefiend;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.toast.apocalypse.client.ApocalypseModelLayers;
import com.toast.apocalypse.client.ApocalypseRenderTypes;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.Shadefiend;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ShadefiendRenderer extends MobRenderer<Shadefiend, ShadefiendModel> {

    private static final ResourceLocation TEXTURE = Apocalypse.resourceLoc("textures/entity/shadefiend/shadefiend.png");

    public ShadefiendRenderer(EntityRendererProvider.Context context) {
        super(context, new ShadefiendModel(context.bakeLayer(ApocalypseModelLayers.SHADEFIEND)), 0.75F);
        getModel().renderType = (resourceLocation) -> ApocalypseRenderTypes.entityCutoutNoCullBlend(resourceLocation, RenderStateShard.TransparencyStateShard.ADDITIVE_TRANSPARENCY);
    }

    @Override
    public ResourceLocation getTextureLocation(Shadefiend shadefiend) {
        return TEXTURE;
    }

    @Override
    protected boolean isShaking(Shadefiend shadefiend) {
        return shadefiend.isInLight();
    }

    @Override
    protected void scale(Shadefiend shadefiend, PoseStack poseStack, float partialTick) {
        poseStack.translate(0.0F, 1.3125F, 0.1875F);
    }

    @Override
    protected void setupRotations(Shadefiend shadefiend, PoseStack poseStack, float bob, float bodyYRot, float partialTick) {
        super.setupRotations(shadefiend, poseStack, bob, bodyYRot, partialTick);
        poseStack.mulPose(Axis.XP.rotationDegrees(shadefiend.getXRot()));
    }
}
