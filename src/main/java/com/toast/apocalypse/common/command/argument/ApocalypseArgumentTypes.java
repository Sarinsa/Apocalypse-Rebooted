package com.toast.apocalypse.common.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ApocalypseArgumentTypes {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENTS = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, Apocalypse.MODID);


    public static final RegistryObject<ArgumentTypeInfo<DifficultyArgument, ?>> DIFFICULTY = register("difficulty", () -> ArgumentTypeInfos.registerByClass(DifficultyArgument.class, SingletonArgumentInfo.contextFree(DifficultyArgument::difficulty)));
    public static final RegistryObject<ArgumentTypeInfo<MaxDifficultyArgument, ?>> MAX_DIFFICULTY = register("max_difficulty", () -> ArgumentTypeInfos.registerByClass(MaxDifficultyArgument.class, SingletonArgumentInfo.contextFree(MaxDifficultyArgument::maxDifficulty)));

    private static <T extends ArgumentType<?>> RegistryObject<ArgumentTypeInfo<T, ?>> register(String name, Supplier<ArgumentTypeInfo<T, ?>> supplier) {
        return ARGUMENTS.register(name, supplier);
    }
}
