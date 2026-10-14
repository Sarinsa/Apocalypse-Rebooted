package com.toast.apocalypse.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class ApocalypseRenderTypes {
    
    
    /**
     * This render type functions similarly to {@link RenderType#entityCutoutNoCull(ResourceLocation)},
     * except a transparency state can be supplied. We use this for rendering the ghost.
     *
     * @param texture A resource location pointing to a texture file.
     */
    public static RenderType entityCutoutNoCullBlend( ResourceLocation texture, RenderStateShard.TransparencyStateShard transparencyState ) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setTextureState( new RenderStateShard.TextureStateShard( texture, false, false ) )
                .setTransparencyState( transparencyState )
                .setLightmapState( RenderStateShard.LightmapStateShard.LIGHTMAP )
                .setOverlayState( RenderStateShard.OverlayStateShard.OVERLAY )
                .setShaderState( RenderStateShard.ShaderStateShard.RENDERTYPE_ENTITY_ALPHA_SHADER )
                .createCompositeState( true );
        return RenderType.create( "apocalypse_entity_cutout_no_cull_blend", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, state );
    }
    
    // No instantiation
    private ApocalypseRenderTypes() { }
}
