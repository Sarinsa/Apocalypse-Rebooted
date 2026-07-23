package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.recipe.TrapRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public final class ApocalypseRecipeTypes {
    
    public static final DeferredRegister<RecipeType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.RECIPE_TYPES, Apocalypse.MOD_ID );
    
    public static RegistryObject<RecipeType<TrapRecipe>> TRAP_ASSEMBLING = register( ApocalypseObjects.RecipeTypes.TRAP_ASSEMBLING );
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a recipe type to the deferred register. */
    private static <T extends Recipe<?>> RegistryObject<RecipeType<T>> register( RegistryObject<RecipeType<?>> regObj ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();
        return REGISTRY.register( name, () -> new RecipeType<T>() {
            @Override
            public String toString() {
                return name;
            }
        } );
    }
    
    
    private ApocalypseRecipeTypes() { }
}
