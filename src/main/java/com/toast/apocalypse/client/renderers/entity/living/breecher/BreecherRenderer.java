package com.toast.apocalypse.client.renderers.entity.living.breecher;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.ResourceLocation;

public class BreecherRenderer extends CreeperRenderer {

    private static final ResourceLocation TEXTURE = Apocalypse.resourceLoc("textures/entity/breecher/breecher.png");

    public BreecherRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
    }

    @Override
    public ResourceLocation getTextureLocation(CreeperEntity breecher) {
        return TEXTURE;
    }
}
