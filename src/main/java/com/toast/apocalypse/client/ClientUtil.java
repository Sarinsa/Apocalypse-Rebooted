package com.toast.apocalypse.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.toast.apocalypse.client.renderer.model.armor.BucketHelmetModel;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Contains various static fields used on the clients and other render-related utility methods. */
public final class ClientUtil {
    
    private static final ResourceLocation BUCKET_HELM_OVERLAY_TEXTURE = Apocalypse.rl( "textures/misc/bucket_blur.png" );
    
    @Nullable
    private static BucketHelmetModel bucketHelmetModel;
    
    private static int acidRainColor;
    
    private static boolean isRainingAcid;
    private static boolean acidSnowEnabled;
    
    
    /** Creates the bucket helmet model. Called from {@link ClientRegister#onAddLayer(EntityRenderersEvent.AddLayers)}. */
    public static void createBucketHelmetModel( EntityRenderersEvent.AddLayers event ) {
        bucketHelmetModel = new BucketHelmetModel(
                event.getEntityModels().bakeLayer( ModelLayers.PLAYER ),
                event.getEntityModels().bakeLayer( ApocalypseModelLayers.BUCKET_HELMET )
        );
    }
    
    /** @return The bucket helmet model used by the bucket helmet item. */
    @Nullable
    public static BucketHelmetModel getBucketHelmetModel() { return bucketHelmetModel; }
    
    /** Sets the acid rain color to use when rendering acid rain. */
    public static void setAcidRainColor( int color ) {
        acidRainColor = color;
    }
    
    /** @return The current acid rain color int. */
    public static int getAcidRainColor() { return acidRainColor; }
    
    /** Toggles acid rain rendering on or off. */
    public static void setIsRainingAcid( boolean isRainingAcid ) { ClientUtil.isRainingAcid = isRainingAcid; }
    
    /** @return True if it is currently raining acid on the client, and returns false otherwise. */
    @SuppressWarnings( "BooleanMethodIsAlwaysInverted" )
    public static boolean isRainingAcid() { return isRainingAcid; }
    
    /** Toggles acid snow on or off. */
    public static void setAcidSnowEnabled( boolean enabled ) { acidSnowEnabled = enabled; }
    
    /** @return True if acid snow is enabled, and returns false otherwise. */
    public static boolean acidSnowEnabled() { return acidSnowEnabled; }
    
    
    /**
     * Renders the bucket in-game GUI bucket helmet overlay when a player has a bucket helmet equipped on their head.
     *
     * @see com.toast.apocalypse.common.item.BucketHelmetItem#initializeClient(Consumer)
     */
    public static void renderBucketHelmOverlay( int screenWidth, int screenHeight ) {
        // Set up render state
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask( false );
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader( GameRenderer::getPositionTexShader );
        RenderSystem.setShaderColor( 1.0F, 1.0F, 1.0F, 1.0F );
        RenderSystem.setShaderTexture( 0, BUCKET_HELM_OVERLAY_TEXTURE );
        // Draw stuff
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin( VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX );
        bufferBuilder.vertex( 0.0D, screenHeight, -90.0D ).uv( 0.0F, 1.0F ).endVertex();
        bufferBuilder.vertex( screenWidth, screenHeight, -90.0D ).uv( 1.0F, 1.0F ).endVertex();
        bufferBuilder.vertex( screenWidth, 0.0D, -90.0D ).uv( 1.0F, 0.0F ).endVertex();
        bufferBuilder.vertex( 0.0D, 0.0D, -90.0D ).uv( 0.0F, 0.0F ).endVertex();
        Tesselator.getInstance().end();
        // Reset render state
        RenderSystem.depthMask( true );
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor( 1.0F, 1.0F, 1.0F, 1.0F );
    }
    
    // Utility class
    private ClientUtil() { }
}
