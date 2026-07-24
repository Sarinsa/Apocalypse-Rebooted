package com.toast.apocalypse.api.lib;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.serialization.Codec;
import com.toast.apocalypse.api.trap.AbstractTrap;
import com.toast.apocalypse.common.command.argument.DifficultyArgument;
import com.toast.apocalypse.common.command.argument.MaxDifficultyArgument;
import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/** Contains references to Apocalypse's registry objects and custom registries. */
@SuppressWarnings( "SameParameterValue" )
public final class ApocalypseObjects {
    
    // ---- Registry Suppliers ---- //
    
    /** The below registry suppliers are populated after {@link net.minecraftforge.registries.NewRegistryEvent}. */
    public static Supplier<IForgeRegistry<AbstractTrap>> TRAP_ACTIONS_REGISTRY;
    
    
    // ----- Registry Objects ----- //
    
    /** Blocks. */
    public interface Blocks {
        RegistryObject<Block> LUNAR_PHASE_SENSOR = block( "lunar_phase_sensor" );
        RegistryObject<Block> MIDNIGHT_STEEL_BLOCK = block( "midnight_steel_block" );
        RegistryObject<Block> DYNAMIC_TRAP = block( "dynamic_trap" );
        RegistryObject<Block> DEAD_GRASS = block( "dead_grass" );
        RegistryObject<Block> DEAD_PLANT = block( "dead_plant" );
        RegistryObject<Block> WET_TORCH = block( "wet_torch" );
        RegistryObject<Block> WET_WALL_TORCH = block( "wet_wall_torch" );
    }
    
    /** Items. */
    public interface Items {
        RegistryObject<Item> FRAGMENTED_SOUL = item( "fragmented_soul" );
        RegistryObject<Item> MIDNIGHT_STEEL_INGOT = item( "midnight_steel_ingot" );
        RegistryObject<Item> FATHERLY_TOAST = item( "fatherly_toast" );
        RegistryObject<Item> BUCKET_HELM = item( "bucket_helm" );
        RegistryObject<Item> MIDNIGHT_STEEL_HELMET = item( "midnight_steel_helmet" );
        RegistryObject<Item> MIDNIGHT_STEEL_CHESTPLTAE = item( "midnight_steel_chestplate" );
        RegistryObject<Item> MIDNIGHT_STEEL_LEGGINGS = item( "midnight_steel_leggings" );
        RegistryObject<Item> MIDNIGHT_STEEL_BOOTS = item( "midnight_steel_boots" );
        RegistryObject<Item> LUNAR_CLOCK = item( "lunar_clock" );
        RegistryObject<Item> APOCALYPSE_COMPENDIUM = item( "apocalypse_compendium" );
        RegistryObject<Item> WET_TORCH = item( "wet_torch" );
        
        RegistryObject<Item> GHOST_SPAWN_EGG = item( "ghost_spawn_egg" );
        RegistryObject<Item> DESTROYER_SPAWN_EGG = item( "destroyer_spawn_egg" );
        RegistryObject<Item> SEEKER_SPAWN_EGG = item( "seeker_spawn_egg" );
        RegistryObject<Item> GRUMP_SPAWN_EGG = item( "grump_spawn_egg" );
        RegistryObject<Item> BREECHER_SPAWN_EGG = item( "breecher_spawn_egg" );
        RegistryObject<Item> FEARWOLF_SPAWN_EGG = item( "fearwolf_spawn_egg" );
        RegistryObject<Item> SHADEFIEND_SPAWN_EGG = item( "shadefiend_spawn_egg" );
    }
    
    /** Block entities. */
    public interface BlockEntities {
        RegistryObject<BlockEntityType<?>> LUNAR_PHASE_SENSOR = blockEntity( "lunar_phase_sensor" );
        RegistryObject<BlockEntityType<?>> DYNAMIC_TRAP = blockEntity( "dynamic_trap" );
    }
    
    /** Entity types. */
    public interface EntityTypes {
        RegistryObject<EntityType<Projectile>> MONSTER_FISH_HOOK = entity( "monster_fish_hook" );
        RegistryObject<EntityType<Projectile>> DESTROYER_FIREBALL = entity( "destroyer_fireball" );
        RegistryObject<EntityType<Projectile>> SEEKER_FIREBALL = entity( "seeker_fireball" );
        RegistryObject<EntityType<FlyingMob>> GHOST = entity( "ghost" );
        RegistryObject<EntityType<Ghast>> DESTROYER = entity( "destroyer" );
        RegistryObject<EntityType<Ghast>> SEEKER = entity( "seeker" );
        RegistryObject<EntityType<Ghast>> GRUMP = entity( "grump" );
        RegistryObject<EntityType<Creeper>> BREECHER = entity( "breecher" );
        RegistryObject<EntityType<Monster>> FEARWOLF = entity( "fearwolf" );
        RegistryObject<EntityType<FlyingMob>> SHADEFIEND = entity( "shadefiend" );
    }
    
    /** Global loot modifier serializers. */
    public interface LootModSerializers {
        RegistryObject<Codec<? extends IGlobalLootModifier>> SIMPLE_ADD_LOOT_MOD = lootModSerializer( "simple_add_loot_mod" );
    }
    
    /** Menu types. */
    public interface MenuTypes {
        RegistryObject<MenuType<?>> DYNAMIC_TRAP = menu( "dynamic_trap" );
    }
    
    /** Mob effects. */
    public interface MobEffects {
        // No mob effects yet.
    }
    
    /** Particle types. */
    public interface ParticleTypes {
        RegistryObject<SimpleParticleType> LUNAR_DESPAWN_SMOKE = particle( "lunar_despawn_smoke" );
    }
    
    /** Recipe serializers. */
    public interface RecipeSerializers {
        RegistryObject<RecipeSerializer<?>> TRAP_ASSEMBLING = recipeSerializer( "trap_assembling" );
    }
    
    /** Recipe types. */
    public interface RecipeTypes {
        RegistryObject<RecipeType<?>> TRAP_ASSEMBLING = recipeType( "trap_assembling" );
    }
    
    /** Sound event types. */
    public interface SoundEvents {
        RegistryObject<SoundEvent> LUNAR_ARMOR_REACT = sound( "item.lunar_armor.react" );
        RegistryObject<SoundEvent> ARMOR_EQUIP_LUNAR = sound( "item.armor.equip_lunar" );
        
        RegistryObject<SoundEvent> DYNAMIC_TRAP_ACTIVATE = sound( "block.dynamic_trap.activate" );
        
        RegistryObject<SoundEvent> MONSTER_HOOK_RETRIEVE = sound( "entity.monster_fish_hook.retrieve" );
        
        RegistryObject<SoundEvent> DESTROYER_FIREBALL_DEFLECT = sound( "entity.destroyer_fireball.deflect" );
        
        RegistryObject<SoundEvent> SEEKER_FIREBALL_IGNITE = sound( "entity.seeker_fireball.ignite" );
        
        RegistryObject<SoundEvent> BREECHER_HURT = sound( "entity.breecher.hurt" );
        RegistryObject<SoundEvent> BREECHER_DEATH = sound( "entity.breecher.death" );
        
        RegistryObject<SoundEvent> DESTROYER_WARN = sound( "entity.destroyer.warn" );
        RegistryObject<SoundEvent> DESTROYER_SHOOT = sound( "entity.destroyer.shoot" );
        RegistryObject<SoundEvent> DESTROYER_HURT = sound( "entity.destroyer.hurt" );
        RegistryObject<SoundEvent> DESTROYER_DEATH = sound( "entity.destroyer.death" );
        
        RegistryObject<SoundEvent> SEEKER_WARN = sound( "entity.seeker.warn" );
        RegistryObject<SoundEvent> SEEKER_SHOOT = sound( "entity.seeker.shoot" );
        RegistryObject<SoundEvent> SEEKER_ALERT_MOBS = sound( "entity.seeker.alert_mobs" );
        RegistryObject<SoundEvent> SEEKER_HURT = sound( "entity.seeker.hurt" );
        RegistryObject<SoundEvent> SEEKER_DEATH = sound( "entity.seeker.death" );
        
        RegistryObject<SoundEvent> GHOST_IDLE = sound( "entity.ghost.idle" );
        RegistryObject<SoundEvent> GHOST_HURT = sound( "entity.ghost.hurt" );
        RegistryObject<SoundEvent> GHOST_DEATH = sound( "entity.ghost.death" );
        RegistryObject<SoundEvent> GHOST_FREEZE = sound( "entity.ghost.freeze" );
        
        RegistryObject<SoundEvent> GRUMP_HURT = sound( "entity.grump.hurt" );
        RegistryObject<SoundEvent> GRUMP_DEATH = sound( "entity.grump.death" );
        RegistryObject<SoundEvent> GRUMP_RAGE = sound( "entity.grump.rage" );
        RegistryObject<SoundEvent> GRUMP_EAT = sound( "entity.grump.eat" );
        RegistryObject<SoundEvent> GRUMP_LAUNCH_HOOK = sound( "entity.grump.launch_hook" );
        RegistryObject<SoundEvent> GRUMP_EQUIP_SADDLE = sound( "entity.grump.equip_saddle" );
        
        RegistryObject<SoundEvent> FEARWOLF_STEP = sound( "entity.fearwolf.step" );
        RegistryObject<SoundEvent> FEARWOLF_IDLE = sound( "entity.fearwolf.idle" );
        RegistryObject<SoundEvent> FEARWOLF_HURT = sound( "entity.fearwolf.hurt" );
        RegistryObject<SoundEvent> FEARWOLF_DEATH = sound( "entity.fearwolf.death" );
        
        RegistryObject<SoundEvent> SHADEFIEND_FLAP = sound( "entity.shadefiend.flap" );
        RegistryObject<SoundEvent> SHADEFIEND_BITE = sound( "entity.shadefiend.bite" );
        RegistryObject<SoundEvent> SHADEFIEND_IDLE = sound( "entity.shadefiend.idle" );
        RegistryObject<SoundEvent> SHADEFIEND_HURT = sound( "entity.shadefiend.hurt" );
        RegistryObject<SoundEvent> SHADEFIEND_DEATH = sound( "entity.shadefiend.death" );
    }
    
    /** Trap types. */
    public interface TrapTypes {
        /** The trap type registry's key. Can be used to register new trap types and create object holders. */
        ResourceKey<Registry<AbstractTrap>> REGISTRY_KEY = ResourceKey.createRegistryKey( rl( "trap_types" ) );
        
        RegistryObject<AbstractTrap> GHOST_FREEZE = trapType( "ghost_freeze" );
        RegistryObject<AbstractTrap> EQUIPMENT_BREAK = trapType( "equipment_break" );
    }
    
    /** Command argument types. */
    public interface CmdArguments {
        RegistryObject<ArgumentTypeInfo<DifficultyArgument, ?>> DIFFICULTY = cmdArgument( "difficulty" );
        RegistryObject<ArgumentTypeInfo<MaxDifficultyArgument, ?>> MAX_DIFFICULTY = cmdArgument( "max_difficulty" );
    }
    
    
    // ---- Internal Methods ---- //
    
    /** @return An object holder for a block. */
    private static RegistryObject<Block> block( String name ) { return ro( name, ForgeRegistries.BLOCKS ); }
    
    /** @return An object holder for an item. */
    private static RegistryObject<Item> item( String name ) { return ro( name, ForgeRegistries.ITEMS ); }
    
    /** @return An object holder for a block entity type. */
    private static RegistryObject<BlockEntityType<?>> blockEntity( String name ) { return ro( name, ForgeRegistries.BLOCK_ENTITY_TYPES ); }
    
    /** @return An object holder for an entity type. */
    private static <T extends Entity> RegistryObject<EntityType<T>> entity( String name ) { return ro( name, ForgeRegistries.ENTITY_TYPES ); }
    
    /** @return An object holder for a global loot modifier serializer. */
    private static RegistryObject<Codec<? extends IGlobalLootModifier>> lootModSerializer( String name ) { return ro( name, ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS ); }
    
    /** @return An object holder for a menu type. */
    private static RegistryObject<MenuType<?>> menu( String name ) { return ro( name, ForgeRegistries.MENU_TYPES ); }
    
    /** @return An object holder for a mob effect. */
    private static RegistryObject<MobEffect> mobEffect( String name ) { return ro( name, ForgeRegistries.MOB_EFFECTS ); }
    
    /** @return An object holder for a particle type. */
    private static <T extends ParticleType<?> & ParticleOptions> RegistryObject<T> particle( String name ) { return ro( name, ForgeRegistries.PARTICLE_TYPES ); }
    
    /** @return An object holder for a recipe serializer. */
    private static RegistryObject<RecipeSerializer<?>> recipeSerializer( String name ) { return ro( name, ForgeRegistries.RECIPE_SERIALIZERS ); }
    
    /** @return An object holder for a recipe type. */
    private static RegistryObject<RecipeType<?>> recipeType( String name ) { return ro( name, ForgeRegistries.RECIPE_TYPES ); }
    
    /** @return An object holder for a sound event type. */
    private static RegistryObject<SoundEvent> sound( String name ) { return ro( name, ForgeRegistries.SOUND_EVENTS ); }
    
    /** @return An object holder for a trap type. */
    private static RegistryObject<AbstractTrap> trapType( String name ) { return ro( name, TrapTypes.REGISTRY_KEY ); }
    
    /** @return An object holder for a command argument type. */
    private static <T extends ArgumentType<?>> RegistryObject<ArgumentTypeInfo<T, ?>> cmdArgument( String name ) { return ro( name, ForgeRegistries.COMMAND_ARGUMENT_TYPES ); }
    
    
    /** @return An object holder for a Forge registry object. */
    private static <R, T extends R> RegistryObject<T> ro( String name, IForgeRegistry<R> reg ) {
        return RegistryObject.create( rl( name ), reg );
    }
    
    /** @return An object holder for a custom registry object. */
    private static <T> RegistryObject<T> ro( String name, ResourceKey<? extends Registry<T>> registryKey ) {
        return RegistryObject.createOptional( rl( name ), registryKey, Apocalypse.MOD_ID );
    }
    
    /** @return A resource location. */
    private static ResourceLocation rl( String path ) {
        return ResourceLocation.fromNamespaceAndPath( Apocalypse.MOD_ID, path );
    }
    
    
    private ApocalypseObjects() { }
}
