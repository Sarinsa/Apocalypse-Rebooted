package com.toast.apocalypse.client.renderer.weather;

import com.mojang.blaze3d.systems.RenderSystem;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.client.IWeatherRenderHandler;

import java.util.Random;

import static net.minecraft.client.renderer.WorldRenderer.getLightColor;

public class AcidRainRenderHandler implements IWeatherRenderHandler {

    private static final ResourceLocation RAIN_LOCATION = Apocalypse.resourceLoc("textures/environment/acid_rain.png");

    public static Vector3f RAIN_COLOR;

    private final float[] rainSizeX = new float[1024];
    private final float[] rainSizeZ = new float[1024];


    public AcidRainRenderHandler() {
        for(int i = 0; i < 32; ++i) {
            for(int j = 0; j < 32; ++j) {
                float f = (float)(j - 16);
                float f1 = (float)(i - 16);
                float f2 = MathHelper.sqrt(f * f + f1 * f1);
                rainSizeX[i << 5 | j] = -f1 / f2;
                rainSizeZ[i << 5 | j] = f / f2;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void render(int ticks, float partialTicks, ClientWorld world, Minecraft mc, LightTexture lightmap, double x, double y, double z) {
        float rainLevel = world.getRainLevel(partialTicks);

        if (!(rainLevel <= 0.0F)) {
            lightmap.turnOnLightLayer();
            int floorX = MathHelper.floor(x);
            int floorY = MathHelper.floor(y);
            int floorZ = MathHelper.floor(z);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuilder();

            RenderSystem.enableAlphaTest();
            RenderSystem.disableCull();
            RenderSystem.normal3f(0.0F, 1.0F, 0.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.enableDepthTest();
            int l = 5;

            if (Minecraft.useFancyGraphics()) {
                l = 10;
            }
            RenderSystem.depthMask(Minecraft.useShaderTransparency());
            int i1 = -1;
            float f1 = (float) ticks + partialTicks;
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            BlockPos.Mutable pos = new BlockPos.Mutable();

            for(int j1 = floorZ - l; j1 <= floorZ + l; ++j1) {
                for(int k1 = floorX - l; k1 <= floorX + l; ++k1) {
                    int l1 = (j1 - floorZ + 16) * 32 + k1 - floorX + 16;
                    double d0 = (double)rainSizeX[l1] * 0.5D;
                    double d1 = (double)rainSizeZ[l1] * 0.5D;
                    pos.set(k1, 0, j1);
                    Biome biome = world.getBiome(pos);

                    if (biome.getPrecipitation() != Biome.RainType.NONE) {
                        int i2 = world.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, pos).getY();
                        int j2 = floorY - l;
                        int k2 = floorY + l;

                        if (j2 < i2) {
                            j2 = i2;
                        }
                        if (k2 < i2) {
                            k2 = i2;
                        }
                        int l2 = Math.max(i2, floorY);

                        if (j2 != k2) {
                            Random random = new Random((long) k1 * k1 * 3121 + k1 * 45238971L ^ (long) j1 * j1 * 418711 + j1 * 13761L);
                            pos.set(k1, j2, j1);

                            if (i1 != 0) {
                                if (i1 >= 0) {
                                    tessellator.end();
                                }
                                i1 = 0;
                                mc.getTextureManager().bind(RAIN_LOCATION);
                                bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE);
                            }
                            int i3 = ticks + k1 * k1 * 3121 + k1 * 45238971 + j1 * j1 * 418711 + j1 * 13761 & 31;
                            float f3 = -((float)i3 + partialTicks) / 32.0F * (3.0F + random.nextFloat());
                            double d2 = (double)((float)k1 + 0.5F) - x;
                            double d4 = (double)((float)j1 + 0.5F) - z;
                            float f4 = MathHelper.sqrt(d2 * d2 + d4 * d4) / (float)l;
                            float alpha = ((1.0F - f4 * f4) * 0.5F + 0.5F) * rainLevel;
                            pos.set(k1, l2, j1);
                            int uv2 = getLightColor(world, pos);

                            bufferbuilder.vertex((double)k1 - x - d0 + 0.5D, (double)k2 - y, (double)j1 - z - d1 + 0.5D)
                                    .uv(0.0F, (float)j2 * 0.25F + f3)
                                    .color(RAIN_COLOR.x(), RAIN_COLOR.y(), RAIN_COLOR.z(), alpha)
                                    .uv2(uv2)
                                    .endVertex();
                            bufferbuilder.vertex((double)k1 - x + d0 + 0.5D, (double)k2 - y, (double)j1 - z + d1 + 0.5D)
                                    .uv(1.0F, (float)j2 * 0.25F + f3)
                                    .color(RAIN_COLOR.x(), RAIN_COLOR.y(), RAIN_COLOR.z(), alpha)
                                    .uv2(uv2)
                                    .endVertex();
                            bufferbuilder.vertex((double)k1 - x + d0 + 0.5D, (double)j2 - y, (double)j1 - z + d1 + 0.5D)
                                    .uv(1.0F, (float)k2 * 0.25F + f3)
                                    .color(RAIN_COLOR.x(), RAIN_COLOR.y(), RAIN_COLOR.z(), alpha)
                                    .uv2(uv2)
                                    .endVertex();
                            bufferbuilder.vertex((double)k1 - x - d0 + 0.5D, (double)j2 - y, (double)j1 - z - d1 + 0.5D)
                                    .uv(0.0F, (float)k2 * 0.25F + f3)
                                    .color(RAIN_COLOR.x(), RAIN_COLOR.y(), RAIN_COLOR.z(), alpha)
                                    .uv2(uv2).endVertex();

                        }
                    }
                }
            }
            if (i1 >= 0) {
                tessellator.end();
            }
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.disableAlphaTest();
            lightmap.turnOffLightLayer();
        }
    }
}
