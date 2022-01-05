package com.toast.apocalypse.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;

/**
 * Mostly a copy-paste of {@link net.minecraft.client.particle.CloudParticle}
 */
public class LunarDespawnSmokeParticle extends SpriteTexturedParticle {

    private final IAnimatedSprite sprites;

    public LunarDespawnSmokeParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, IAnimatedSprite animatedSprite) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sprites = animatedSprite;
        this.xd *= 0.1F;
        this.yd *= 0.1F;
        this.zd *= 0.1F;
        this.xd += xSpeed;
        this.yd += ySpeed;
        this.zd += zSpeed;
        float f1 = 1.0F - (float)(Math.random() * (double)0.3F);
        this.rCol = f1;
        this.gCol = f1;
        this.bCol = f1;
        this.quadSize *= 2.0F;
        int i = (int)(8.0D / (Math.random() * 0.8D + 0.3D));
        this.lifetime = (int)Math.max((float)i * 2.5F, 1.0F);
        this.hasPhysics = false;
        this.setSpriteFromAge(animatedSprite);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public float getQuadSize(float partialTick) {
        return this.quadSize * MathHelper.clamp(((float)this.age + partialTick) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        else {
            this.setSpriteFromAge(this.sprites);
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.96F;
            this.yd *= 0.96F;
            this.zd *= 0.96F;
            PlayerEntity player = this.level.getNearestPlayer(this.x, this.y, this.z, 2.0D, false);

            if (player != null) {
                double playerY = player.getY();
                if (this.y > playerY) {
                    this.y += (playerY - this.y) * 0.2D;
                    this.yd += (player.getDeltaMovement().y - this.yd) * 0.2D;
                    this.setPos(this.x, this.y, this.z);
                }
            }

            if (this.onGround) {
                this.xd *= 0.7F;
                this.zd *= 0.7F;
            }
        }
    }

    public static class Factory implements IParticleFactory<BasicParticleType> {

        private final IAnimatedSprite sprites;

        public Factory(IAnimatedSprite animatedSprite) {
            this.sprites = animatedSprite;
        }

        public Particle createParticle(BasicParticleType particleType, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new LunarDespawnSmokeParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
