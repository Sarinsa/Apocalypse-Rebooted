package com.toast.apocalypse.common.register;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ApocalypseParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Apocalypse.MODID);


    public static final RegistryObject<BasicParticleType> LUNAR_DESPAWN_SMOKE = registerSimple("lunar_despawn_smoke", true);


    private static RegistryObject<BasicParticleType> registerSimple(String name, boolean overrideLimiter) {
        return PARTICLES.register(name, () -> new BasicParticleType(overrideLimiter));
    }
}
