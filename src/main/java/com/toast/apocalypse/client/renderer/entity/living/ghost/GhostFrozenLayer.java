package com.toast.apocalypse.client.renderer.entity.living.ghost;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.toast.apocalypse.client.ApocalypseRenderTypes;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.GhostEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class GhostFrozenLayer <T extends GhostEntity> extends LayerRenderer<T, GhostModel<T>> {

    private static final RenderType RENDER_TYPE = ApocalypseRenderTypes.entityCutoutNoCullBlend(Apocalypse.resourceLoc("textures/entity/ghost/ghost.png"), ApocalypseRenderTypes.GHOST_ALPHA);

    public GhostFrozenLayer(IEntityRenderer<T, GhostModel<T>> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T ghost, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (ghost.isFrozen()) {
            IVertexBuilder vertexBuilder = buffer.getBuffer(RENDER_TYPE);
            float colorShift = ((float)(ghost.tickCount % 25) + partialTicks) / 25.0F;;
            this.getParentModel().renderToBuffer(matrixStack, vertexBuilder, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY, colorShift, 1.0F, colorShift, 1.0F);
        }
    }
}
