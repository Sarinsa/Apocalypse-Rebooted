package com.toast.apocalypse.common.core.register;

import com.mojang.serialization.Codec;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.loot_modifier.SimpleAddLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ApocalypseLootMods {
    
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTRY = DeferredRegister.create( ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Apocalypse.MOD_ID );
    
    
    public static final RegistryObject<Codec<SimpleAddLootModifier>> SIMPLE_ADD_LOOT_MOD = register( "simple_add_loot_mod", SimpleAddLootModifier.CODEC );
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    private static <T extends Codec<? extends IGlobalLootModifier>> RegistryObject<T> register( String name, Supplier<T> supplier ) {
        return REGISTRY.register( name, supplier );
    }
}
