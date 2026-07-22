package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ApocalypseParticles {
    
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.PARTICLE_TYPES, Apocalypse.MOD_ID );
    
    
    public static final RegistryObject<SimpleParticleType> LUNAR_DESPAWN_SMOKE = registerSimple( "lunar_despawn_smoke", true );
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    private static RegistryObject<SimpleParticleType> registerSimple( String name, boolean overrideLimiter ) {
        return REGISTRY.register( name, () -> new SimpleParticleType( overrideLimiter ) );
    }
}
