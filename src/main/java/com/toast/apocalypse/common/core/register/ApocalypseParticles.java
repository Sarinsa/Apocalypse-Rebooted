package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.client.particle.LunarDespawnSmokeParticle;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ApocalypseParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Apocalypse.MODID);


    public static final RegistryObject<SimpleParticleType> LUNAR_DESPAWN_SMOKE = registerSimple("lunar_despawn_smoke", true);


    private static RegistryObject<SimpleParticleType> registerSimple(String name, boolean overrideLimiter) {
        return PARTICLES.register(name, () -> new SimpleParticleType(overrideLimiter));
    }
}
