package com.toast.apocalypse.common.core.config;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.util.MobHelper;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.field.collection.AttributeOpListField;
import fathertoast.crust.api.config.common.field.collection.BlockStateSetField;
import fathertoast.crust.api.config.common.value.collection.AttributeOpList;
import fathertoast.crust.api.config.common.value.collection.BlockStateSet;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

public class EntitiesConfig extends AbstractConfigFile {
    
    public final FullMoon FULL_MOON;
    public final Misc MISC;
    
    /** Builds the config spec that should be used for this config. */
    public EntitiesConfig( String cfgName ) {
        super( Apocalypse.MOD_ID, cfgName, false,
                "This config contains options for the entities added by this mod."
        );
        BlockStateSetField.describe( SPEC );
        
        FULL_MOON = new FullMoon( this );
        MISC = new Misc( this );
    }
    
    public static class FullMoon extends AbstractConfigCategory<EntitiesConfig> {
        
        public final AttributeOpListField ghostAttributes;
        public final DoubleField ghostWeightDuration;
        public final DoubleField ghostDeathtouch;
        
        public final AttributeOpListField destroyerAttributes;
        public final BlockStateSetField destroyerProofBlocks;
        public final IntField destroyerExplosionPower;
        public final IntField destroyerEquipmentDamage;
        public final BooleanField destroyerTargetRespawnPos;
        
        public final AttributeOpListField seekerAttributes;
        public final IntField seekerExplosionPower;
        
        public final AttributeOpListField grumpAttributes;
        public final DoubleField grumpWeightDuration;
        public final DoubleField grumpDeathtouch;
        public final DoubleField grumpBucketHelmetChance;
        
        public final AttributeOpListField breecherAttributes;
        public final BlockStateSetField breecherExplosionTargets;
        
        FullMoon( EntitiesConfig parent ) {
            super( parent, "lunar_siege_monsters",
                    "Options to customize the monsters that tend to be exclusive to lunar sieges " +
                            "(the full moon event)." );
            
            ghostAttributes = SPEC.define( attributesField( "ghost", "ghosts" ) );
            ghostWeightDuration = SPEC.define( new DoubleField( "ghost.weight_duration", 1.0, DoubleField.Range.NON_NEGATIVE,
                    "Duration multiplier for the Weight II applied by ghost melee attacks. (Weight pulls " +
                            "you downward and increases fall damage.)",
                    MobHelper.EFFECT_DURATION_NOTE ) );
            ghostDeathtouch = SPEC.define( new DoubleField( "ghost.deathtouch", 1.0, DoubleField.Range.NON_NEGATIVE,
                    "Amount of health loss ghosts inflict with their melee attacks.",
                    "This ignores all defenses, but cannot reduce health below one half-heart." ) );
            
            SPEC.newLine();
            
            destroyerAttributes = SPEC.define( attributesField( "destroyer", "destroyers" ) );
            destroyerProofBlocks = SPEC.define( new BlockStateSetField( "destroyer.proof_blocks", new BlockStateSet.Builder<>()
                    .addTag( BlockTags.WITHER_IMMUNE ).add( Blocks.BEDROCK ) // Bedrock is in the tag normally, but also specify just in case
                    .build(),
                    "A list of blocks that destroyers cannot explode.",
                    "Generally speaking, destroyers should be able to blow up anything, but some exceptions may be " +
                            "desired (bedrock and whatnot)." ) );
            destroyerExplosionPower = SPEC.define( new IntField( "destroyer.explosion_power", 2, 1, 10,
                    "The explosion power of destroyer fireballs." ) );
            destroyerEquipmentDamage = SPEC.define( new IntField( "destroyer.equipment_damage", 0, IntField.Range.NON_NEGATIVE,
                    "Additional damage destroyer fireballs deal to their target's equipment (armor, shield, etc.). " +
                            "Set this to 0 to deal no extra damage." ) );
            destroyerTargetRespawnPos = SPEC.define( new BooleanField( "destroyer.target_respawn_pos", false,
                    "If enabled, destroyers will attempt to blow up their target player's respawn point " +
                            "(bed, respawn anchor, etc.) if that player is dead.",
                    "This does not apply to the global respawn point (where players respawn if they have no bed)" ) );
            
            SPEC.newLine();
            
            seekerAttributes = SPEC.define( attributesField( "seeker", "seekers" ) );
            seekerExplosionPower = SPEC.define( new IntField( "seeker.explosion_power", 3, 1, 10,
                    "The explosion power of seeker fireballs." ) );
            
            SPEC.newLine();
            
            grumpAttributes = SPEC.define( attributesField( "grump", "grumps" ) );
            grumpWeightDuration = SPEC.define( new DoubleField( "grump.weight_duration", 1.0, DoubleField.Range.NON_NEGATIVE,
                    "Duration multiplier for the Weight applied by grump melee attacks. (Weight pulls you " +
                            "downward and increases fall damage.)",
                    MobHelper.EFFECT_DURATION_NOTE ) );
            grumpDeathtouch = SPEC.define( new DoubleField( "grump.deathtouch", 0.0, DoubleField.Range.NON_NEGATIVE,
                    "Amount of health loss grumps inflict with their melee attacks.",
                    "This ignores all defenses, but cannot reduce health below one half-heart." ) );
            grumpBucketHelmetChance = SPEC.define( new DoubleField( "grump.bucket_helmet_chance", 0.05, DoubleField.Range.PERCENT,
                    "The chance for grumps to spawn with a bucket helmet equipped.",
                    "Grumps with bucket helmets take greatly reduced damage from arrows." ) );
            
            SPEC.newLine();
            
            breecherAttributes = SPEC.define( attributesField( "breecher", "breechers" ) );
            breecherExplosionTargets = SPEC.define( new BlockStateSetField( "breecher.explosion_targets", new BlockStateSet.Builder<>()
                    .addTag( BlockTags.BEDS ).addTag( BlockTags.DOORS ).addTag( BlockTags.TRAPDOORS )
                    .addTag( Tags.Blocks.CHESTS ).addTag( Tags.Blocks.BARRELS ).addTag( Tags.Blocks.FENCE_GATES )
                    .add( ApocalypseObjects.Blocks.DYNAMIC_TRAP )
                    .build(),
                    "A list of blocks that breechers will target and try to explode if they can't currently " +
                            "reach their target player." ) );
        }
    }
    
    public static class Misc extends AbstractConfigCategory<EntitiesConfig> {
        
        public final AttributeOpListField fearwolfAttributes;
        public final DoubleField fearwolfSlownessDuration;
        public final DoubleField fearwolfDeathtouch;
        
        public final AttributeOpListField shadefiendAttributes;
        public final IntField shadefiendSkyLightTolerance;
        public final IntField shadefiendBlockLightTolerance;
        public final DoubleField shadefiendDarknessDuration;
        public final DoubleField shadefiendVulnerabilityDuration;
        public final DoubleField shadefiendDeathtouch;
        
        Misc( EntitiesConfig parent ) {
            super( parent, "miscellaneous_monsters",
                    "Options to customize various entities that don't fit in a specific category." );
            
            fearwolfAttributes = SPEC.define( attributesField( "fearwolf", "fearwolves" ) );
            fearwolfSlownessDuration = SPEC.define( new DoubleField( "fearwolf.slowness_duration", 0.5, DoubleField.Range.NON_NEGATIVE,
                    "Duration multiplier for the Slowness applied by fearwolf melee attacks.",
                    MobHelper.EFFECT_DURATION_NOTE ) );
            fearwolfDeathtouch = SPEC.define( new DoubleField( "fearwolf.deathtouch", 0.0, DoubleField.Range.NON_NEGATIVE,
                    "Amount of health loss fearwolves inflict with their melee attacks.",
                    "This ignores all defenses, but cannot reduce health below one half-heart." ) );
            
            SPEC.newLine();
            
            shadefiendAttributes = SPEC.define( attributesField( "shadefiend", "the Shadefiend" ) );
            shadefiendSkyLightTolerance = SPEC.define( new IntField( "shadefiend.light_tolerance.sky", 3, 0, 16,
                    "The maximum sky and block light levels the Shadefiend can exist in without taking damage.",
                    "This should generally be at least high enough to prevent it from dying under its configured spawn conditions." ) );
            shadefiendBlockLightTolerance = SPEC.define( new IntField( "shadefiend.light_tolerance.block", 3, 0, 16 ) );
            shadefiendDarknessDuration = SPEC.define( new DoubleField( "shadefiend.darkness_duration", 0.5, DoubleField.Range.NON_NEGATIVE,
                    "Duration multiplier for the Darkness applied by the Shadefiend's melee attacks.",
                    MobHelper.EFFECT_DURATION_NOTE ) );
            shadefiendVulnerabilityDuration = SPEC.define( new DoubleField( "shadefiend.vulnerability_duration", 1.0, DoubleField.Range.NON_NEGATIVE,
                    "Duration multiplier for the Vulnerability applied by the Shadefiend's melee attacks. " +
                            "(Vulnerability causes you to take 25% more damage.)",
                    MobHelper.EFFECT_DURATION_NOTE ) );
            shadefiendDeathtouch = SPEC.define( new DoubleField( "shadefiend.deathtouch", 1.0, DoubleField.Range.NON_NEGATIVE,
                    "Amount of health loss the Shadefiend inflicts with its melee attacks.",
                    "This ignores all defenses, but cannot reduce health below one half-heart." ) );
        }
    }
    
    /** @return The standard attribute modifier field for an entity, with no modifiers on by default. */
    private static AttributeOpListField attributesField( String key, String name ) {
        return attributesField( key, name, new AttributeOpList() );
    }
    
    /** @return The standard attribute modifier field for an entity, with the supplied default modifiers. */
    private static AttributeOpListField attributesField( String key, String name, AttributeOpList defaultValue ) {
        return new AttributeOpListField( key + ".attributes", defaultValue,
                "Attribute modifiers for " + name + "." );
    }
}