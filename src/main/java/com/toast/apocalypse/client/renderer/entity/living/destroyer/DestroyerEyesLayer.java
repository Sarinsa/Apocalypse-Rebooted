package com.toast.apocalypse.client.renderer.entity.living.destroyer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.DestroyerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.GhastModel;
import net.minecraft.client.renderer.texture.OverlayTexture;

/** Renders the destroyer's fullbright eyes */
public class DestroyerEyesLayer <T extends DestroyerEntity> extends LayerRenderer<T, GhastModel<T>> {

    private static final RenderType EYES = RenderType.entityCutout(Apocalypse.resourceLoc("textures/entity/destroyer/destroyer_eyes.png"));
    private static final RenderType EYES_FIRE = RenderType.entityCutout(Apocalypse.resourceLoc("textures/entity/destroyer/destroyer_eyes_fire.png"));


    public DestroyerEyesLayer(IEntityRenderer<T, GhastModel<T>> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T destroyer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        IVertexBuilder vertexBuilder = buffer.getBuffer(destroyer.isCharging() ? EYES_FIRE : EYES);
        this.getParentModel().renderToBuffer(matrixStack, vertexBuilder, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
