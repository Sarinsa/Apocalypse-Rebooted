package com.toast.apocalypse.client.renderers.entity.living.seeker;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.SeekerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.GhastModel;
import net.minecraft.util.ResourceLocation;

public class SeekerRenderer<T extends SeekerEntity> extends MobRenderer<T, GhastModel<T>> {

    private static final ResourceLocation[] TEXTURES = {
            Apocalypse.resourceLoc("textures/entity/seeker/seeker.png"),
            Apocalypse.resourceLoc("textures/entity/seeker/seeker_shooting.png"),
            Apocalypse.resourceLoc("textures/entity/seeker/seeker_alert.png")
    };

    public SeekerRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new GhastModel<>(), 3.0F);
    }

    @Override
    protected void scale(T destroyer, MatrixStack matrixStack, float scale) {
        matrixStack.scale(5.0F, 5.0F, 5.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(T seeker) {
        if (seeker.isAlerting())
            return TEXTURES[2];

        return seeker.isCharging() ? TEXTURES[1] : TEXTURES[0];
    }
}
