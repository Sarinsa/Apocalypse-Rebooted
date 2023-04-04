package com.toast.apocalypse.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.toast.apocalypse.client.renderer.model.armor.BucketHelmetModel;
import com.toast.apocalypse.client.renderer.weather.AcidRainRenderHelper;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class ClientUtil {

    private static final ResourceLocation BUCKET_HELM_OVERLAY_TEXTURE = Apocalypse.resourceLoc("textures/misc/bucket_blur.png");
    public static BucketHelmetModel BUCKET_HELMET_MODEL;

    /** Gets updated via packet */
    public static int OVERWORLD_MOON_PHASE = 0;
    // TODO - One day... one day..
    public static int[] UNLOCKED_INDEXES = new int[]{};

    public static final AcidRainRenderHelper ACID_RAIN_TICKER = new AcidRainRenderHelper();


    public static void onAddLayer(EntityRenderersEvent.AddLayers event) {
        BUCKET_HELMET_MODEL = new BucketHelmetModel(event.getEntityModels().bakeLayer(ModelLayers.PLAYER), event.getEntityModels().bakeLayer(ApocalypseModelLayers.BUCKET_HELMET));
    }

    public static void renderBucketHelmOverlay(int screenWidth, int screenHeight) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BUCKET_HELM_OVERLAY_TEXTURE);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(0.0D, screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
        bufferbuilder.vertex(screenWidth, screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
        tesselator.end();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
