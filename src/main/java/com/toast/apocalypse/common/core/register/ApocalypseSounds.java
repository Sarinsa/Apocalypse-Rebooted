package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ApocalypseSounds {
    
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create( ForgeRegistries.SOUND_EVENTS, Apocalypse.MODID );
    
    
    public static final RegistryObject<SoundEvent> LUNAR_ARMOR_REACT = register( "item.lunar_armor.react" );
    public static final RegistryObject<SoundEvent> ARMOR_EQUIP_LUNAR = register( "item.armor.equip_lunar" );
    
    public static final RegistryObject<SoundEvent> DYNAMIC_TRAP_ACTIVATE = register( "block.dynamic_trap.activate" );
    
    public static final RegistryObject<SoundEvent> MONSTER_HOOK_RETRIEVE = register( "entity.monster_fish_hook.retrieve" );
    
    public static final RegistryObject<SoundEvent> DESTROYER_FIREBALL_DEFLECT = register( "entity.destroyer_fireball.deflect" );
    
    public static final RegistryObject<SoundEvent> SEEKER_FIREBALL_IGNITE = register( "entity.seeker_fireball.ignite" );
    
    public static final RegistryObject<SoundEvent> BREECHER_HURT = register( "entity.breecher.hurt" );
    public static final RegistryObject<SoundEvent> BREECHER_DEATH = register( "entity.breecher.death" );
    
    public static final RegistryObject<SoundEvent> DESTROYER_WARN = register( "entity.destroyer.warn" );
    public static final RegistryObject<SoundEvent> DESTROYER_SHOOT = register( "entity.destroyer.shoot" );
    public static final RegistryObject<SoundEvent> DESTROYER_HURT = register( "entity.destroyer.hurt" );
    public static final RegistryObject<SoundEvent> DESTROYER_DEATH = register( "entity.destroyer.death" );
    
    public static final RegistryObject<SoundEvent> SEEKER_WARN = register( "entity.seeker.warn" );
    public static final RegistryObject<SoundEvent> SEEKER_SHOOT = register( "entity.seeker.shoot" );
    public static final RegistryObject<SoundEvent> SEEKER_ALERT_MOBS = register( "entity.seeker.alert_mobs" );
    public static final RegistryObject<SoundEvent> SEEKER_HURT = register( "entity.seeker.hurt" );
    public static final RegistryObject<SoundEvent> SEEKER_DEATH = register( "entity.seeker.death" );
    
    public static final RegistryObject<SoundEvent> GHOST_IDLE = register( "entity.ghost.idle" );
    public static final RegistryObject<SoundEvent> GHOST_HURT = register( "entity.ghost.hurt" );
    public static final RegistryObject<SoundEvent> GHOST_DEATH = register( "entity.ghost.death" );
    public static final RegistryObject<SoundEvent> GHOST_FREEZE = register( "entity.ghost.freeze" );
    
    public static final RegistryObject<SoundEvent> GRUMP_HURT = register( "entity.grump.hurt" );
    public static final RegistryObject<SoundEvent> GRUMP_DEATH = register( "entity.grump.death" );
    public static final RegistryObject<SoundEvent> GRUMP_RAGE = register( "entity.grump.rage" );
    public static final RegistryObject<SoundEvent> GRUMP_EAT = register( "entity.grump.eat" );
    public static final RegistryObject<SoundEvent> GRUMP_LAUNCH_HOOK = register( "entity.grump.launch_hook" );
    public static final RegistryObject<SoundEvent> GRUMP_EQUIP_SADDLE = register( "entity.grump.equip_saddle" );
    
    public static final RegistryObject<SoundEvent> FEARWOLF_STEP = register( "entity.fearwolf.step" );
    public static final RegistryObject<SoundEvent> FEARWOLF_IDLE = register( "entity.fearwolf.idle" );
    public static final RegistryObject<SoundEvent> FEARWOLF_HURT = register( "entity.fearwolf.hurt" );
    public static final RegistryObject<SoundEvent> FEARWOLF_DEATH = register( "entity.fearwolf.death" );
    
    public static final RegistryObject<SoundEvent> SHADEFIEND_FLAP = register( "entity.shadefiend.flap" );
    public static final RegistryObject<SoundEvent> SHADEFIEND_BITE = register( "entity.shadefiend.bite" );
    public static final RegistryObject<SoundEvent> SHADEFIEND_IDLE = register( "entity.shadefiend.idle" );
    public static final RegistryObject<SoundEvent> SHADEFIEND_HURT = register( "entity.shadefiend.hurt" );
    public static final RegistryObject<SoundEvent> SHADEFIEND_DEATH = register( "entity.shadefiend.death" );
    
    
    private static RegistryObject<SoundEvent> register( String name ) {
        return SOUNDS.register( name, () -> SoundEvent.createVariableRangeEvent( Apocalypse.rl( name ) ) );
    }
}
