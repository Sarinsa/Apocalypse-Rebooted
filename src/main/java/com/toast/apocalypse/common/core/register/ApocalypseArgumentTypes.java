package com.toast.apocalypse.common.core.register;

import com.mojang.brigadier.arguments.ArgumentType;
import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.command.argument.DifficultyArgument;
import com.toast.apocalypse.common.command.argument.MaxDifficultyArgument;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public class ApocalypseArgumentTypes {
    
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> REGISTRY = DeferredRegister.create( ForgeRegistries.COMMAND_ARGUMENT_TYPES, Apocalypse.MOD_ID );
    
    static {
        register( ApocalypseObjects.CmdArguments.DIFFICULTY,
                () -> ArgumentTypeInfos.registerByClass( DifficultyArgument.class,
                        SingletonArgumentInfo.contextFree( DifficultyArgument::difficulty ) ) );
        
        register( ApocalypseObjects.CmdArguments.MAX_DIFFICULTY,
                () -> ArgumentTypeInfos.registerByClass( MaxDifficultyArgument.class,
                        SingletonArgumentInfo.contextFree( MaxDifficultyArgument::maxDifficulty ) ) );
    }
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers an argument type to the deferred register. */
    @SuppressWarnings( "SameParameterValue" )
    private static <T extends ArgumentType<?>> void register( RegistryObject<?> regObj, Supplier<ArgumentTypeInfo<T, ?>> supplier ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), supplier );
    }
}
