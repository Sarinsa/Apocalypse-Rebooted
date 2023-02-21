package com.toast.apocalypse.client.renderer.entity.living.ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.Ghost;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

/** Renders the solid, fullbright eyes of the ghost */
public class GhostEyesLayer<T extends Ghost> extends RenderLayer<T, GhostModel<T>> {

    private static final RenderType EYES = RenderType.entityCutout(Apocalypse.resourceLoc("textures/entity/ghost/ghost_eyes.png"));

    public GhostEyesLayer(RenderLayerParent<T, GhostModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T ghost, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        VertexConsumer vertexConsumer = buffer.getBuffer(EYES);
        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
