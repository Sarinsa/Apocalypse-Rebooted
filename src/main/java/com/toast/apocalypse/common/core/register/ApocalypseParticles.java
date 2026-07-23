package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public final class ApocalypseParticles {
    
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.PARTICLE_TYPES, Apocalypse.MOD_ID );
    
    static {
        registerSimple( ApocalypseObjects.ParticleTypes.LUNAR_DESPAWN_SMOKE, true );
    }
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /**
     * Registers a simple particle type to the deferred register.
     *
     * @param overrideLimiter If true, particles spawned from this type will always be visible.
     */
    @SuppressWarnings( "SameParameterValue" )
    private static void registerSimple( RegistryObject<SimpleParticleType> regObj, boolean overrideLimiter ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), () -> new SimpleParticleType( overrideLimiter ) );
    }
    
    
    private ApocalypseParticles() { }
}
