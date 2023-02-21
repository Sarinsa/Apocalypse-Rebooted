package com.toast.apocalypse.client.renderer.entity.living.destroyer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.Destroyer;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DestroyerRenderer<T extends Destroyer> extends MobRenderer<T, GhastModel<T>> {

    private static final ResourceLocation[] TEXTURES = {
            Apocalypse.resourceLoc("textures/entity/destroyer/destroyer.png"),
            Apocalypse.resourceLoc("textures/entity/destroyer/destroyer_fire.png")
    };

    public DestroyerRenderer(EntityRendererProvider.Context context) {
        super(context, new GhastModel<>(context.bakeLayer(ModelLayers.GHAST)), 3.0F);
        this.addLayer(new DestroyerEyesLayer<>(this));
    }

    @Override
    protected void scale(T destroyer, PoseStack poseStack, float scale) {
        poseStack.scale(5.0F, 5.0F, 5.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(T destroyer) {
        return destroyer.isCharging() ? TEXTURES[1] : TEXTURES[0];
    }
}
