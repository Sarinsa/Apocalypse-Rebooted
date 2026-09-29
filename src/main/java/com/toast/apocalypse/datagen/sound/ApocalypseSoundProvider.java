package com.toast.apocalypse.datagen.sound;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

import static com.toast.apocalypse.api.lib.ApocalypseObjects.SoundEvents;

public class ApocalypseSoundProvider extends AbstractSoundProvider {
    
    public ApocalypseSoundProvider( DataGenerator dataGen, ExistingFileHelper fileHelper ) {
        super( dataGen.getPackOutput(), Apocalypse.MOD_ID, fileHelper );
    }
    
    @Override
    protected void addSoundEvents() {
        addEntry( SoundEvents.LUNAR_ARMOR_REACT, "lunar_armor.react", "Midnight Steel armor reacting",
                mcLoc( "block/enchantment_table/enchant" ), 1, 3 );
        addEntry( SoundEvents.LUNAR_ARMOR_EQUIP, "lunar_armor.equip", "Midnight Steel armor clanks",
                mcLoc( "item/armor/equip_iron" ), 1, 6 );
        
        addEntry( SoundEvents.DYNAMIC_TRAP_ACTIVATE, "dynamic_trap.activate", "Dynamic Trap activated",
                mcLoc( "random/click" ) );
        
        addEntry( SoundEvents.MONSTER_HOOK_RETRIEVE, "monster_fish_hook.retrieve", "Grump hook retrieved",
                mcLoc( "entity/bobber/retrieve1" ) );
        
        addEntry( SoundEvents.DESTROYER_FIREBALL_DEFLECT, "destroyer_fireball.deflect", "Destroyer fireball fizzles",
                mcLoc( "random/fuse" ) );
        
        addEntry( SoundEvents.SEEKER_FIREBALL_IGNITE, "seeker_fireball.ignite", "Seeker fireball ignited",
                mcLoc( "mob/ghast/fireball4" ) );
        
        addEntry( SoundEvents.DESTROYER_WARN, "destroyer.warn", "Destroyer shoots",
                mcLoc( "mob/ghast/charge" ) );
        addEntry( SoundEvents.DESTROYER_SHOOT, "destroyer.shoot", "Destroyer shoots",
                mcLoc( "mob/ghast/fireball4" ) );
        addEntry( SoundEvents.DESTROYER_HURT, "destroyer.hurt", "Destroyer hurts",
                mcLoc( "mob/ghast/scream" ), 1, 5 );
        addEntry( SoundEvents.DESTROYER_DEATH, "destroyer.death", "Destroyer dies",
                mcLoc( "mob/ghast/death" ) );
        
        addEntry( SoundEvents.SEEKER_WARN, "seeker.warn", "Seeker shoots",
                mcLoc( "mob/ghast/charge" ) );
        addEntry( SoundEvents.SEEKER_SHOOT, "seeker.shoot", "Seeker shoots",
                mcLoc( "mob/ghast/fireball4" ) );
        addEntry( SoundEvents.SEEKER_ALERT_MOBS, "seeker.alert", "Seeker alerts",
                mcLoc( "mob/ghast/affectionate_scream" ) );
        addEntry( SoundEvents.SEEKER_HURT, "seeker.hurt", "Seeker hurts",
                mcLoc( "mob/ghast/scream" ), 1, 5 );
        addEntry( SoundEvents.SEEKER_DEATH, "seeker.death", "Seeker dies",
                mcLoc( "mob/ghast/death" ) );
        
        addEntry( SoundEvents.BREECHER_HURT, "breecher.hurt", "Breecher hurts",
                mcLoc( "mob/creeper/say" ), 1, 4 );
        addEntry( SoundEvents.BREECHER_DEATH, "breecher.death", "Breecher dies",
                mcLoc( "mob/creeper/death" ) );
        
        addEntry( SoundEvents.GHOST_IDLE, "ghost.idle", "Ghost groans",
                mcLoc( "mob/blaze/breathe" ), 1, 4 );
        addEntry( SoundEvents.GHOST_HURT, "ghost.hurt", "Ghost hurts",
                mcLoc( "mob/endermen/scream" ), 1, 4 );
        addEntry( SoundEvents.GHOST_DEATH, "ghost.death", "Ghost perishes",
                mcLoc( "mob/blaze/death" ) );
        addEntry( SoundEvents.GHOST_FREEZE, "ghost.freeze", "Ghost freezes",
                modLoc( "entity/ghost/ghost_freeze" ) );
        
        addEntry( SoundEvents.GRUMP_HURT, "grump.hurt", "Grump hurts",
                mcLoc( "mob/ghast/scream" ), 1, 5 );
        addEntry( SoundEvents.GRUMP_DEATH, "grump.death", "Grump hurts",
                mcLoc( "mob/ghast/death" ) );
        addEntry( SoundEvents.GRUMP_RAGE, "grump.rage", "Grump is enraged",
                modLoc( "entity/grump/rage" ) );
        addEntry( SoundEvents.GRUMP_EAT, "grump.eat", "Grump eats",
                mcLoc( "entity/horse/eat" ), 1, 5 );
        addEntry( SoundEvents.GRUMP_LAUNCH_HOOK, "grump.launch_hook", "Grump launches hook",
                mcLoc( "entity/bobber/castfast" ) );
        addEntry( SoundEvents.GRUMP_EQUIP_SADDLE, "grump.equip_saddle", "Saddle equips",
                mcLoc( "mob/horse/leather" ) );
        
        addEntry( SoundEvents.FEARWOLF_STEP, "fearwolf.step", "Footsteps",
                mcLoc( "mob/wolf/step" ), 1, 5 );
        addEntry( SoundEvents.FEARWOLF_IDLE, "fearwolf.idle", "Fearwolf growls",
                mcLoc( "mob/wolf/growl" ), 1, 3 );
        addEntry( SoundEvents.FEARWOLF_HURT, "fearwolf.hurt", "Fearwolf hurts",
                mcLoc( "mob/wolf/hurt" ), 1, 3 );
        addEntry( SoundEvents.FEARWOLF_DEATH, "fearwolf.death", "Fearwolf dies",
                mcLoc( "mob/wolf/death" ) );
        
        addEntry( SoundEvents.SHADEFIEND_FLAP, "shadefiend.flap", "Shadefiend flaps",
                mcLoc( "mob/wolf/death" ) );
        addEntry( SoundEvents.SHADEFIEND_IDLE, "shadefiend.idle", "Shadefiend screeches",
                mcLoc( "mob/wolf/death" ) );
        addEntry( SoundEvents.SHADEFIEND_BITE, "shadefiend.bite", "Shadefiend bites",
                mcLoc( "mob/wolf/death" ) );
        addEntry( SoundEvents.SHADEFIEND_HURT, "shadefiend.hurt", "Shadefiend dies",
                mcLoc( "mob/wolf/death" ) );
        addEntry( SoundEvents.SHADEFIEND_DEATH, "shadefiend.death", "Shadefiend dies",
                mcLoc( "mob/wolf/death" ) );
    }
    
    
    /** Adds a sound event entry and creates subtitle translation data for it. */
    private void addEntry( RegistryObject<SoundEvent> regObj, String subtitleKey, String subtitle, ResourceLocation... soundFiles ) {
        addEntry( regObj, subtitleKey, subtitle, List.of( soundFiles ) );
    }
    
    /** Adds a sound event entry and creates subtitle translation data for it. */
    private void addEntry( RegistryObject<SoundEvent> regObj, String subtitleKey, String subtitle, List<ResourceLocation> soundFiles ) {
        String translationKey = "sound_event." + modId + ".subtitle." + subtitleKey;
        Apocalypse.tk( translationKey, subtitle );
        add( regObj, translationKey, soundFiles );
    }
    
    /**
     * Adds a sound event entry and creates subtitle translation data for it.
     * This method is essentially a shortcut for calling {@link #addEntry(RegistryObject, String, String, List)}
     * with {@link #soundsRanged(ResourceLocation, int, int)} as the list of sound file locations.
     *
     * @param baseLoc The "base" resource location of the sound files to add to the entry.
     * @param min     The minimum index.
     * @param max     The maximum index.
     */
    @SuppressWarnings( "SameParameterValue" )
    private void addEntry( RegistryObject<SoundEvent> regObj, String subtitleKey, String subtitle, ResourceLocation baseLoc, int min, int max ) {
        addEntry( regObj, subtitleKey, subtitle, soundsRanged( baseLoc, min, max ) );
    }
}
