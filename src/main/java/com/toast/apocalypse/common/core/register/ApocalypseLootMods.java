package com.toast.apocalypse.common.core.register;

import com.mojang.serialization.Codec;
import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.loot_modifier.SimpleAddLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public final class ApocalypseLootMods {
    
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTRY = DeferredRegister.create( ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Apocalypse.MOD_ID );
    
    static {
        register( ApocalypseObjects.LootModSerializers.SIMPLE_ADD_LOOT_MOD, SimpleAddLootModifier.CODEC );
    }
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a global loot modifier serializer to the deferred register. */
    private static <T extends IGlobalLootModifier> void register( RegistryObject<Codec<? extends IGlobalLootModifier>> regObj, Supplier<Codec<T>> supplier ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), supplier );
    }
    
    
    private ApocalypseLootMods() { }
}
