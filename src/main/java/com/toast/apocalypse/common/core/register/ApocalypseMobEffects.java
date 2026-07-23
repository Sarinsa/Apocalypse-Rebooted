package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public final class ApocalypseMobEffects {
    
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create( ForgeRegistries.MOB_EFFECTS, Apocalypse.MOD_ID );
    
    // No registry object yet.
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a mob effect to the deferred register. */
    private static void register( RegistryObject<MobEffect> regObj, Supplier<MobEffect> supplier ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), supplier );
    }
    
    
    private ApocalypseMobEffects() { }
}
