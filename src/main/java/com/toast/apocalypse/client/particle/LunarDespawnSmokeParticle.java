package com.toast.apocalypse.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

public class LunarDespawnSmokeParticle extends TextureSheetParticle {
    
    /** The sprite set this particle picks textures from. */
    private final SpriteSet sprites;
    
    
    public LunarDespawnSmokeParticle( ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet animatedSprite ) {
        super( level, x, y, z, 0.0D, 0.0D, 0.0D );
        sprites = animatedSprite;
        xd *= 0.1F;
        yd *= 0.1F;
        zd *= 0.1F;
        xd += xSpeed;
        yd += ySpeed;
        zd += zSpeed;
        float shade = 1.0F - (float) (Math.random() * (double) 0.3F);
        rCol = gCol = bCol = shade;
        quadSize *= 2.0F;
        int i = (int) (8.0D / (Math.random() * 0.8D + 0.3D));
        lifetime = (int) Math.max( (float) i * 2.5F, 1.0F );
        hasPhysics = false;
        setSpriteFromAge( animatedSprite );
    }
    
    /** @return The render type to use for this particle. */
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    
    /** @return The visual size of this particle. */
    @Override
    public float getQuadSize( float partialTick ) {
        return quadSize * Mth.clamp( ((float) age + partialTick) / (float) lifetime * 32.0F, 0.0F, 1.0F );
    }
    
    /** Called each tick to update this particle. */
    @Override
    public void tick() {
        // Update old position
        xo = x;
        yo = y;
        zo = z;
        
        // Tick life span, remove if expired
        if( age++ >= lifetime ) {
            remove();
        }
        // Update particle state
        else {
            setSpriteFromAge( sprites );
            move( xd, yd, zd );
            xd *= 0.96F;
            yd *= 0.96F;
            zd *= 0.96F;
            
            // Slow down particle motion when on ground
            if( onGround ) {
                xd *= 0.7F;
                zd *= 0.7F;
            }
        }
    }
    
    /**
     * This particle's provider.
     *
     * @see com.toast.apocalypse.client.ClientRegister#registerParticles(RegisterParticleProvidersEvent)
     */
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        
        /** The sprite set to pick textures from. */
        private final SpriteSet sprites;
        
        public Factory( SpriteSet spriteSet ) {
            sprites = spriteSet;
        }
        
        /**
         * Creates a new particle.
         *
         * @param type  The particle type.
         * @param level The level the particle is being spawned in.
         * @param x     The X position of the particle.
         * @param y     The Y position of the particle.
         * @param z     The Z position of the particle.
         * @param dX    The X delta movement / velocity of the particle.
         * @param dY    The Y delta movement / velocity of the particle.
         * @param dZ    The X delta movement / velocity of the particle.
         */
        @Override
        public Particle createParticle( SimpleParticleType type, ClientLevel level, double x, double y, double z, double dX, double dY, double dZ ) {
            return new LunarDespawnSmokeParticle( level, x, y, z, dX, dY, dZ, sprites );
        }
    }
}
