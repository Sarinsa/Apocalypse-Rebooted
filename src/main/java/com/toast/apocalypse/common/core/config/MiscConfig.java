package com.toast.apocalypse.common.core.config;

import com.toast.apocalypse.api.util.ApocalypseObjects;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BlockListField;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.value.BlockEntry;
import fathertoast.crust.api.config.common.value.BlockList;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import java.util.List;

public class MiscConfig extends AbstractConfigFile {
    
    public final VersionCheck VERSION_CHECK;
    public final TrapProperties TRAP_PROPERTIES;
    public final Events EVENTS;
    public final Other OTHER;
    
    
    /** Builds the config spec that should be used for this config. */
    public MiscConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "This config contains misc settings; a bit of this and a bit of that."
        );
        SPEC.fileOnlyNewLine();
        SPEC.describeBlockList();
        SPEC.fileOnlyNewLine();
        
        VERSION_CHECK = new VersionCheck( this );
        TRAP_PROPERTIES = new TrapProperties( this );
        EVENTS = new Events( this );
        OTHER = new Other( this );
    }
    
    public static class VersionCheck extends AbstractConfigCategory<MiscConfig> {
        
        public final BooleanField sendUpdateMessage;
        
        
        VersionCheck( MiscConfig parent ) {
            super( parent, "version_check",
                    "Contains settings related to version checking (when Forge looks for updates of Apocalypse)." );
            
            sendUpdateMessage = SPEC.define( new BooleanField( "send_update_message", true,
                    "If enabled, the player will receive an in-game message when a new mod update is released." ) );
            
            SPEC.newLine();
        }
    }
    
    
    public static class TrapProperties extends AbstractConfigCategory<MiscConfig> {
        
        public final IntField ghostFreezeRange;
        public final IntField armorShatterRange;
        
        
        TrapProperties( MiscConfig parent ) {
            super( parent, "trap_properties",
                    "Contains settings related to trap types used in the Dynamic Trap.",
                    "NOTE: AoE for traps depends on the facing of the Dynamic Trap! The bounding box is not centered on the Dynamic Trap block." );
            
            ghostFreezeRange = SPEC.define( new IntField( "ghost_freeze_range", 20, 1, 60,
                    "The range for the Dynamic Trap's AoE in which the Ghost Freeze trap will affect ghosts." ) );
            armorShatterRange = SPEC.define( new IntField( "armor_shatter_range", 20, 1, 60,
                    "The range for the Dynamic Trap's AoE in which the Armor Shatter trap will affect mobs." ) );
            
            SPEC.newLine();
        }
    }
    
    
    public static class Events extends AbstractConfigCategory<MiscConfig> {
        
        public final BooleanField displayStartMessage;
        
        
        Events( MiscConfig parent ) {
            super( parent, "events",
                    "Contains settings shared by all events from Apocalypse." );
            
            displayStartMessage = SPEC.define( new BooleanField( "display_start_message", true,
                    "If enabled, Apocalypse events will display a short message to the player when they start up." ) );
            
            SPEC.newLine();
        }
    }
    
    
    public static class Other extends AbstractConfigCategory<MiscConfig> {
        
        public final BooleanField rainFizzlesTorches;
        
        public final DoubleField grumpBucketHelmetChance;
        
        public final BlockListField breecherExplosionTargets;
        
        public final IntField seekerExplosionPower;
        
        public final BlockListField destroyerProofBlocks;
        public final IntField destroyerExplosionPower;
        public final IntField destroyerEquipmentDamage;
        public final BooleanField destroyerTargetRespawnPos;
        
        public final BooleanField pauseDaylightCycle;
        
        public final IntField lunarEquipmentUpdateTime;
        
        
        Other( MiscConfig parent ) {
            super( parent, "other",
                    "Some uncategorized settings. Very cool!" );
            
            rainFizzlesTorches = SPEC.define( new BooleanField( "rain_fizzles_torches", true,
                    "If enabled, torches exposed to rain will become wet and fizzle out.",
                    "Wet torches give off very little light, but will reignite on their own when the rain stops." ) );
            
            SPEC.newLine();
            
            grumpBucketHelmetChance = SPEC.define( new DoubleField( "grump_bucket_helmet_chance", 0.05, DoubleField.Range.PERCENT,
                    "The chance for grumps to spawn with a bucket helmet equipped.",
                    "Grumps with bucket helmets take greatly reduced damage from arrows." ) );
            
            SPEC.newLine();
            
            breecherExplosionTargets = SPEC.define( new BlockListField( "breecher_explosion_targets", new BlockList( List.of(), List.of(
                    BlockTags.BEDS, BlockTags.DOORS, BlockTags.TRAPDOORS, Tags.Blocks.CHESTS, Tags.Blocks.BARRELS, Tags.Blocks.FENCE_GATES
            ), new BlockEntry( ApocalypseObjects.Blocks.DYNAMIC_TRAP.get() ) ),
                    "A list of blocks that the Breecher will target and try to explode if it can't currently reach its target player." ) );
            
            SPEC.newLine();
            
            seekerExplosionPower = SPEC.define( new IntField( "seeker_explosion_power", 4, 1, 10,
                    "The explosion power of Seeker fireballs." ) );
            
            SPEC.newLine();
            
            destroyerProofBlocks = SPEC.define( new BlockListField( "destroyer_proof_blocks", new BlockList( List.of(), List.of(),
                    new BlockEntry( Blocks.BEDROCK )
            ),
                    "A list of blocks that the Destroyer cannot explode.",
                    "Generally speaking destroyers are supposed to be able to blow up anything, but some exceptions may be desired (bedrock and whatnot)." ) );
            destroyerExplosionPower = SPEC.define( new IntField( "destroyer_explosion_power", 2, 1, 10,
                    "The explosion power of Destroyer fireballs." ) );
            destroyerEquipmentDamage = SPEC.define( new IntField( "destroyer_equipment_damage", 0, IntField.Range.NON_NEGATIVE,
                    "Additional damage destroyer fireballs deals to its target's equipment (armor, shield etc.). Set this to 0 to deal no extra damage." ) );
            destroyerTargetRespawnPos = SPEC.define( new BooleanField( "destroyer_target_respawn_pos", false,
                    "If enabled, Destroyers will attempt to blow up their target player's respawn point (bed, respawn anchor etc.).",
                    "This does not apply to the global respawn point (where players respawn if they have no bed etc.)" ) );
            
            SPEC.newLine();
            
            pauseDaylightCycle = SPEC.define( new BooleanField( "pause_daylight_cycle", true,
                    "(Only relevant for multiplayer) If enabled, the day-night cycle will pause if no players are online.",
                    "Useful if you want your players to be unable to just skip through full moons by disconnecting." ) );
            
            SPEC.newLine();
            
            lunarEquipmentUpdateTime = SPEC.define( new IntField( "lunar_equipment_update_time", 60, 5, 3600,
                    "Some equipment added by Apocalypse (Midnight Steel armor) has abilities or attribute modifiers that halve on new moons or change over time when it is a full moon night.",
                    "This field's value is the amount of seconds that must pass before the equipment's stats change again during full moon nights." ) );
            
            SPEC.newLine();
        }
    }
}
