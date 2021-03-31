package com.toast.apocalypse.client.renderers.entity.ghost;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.GhostEntity;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GhostRenderer<T extends GhostEntity> extends LivingRenderer<T, GhostModel<T>> {

    public static final ResourceLocation GHOST_TEXTURE = Apocalypse.resourceLoc("textures/entity/ghost/ghost.png");
    public static final ResourceLocation GHOST_TEXTURE_SOLID = Apocalypse.resourceLoc("textures/entity/ghost/ghost_base.png");

    public GhostRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new GhostModel<>(), 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(T ghost) {
        return GHOST_TEXTURE;
    }
}
