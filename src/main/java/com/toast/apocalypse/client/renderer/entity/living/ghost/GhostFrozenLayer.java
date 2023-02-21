package com.toast.apocalypse.client.renderer.entity.living.ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.toast.apocalypse.client.ApocalypseRenderTypes;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.Ghost;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class GhostFrozenLayer <T extends Ghost> extends RenderLayer<T, GhostModel<T>> {

    private static final RenderType RENDER_TYPE = ApocalypseRenderTypes.entityCutoutNoCullBlend(Apocalypse.resourceLoc("textures/entity/ghost/ghost.png"), RenderStateShard.TransparencyStateShard.ADDITIVE_TRANSPARENCY);

    public GhostFrozenLayer(RenderLayerParent<T, GhostModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T ghost, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (ghost.isFrozen()) {
            VertexConsumer vertexConsumer = buffer.getBuffer(RENDER_TYPE);
            float colorShift = ((float)(ghost.tickCount % 25) + partialTicks) / 25.0F;;
            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY, colorShift, 1.0F, colorShift, 1.0F);
        }
    }
}
