package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.api.AbstractTrap;
import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.trap_actions.EquipmentBreakTrap;
import com.toast.apocalypse.common.trap_actions.GhostFreezeTrap;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public class ApocalypseTrapTypes {
    
    public static final DeferredRegister<AbstractTrap> REGISTRY = DeferredRegister.create( ApocalypseObjects.TrapTypes.REGISTRY_KEY, Apocalypse.MOD_ID );
    
    
    static {
        register( ApocalypseObjects.TrapTypes.GHOST_FREEZE, GhostFreezeTrap::new );
        register( ApocalypseObjects.TrapTypes.EQUIPMENT_BREAK, EquipmentBreakTrap::new );
    }
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a trap type to the deferred register. */
    @SuppressWarnings( "SameParameterValue" )
    private static void register( RegistryObject<AbstractTrap> regObj, Supplier<AbstractTrap> supplier ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), supplier );
    }
    
    /**
     * Called when new registries can be created.
     * <br><br>
     * Added as a listener method in {@link Apocalypse#Apocalypse(FMLJavaModLoadingContext)}.
     */
    public static void onRegistryCreate( NewRegistryEvent event ) {
        RegistryBuilder<AbstractTrap> builder = new RegistryBuilder<>();
        builder.setName( ApocalypseObjects.TrapTypes.REGISTRY_KEY.location() );
        ApocalypseObjects.TRAP_ACTIONS_REGISTRY = event.create( builder );
    }
}
