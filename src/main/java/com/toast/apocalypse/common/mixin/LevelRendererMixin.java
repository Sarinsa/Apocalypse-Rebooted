package com.toast.apocalypse.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.toast.apocalypse.common.misc.mixin_work.ClientMixinHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements ResourceManagerReloadListener, AutoCloseable {

    @Shadow
    private ClientLevel level;


    public LevelRendererMixin(Minecraft mc, EntityRenderDispatcher entityRD, BlockEntityRenderDispatcher blockEntityRD, RenderBuffers renderBuffers) {}


    @ModifyArg(
            method = "renderSnowAndRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V",
                    ordinal = 0
            )
    )
    public ResourceLocation modifyFirst_renderSnowAndRain(
            ResourceLocation originalTexture,
            @Local(ordinal = 0) Biome.Precipitation precipitation) {
        return ClientMixinHooks.getRenderSnowAndRainTexture(originalTexture, precipitation);
    }

    @ModifyArg(
            method = "renderSnowAndRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V",
                    ordinal = 1
            )
    )
    public ResourceLocation modifySecond_renderSnowAndRain(
            ResourceLocation originalTexture,
            @Local(ordinal = 0) Biome.Precipitation precipitation) {
        return ClientMixinHooks.getRenderSnowAndRainTexture(originalTexture, precipitation);
    }

    @ModifyArg(
            method = "renderSnowAndRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;color(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            ),
            index = 0,
            require = 8
    )
    public float modifyRColor_renderSnowAndRain(float originalR, @Local(ordinal = 0) Biome.Precipitation precipitation) {
        return ClientMixinHooks.getAcidRainColor(originalR, 0, precipitation);
    }

    @ModifyArg(
            method = "renderSnowAndRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;color(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            ),
            index = 1,
            require = 8
    )
    public float modifyGColor_renderSnowAndRain(float originalR, @Local(ordinal = 0) Biome.Precipitation precipitation) {
        return ClientMixinHooks.getAcidRainColor(originalR, 1, precipitation);
    }

    @ModifyArg(
            method = "renderSnowAndRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;color(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            ),
            index = 2,
            require = 8
    )
    public float modifyBColor_renderSnowAndRain(float originalR, @Local(ordinal = 0) Biome.Precipitation precipitation) {
        return ClientMixinHooks.getAcidRainColor(originalR, 2, precipitation);
    }

    @ModifyArg(
            method = "tickRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V")
    )
    public ParticleOptions modify_tickRain(ParticleOptions originalParticle, @Local(ordinal = 0) FluidState fluidState) {
        return ClientMixinHooks.getRainTickParticle(level, originalParticle, fluidState);
    }
}
