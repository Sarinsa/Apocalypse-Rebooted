package com.toast.apocalypse.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ApocalypseRenderTypes {


    /**
     * This RenderType is the same as entityCutoutNoCull except
     * it can use whatever transparency state needed. We use this for rendering the ghost.
     *
     * @param resourceLocation The ResourceLocation pointing to a texture file.
     */
    public static RenderType entityCutoutNoCullBlend(ResourceLocation resourceLocation, RenderStateShard.TransparencyStateShard transparencyState) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(transparencyState)
                .setLightmapState(RenderStateShard.LightmapStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OverlayStateShard.OVERLAY)
                .setShaderState(RenderStateShard.ShaderStateShard.RENDERTYPE_ENTITY_ALPHA_SHADER)
                .createCompositeState(true);

        return RenderType.create("apocalypse_entity_cutout_no_cull_blend", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, state);
    }
}
