package com.toast.apocalypse.client.renderer.weather;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.client.IWeatherParticleRenderHandler;

import java.util.Random;

public class AcidRainParticleRenderHandler implements IWeatherParticleRenderHandler {

    private int rainSoundTime;


    public AcidRainParticleRenderHandler() {

    }

    @Override
    public void render(int ticks, ClientWorld world, Minecraft mc, ActiveRenderInfo renderInfo) {
        float rainLevel = world.getRainLevel(1.0F) / (Minecraft.useFancyGraphics() ? 1.0F : 2.0F);

        if (!(rainLevel <= 0.0F)) {
            Random random = new Random((long)ticks * 312987231L);
            BlockPos renderInfoPos = new BlockPos(renderInfo.getPosition());
            BlockPos soundPos = null;
            int particleCount = (int)(100.0F * rainLevel * rainLevel) / 3;

            for(int i = 0; i < particleCount; ++i) {
                if (mc.options.particles == ParticleStatus.MINIMAL) {
                    break;
                }

                int offsetX = random.nextInt(26) - 15;
                int offsetZ = random.nextInt(26) - 15;
                BlockPos groundPos = world.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, renderInfoPos.offset(offsetX, 0, offsetZ)).below();
                Biome biome = world.getBiome(groundPos);

                if (random.nextInt(5) > 0)
                    continue;

                if (groundPos.getY() > 0 && groundPos.getY() <= renderInfoPos.getY() + 10 && groundPos.getY() >= renderInfoPos.getY() - 10 && biome.getPrecipitation() == Biome.RainType.RAIN && biome.getTemperature(groundPos) >= 0.15F) {
                    soundPos = groundPos;

                    double xOffset = random.nextDouble();
                    double zOffset = random.nextDouble();
                    BlockState state = world.getBlockState(groundPos);
                    FluidState fluidState = world.getFluidState(groundPos);
                    VoxelShape collisionShape = state.getCollisionShape(world, groundPos);

                    double d2 = collisionShape.max(Direction.Axis.Y, xOffset, zOffset);
                    double d3 = fluidState.getHeight(world, groundPos);
                    double yOffset = Math.max(d2, d3);

                    IParticleData particleType = fluidState.is(FluidTags.WATER) ? ParticleTypes.RAIN : ParticleTypes.SMOKE;
                    world.addParticle(particleType, (double)groundPos.getX() + xOffset, (double)groundPos.getY() + yOffset, (double)groundPos.getZ() + zOffset, 0.0D, 0.0D, 0.0D);
                }
            }

            if (soundPos != null && random.nextInt(3) < rainSoundTime++) {
                rainSoundTime = 0;
                if (soundPos.getY() > renderInfoPos.getY() + 1 && world.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, renderInfoPos).getY() > MathHelper.floor((float)renderInfoPos.getY())) {
                    world.playLocalSound(soundPos, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F, 0.5F, false);
                }
                else {
                    world.playLocalSound(soundPos, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F, false);
                }
            }
        }
    }
}
