package com.toast.apocalypse.client.renderer.entity.living.ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.Ghost;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class GhostEyesLayer<T extends Ghost> extends RenderLayer<T, GhostModel<T>> {
    
    private static final RenderType EYES = RenderType.entityCutout( Apocalypse.rl( "textures/entity/ghost/ghost_eyes.png" ) );
    
    public GhostEyesLayer( RenderLayerParent<T, GhostModel<T>> parent ) {
        super( parent );
    }
    
    
    /** Renders this render layer. */
    @Override
    public void render( PoseStack poseStack, MultiBufferSource buffer, int packedLight, T ghost, float limbSwing, float limbSwingAmount,
                        float partialTicks, float ageInTicks, float netHeadYaw, float headPitch ) {
        final VertexConsumer vertexConsumer = buffer.getBuffer( EYES );
        getParentModel().renderToBuffer( poseStack, vertexConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F );
    }
}
