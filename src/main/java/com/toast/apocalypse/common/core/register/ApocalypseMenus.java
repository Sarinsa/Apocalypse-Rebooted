package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.menus.DynamicTrapMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public final class ApocalypseMenus {
    
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.MENU_TYPES, Apocalypse.MOD_ID );
    
    static {
        register( ApocalypseObjects.MenuTypes.DYNAMIC_TRAP, DynamicTrapMenu::new );
    }
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a menu type to the deferred register. */
    private static <T extends AbstractContainerMenu> void register( RegistryObject<MenuType<?>> regObj, MenuType.MenuSupplier<T> supplier ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), () -> new MenuType<>( supplier, FeatureFlags.VANILLA_SET ) );
    }
    
    
    private ApocalypseMenus() { }
}
