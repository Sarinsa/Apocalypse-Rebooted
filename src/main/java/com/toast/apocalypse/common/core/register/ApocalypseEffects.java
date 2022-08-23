package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.effect.HeavyEffect;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ApocalypseEffects {

    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, Apocalypse.MODID);

    public static final RegistryObject<Effect> HEAVY = register("heavy", () -> new HeavyEffect(EffectType.HARMFUL, 0xF67B2CC));

    private static <T extends Effect> RegistryObject<T> register(String name, Supplier<T> effectSupplier) {
        return EFFECTS.register(name, effectSupplier);
    }
}
