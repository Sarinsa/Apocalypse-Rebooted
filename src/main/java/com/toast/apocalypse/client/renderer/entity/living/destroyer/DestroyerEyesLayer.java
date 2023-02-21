package com.toast.apocalypse.client.renderer.entity.living.destroyer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.toast.apocalypse.client.renderer.entity.living.ghost.GhostModel;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.Destroyer;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

/**
 * Renders the destroyer's fullbright eyes
 */
public class DestroyerEyesLayer<T extends Destroyer> extends RenderLayer<T, GhastModel<T>> {

    private static final RenderType EYES = RenderType.entityCutout(Apocalypse.resourceLoc("textures/entity/destroyer/destroyer_eyes.png"));
    private static final RenderType EYES_FIRE = RenderType.entityCutout(Apocalypse.resourceLoc("textures/entity/destroyer/destroyer_eyes_fire.png"));


    public DestroyerEyesLayer(RenderLayerParent<T, GhastModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T destroyer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        VertexConsumer vertexConsumer = buffer.getBuffer(destroyer.isCharging() ? EYES_FIRE : EYES);
        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
