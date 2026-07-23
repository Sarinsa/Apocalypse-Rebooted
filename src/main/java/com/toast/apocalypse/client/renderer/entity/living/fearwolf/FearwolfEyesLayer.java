package com.toast.apocalypse.client.renderer.entity.living.fearwolf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.Fearwolf;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class FearwolfEyesLayer<T extends Fearwolf> extends RenderLayer<T, FearwolfModel<T>> {
    
    private static final RenderType EYES = RenderType.entityCutout( Apocalypse.rl( "textures/entity/fearwolf/fearwolf_eyes.png" ) );
    
    public FearwolfEyesLayer( RenderLayerParent<T, FearwolfModel<T>> parent ) {
        super( parent );
    }
    
    
    /** Renders this render layer. */
    @Override
    public void render( PoseStack poseStack, MultiBufferSource buffer, int packedLight, T fearwolf, float limbSwing, float limbSwingAmount,
                        float partialTicks, float ageInTicks, float netHeadYaw, float headPitch ) {
        final VertexConsumer vertexConsumer = buffer.getBuffer( EYES );
        getParentModel().renderToBuffer( poseStack, vertexConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F );
    }
}