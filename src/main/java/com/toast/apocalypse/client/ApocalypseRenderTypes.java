package com.toast.apocalypse.client;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

// May I also add that the fact that every single RenderState
// is protected, which is a massive pain in the bum to access transform?
public class ApocalypseRenderTypes {

    /**
     * This RenderType is the same as entityCutoutNoCull except
     * with additive transparency. We use this for rendering the ghost entity.
     *
     * @param resourceLocation The ResourceLocation pointing to a texture file.
     */
    public static RenderType entityCutoutNoCullBlend(ResourceLocation resourceLocation) {
        RenderType.State state = RenderType.State.builder()
                .setTextureState(new RenderState.TextureState(resourceLocation, false, false))
                .setTransparencyState(RenderState.ADDITIVE_TRANSPARENCY)
                .setDiffuseLightingState(RenderState.DIFFUSE_LIGHTING)
                .setAlphaState(RenderState.DEFAULT_ALPHA)
                .setCullState(RenderState.NO_CULL)
                .setLightmapState(RenderState.LIGHTMAP)
                .setOverlayState(RenderState.OVERLAY)
                .createCompositeState(true);

        return RenderType.create("apocalypse_entity_cutout_no_cull_blend", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, false, state);
    }
}
