package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.loot_modifier.SimpleAddLootModifier;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ApocalypseLootMods {

    public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, Apocalypse.MODID);


    public static final RegistryObject<SimpleAddLootModifier.Serializer> SIMPLE_ADD_LOOT_MOD = register("simple_add_loot_mod", SimpleAddLootModifier.Serializer::new);


    private static <T extends GlobalLootModifierSerializer<?>> RegistryObject<T> register(String name, Supplier<T> supplier) {
        return LOOT_MODIFIERS.register(name, supplier);
    }
}
