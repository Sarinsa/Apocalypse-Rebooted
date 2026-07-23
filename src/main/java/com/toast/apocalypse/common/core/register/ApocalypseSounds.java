package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public final class ApocalypseSounds {
    
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create( ForgeRegistries.SOUND_EVENTS, Apocalypse.MOD_ID );
    
    static {
        register( ApocalypseObjects.SoundEvents.LUNAR_ARMOR_REACT );
        register( ApocalypseObjects.SoundEvents.ARMOR_EQUIP_LUNAR );
        
        register( ApocalypseObjects.SoundEvents.DYNAMIC_TRAP_ACTIVATE );
        
        register( ApocalypseObjects.SoundEvents.MONSTER_HOOK_RETRIEVE );
        
        register( ApocalypseObjects.SoundEvents.DESTROYER_FIREBALL_DEFLECT );
        
        register( ApocalypseObjects.SoundEvents.SEEKER_FIREBALL_IGNITE );
        
        register( ApocalypseObjects.SoundEvents.BREECHER_HURT );
        register( ApocalypseObjects.SoundEvents.BREECHER_DEATH );
        
        register( ApocalypseObjects.SoundEvents.DESTROYER_WARN );
        register( ApocalypseObjects.SoundEvents.DESTROYER_SHOOT );
        register( ApocalypseObjects.SoundEvents.DESTROYER_HURT );
        register( ApocalypseObjects.SoundEvents.DESTROYER_DEATH );
        
        register( ApocalypseObjects.SoundEvents.SEEKER_WARN );
        register( ApocalypseObjects.SoundEvents.SEEKER_SHOOT );
        register( ApocalypseObjects.SoundEvents.SEEKER_ALERT_MOBS );
        register( ApocalypseObjects.SoundEvents.SEEKER_HURT );
        register( ApocalypseObjects.SoundEvents.SEEKER_DEATH );
        
        register( ApocalypseObjects.SoundEvents.GHOST_IDLE );
        register( ApocalypseObjects.SoundEvents.GHOST_HURT );
        register( ApocalypseObjects.SoundEvents.GHOST_DEATH );
        register( ApocalypseObjects.SoundEvents.GHOST_FREEZE );
        
        register( ApocalypseObjects.SoundEvents.GRUMP_HURT );
        register( ApocalypseObjects.SoundEvents.GRUMP_DEATH );
        register( ApocalypseObjects.SoundEvents.GRUMP_RAGE );
        register( ApocalypseObjects.SoundEvents.GRUMP_EAT );
        register( ApocalypseObjects.SoundEvents.GRUMP_LAUNCH_HOOK );
        register( ApocalypseObjects.SoundEvents.GRUMP_EQUIP_SADDLE );
        
        register( ApocalypseObjects.SoundEvents.FEARWOLF_STEP );
        register( ApocalypseObjects.SoundEvents.FEARWOLF_IDLE );
        register( ApocalypseObjects.SoundEvents.FEARWOLF_HURT );
        register( ApocalypseObjects.SoundEvents.FEARWOLF_DEATH );
        
        register( ApocalypseObjects.SoundEvents.SHADEFIEND_FLAP );
        register( ApocalypseObjects.SoundEvents.SHADEFIEND_BITE );
        register( ApocalypseObjects.SoundEvents.SHADEFIEND_IDLE );
        register( ApocalypseObjects.SoundEvents.SHADEFIEND_HURT );
        register( ApocalypseObjects.SoundEvents.SHADEFIEND_DEATH );
    }
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a sound event to the deferred register. */
    private static void register( RegistryObject<SoundEvent> regObj ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();
        REGISTRY.register( name, () -> SoundEvent.createVariableRangeEvent( Apocalypse.rl( name ) ) );
    }
    
    
    private ApocalypseSounds() { }
}
