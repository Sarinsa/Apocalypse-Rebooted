package com.toast.apocalypse.client.renderers.entity.living.ghost;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.GhostEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;

/** Renders the solid, fullbright eyes of the ghost */
public class GhostEyesLayer<T extends GhostEntity> extends LayerRenderer<T, GhostModel<T>> {

    private static final RenderType EYES = RenderType.entityCutout(Apocalypse.resourceLoc("textures/entity/ghost/ghost_eyes.png"));

    public GhostEyesLayer(IEntityRenderer<T, GhostModel<T>> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T ghost, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        IVertexBuilder vertexBuilder = buffer.getBuffer(EYES);
        this.getParentModel().renderToBuffer(matrixStack, vertexBuilder, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
