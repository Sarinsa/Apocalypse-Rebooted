package com.toast.apocalypse.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class LunarDespawnSmokeParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    public LunarDespawnSmokeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet animatedSprite) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
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
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public float getQuadSize(float partialTick) {
        return this.quadSize * Mth.clamp(((float)this.age + partialTick) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;

        if (age++ >= lifetime) {
            remove();
        }
        else {
            setSpriteFromAge(sprites);
            move(xd, yd, zd);
            xd *= 0.96F;
            yd *= 0.96F;
            zd *= 0.96F;
            Player player = this.level.getNearestPlayer(this.x, this.y, this.z, 2.0D, false);

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

    public static class Factory implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Factory(SpriteSet animatedSprite) {
            this.sprites = animatedSprite;
        }

        @Override
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new LunarDespawnSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
