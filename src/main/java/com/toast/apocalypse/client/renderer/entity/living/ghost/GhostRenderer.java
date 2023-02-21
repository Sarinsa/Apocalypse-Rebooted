package com.toast.apocalypse.client.renderer.entity.living.ghost;

import com.toast.apocalypse.client.ApocalypseModelLayers;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.Ghost;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class GhostRenderer<T extends Ghost> extends MobRenderer<T, GhostModel<T>> {

    protected static final ResourceLocation GHOST_TEXTURE = Apocalypse.resourceLoc("textures/entity/ghost/ghost.png");

    public GhostRenderer(EntityRendererProvider.Context context) {
        super(context, new GhostModel<>(context.bakeLayer(ApocalypseModelLayers.GHOST)), 0.0F);
        this.addLayer(new GhostEyesLayer<>(this));
        this.addLayer(new GhostFrozenLayer<>(this));
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(T ghost) {
        return GHOST_TEXTURE;
    }
}
