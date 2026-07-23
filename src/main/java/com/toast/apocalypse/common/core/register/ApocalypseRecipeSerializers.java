package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.recipe.TrapRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public final class ApocalypseRecipeSerializers {
    
    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.RECIPE_SERIALIZERS, Apocalypse.MOD_ID );
    
    static {
        register( ApocalypseObjects.RecipeSerializers.TRAP_ASSEMBLING, TrapRecipe.Serializer::new );
    }
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a recipe serializer to the deferred register. */
    public static <T extends Recipe<?>> void register( RegistryObject<RecipeSerializer<?>> regObj, Supplier<RecipeSerializer<T>> supplier ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), supplier );
    }
    
    
    private ApocalypseRecipeSerializers() { }
}
