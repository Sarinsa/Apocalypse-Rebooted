package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ApocalypseMobEffects {
    
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create( ForgeRegistries.MOB_EFFECTS, Apocalypse.MOD_ID );
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    private static <T extends MobEffect> RegistryObject<T> register( String name, Supplier<T> effectSupplier ) {
        return REGISTRY.register( name, effectSupplier );
    }
}
