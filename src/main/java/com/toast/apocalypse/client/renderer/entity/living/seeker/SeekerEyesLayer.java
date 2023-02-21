package com.toast.apocalypse.client.renderer.entity.living.seeker;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.Seeker;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

/** Renders the seeker's fullbright eyes */
public class SeekerEyesLayer <T extends Seeker> extends RenderLayer<T, GhastModel<T>> {

    private static final RenderType EYES = RenderType.entityCutout(Apocalypse.resourceLoc("textures/entity/seeker/seeker_eyes.png"));
    private static final RenderType EYES_FIRE = RenderType.entityCutout(Apocalypse.resourceLoc("textures/entity/seeker/seeker_eyes_fire.png"));
    private static final RenderType EYES_ALERT = RenderType.entityCutout(Apocalypse.resourceLoc("textures/entity/seeker/seeker_eyes_alert.png"));


    public SeekerEyesLayer(RenderLayerParent<T, GhastModel<T>> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T seeker, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        VertexConsumer vertexConsumer;

        if (seeker.isAlerting()) {
            vertexConsumer = buffer.getBuffer(EYES_ALERT);
        }
        else {
            vertexConsumer = buffer.getBuffer(seeker.isCharging() ? EYES_FIRE : EYES);
        }
        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}