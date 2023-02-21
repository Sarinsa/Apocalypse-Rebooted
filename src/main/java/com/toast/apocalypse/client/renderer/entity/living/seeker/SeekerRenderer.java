package com.toast.apocalypse.client.renderer.entity.living.seeker;

import com.mojang.blaze3d.vertex.PoseStack;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.Seeker;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SeekerRenderer<T extends Seeker> extends MobRenderer<T, GhastModel<T>> {

    private static final ResourceLocation[] TEXTURES = {
            Apocalypse.resourceLoc("textures/entity/seeker/seeker.png"),
            Apocalypse.resourceLoc("textures/entity/seeker/seeker_fire.png"),
            Apocalypse.resourceLoc("textures/entity/seeker/seeker_alert.png")
    };

    public SeekerRenderer(EntityRendererProvider.Context context) {
        super(context, new GhastModel<>(context.bakeLayer(ModelLayers.GHAST)), 3.0F);
        this.addLayer(new SeekerEyesLayer<>(this));
    }

    @Override
    protected void scale(T destroyer, PoseStack poseStack, float scale) {
        poseStack.scale(5.0F, 5.0F, 5.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(T seeker) {
        if (seeker.isAlerting())
            return TEXTURES[2];

        return seeker.isCharging() ? TEXTURES[1] : TEXTURES[0];
    }
}
