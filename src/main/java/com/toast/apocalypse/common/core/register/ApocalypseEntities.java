package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.api.lib.ApocalypseObjects;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.*;
import com.toast.apocalypse.common.entity.projectile.DestroyerFireballEntity;
import com.toast.apocalypse.common.entity.projectile.MonsterFishHook;
import com.toast.apocalypse.common.entity.projectile.SeekerFireballEntity;
import fathertoast.crust.api.lib.CrustEntityHelper;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Objects;

public class ApocalypseEntities {
    
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.ENTITY_TYPES, Apocalypse.MOD_ID );
    
    public static final RegistryObject<EntityType<MonsterFishHook>> MONSTER_FISH_HOOK = register( ApocalypseObjects.EntityTypes.MONSTER_FISH_HOOK,
            EntityType.Builder.<MonsterFishHook>of( MonsterFishHook::new, MobCategory.MISC )
                    .sized( 0.25F, 0.25F ).noSave().noSummon()
                    .clientTrackingRange( 4 ).updateInterval( 5 ) );
    public static final RegistryObject<EntityType<DestroyerFireballEntity>> DESTROYER_FIREBALL = register( ApocalypseObjects.EntityTypes.DESTROYER_FIREBALL,
            EntityType.Builder.<DestroyerFireballEntity>of( DestroyerFireballEntity::new, MobCategory.MISC )
                    .sized( 1.0F, 1.0F )
                    .clientTrackingRange( 4 ).updateInterval( 4 ) );
    public static final RegistryObject<EntityType<SeekerFireballEntity>> SEEKER_FIREBALL = register( ApocalypseObjects.EntityTypes.SEEKER_FIREBALL,
            EntityType.Builder.<SeekerFireballEntity>of( SeekerFireballEntity::new, MobCategory.MISC )
                    .sized( 0.6F, 0.6F )
                    .clientTrackingRange( 4 ).updateInterval( 6 ) );
    
    public static final RegistryObject<EntityType<Ghost>> GHOST = register( ApocalypseObjects.EntityTypes.GHOST, CrustEntityHelper.monsterType( Ghost::new, 0.6F, 1.95F ) );
    public static final RegistryObject<EntityType<Destroyer>> DESTROYER = register( ApocalypseObjects.EntityTypes.DESTROYER, CrustEntityHelper.monsterType( Destroyer::new, 4.5F, 4.5F ).fireImmune() );
    public static final RegistryObject<EntityType<Seeker>> SEEKER = register( ApocalypseObjects.EntityTypes.SEEKER, CrustEntityHelper.monsterType( Seeker::new, 4.5F, 4.5F ).fireImmune() );
    public static final RegistryObject<EntityType<Grump>> GRUMP = register( ApocalypseObjects.EntityTypes.GRUMP, CrustEntityHelper.monsterType( Grump::new, 1.0F, 1.0F ) );
    public static final RegistryObject<EntityType<Breecher>> BREECHER = register( ApocalypseObjects.EntityTypes.BREECHER, CrustEntityHelper.monsterType( Breecher::new, 0.6F, 1.7F ) );
    public static final RegistryObject<EntityType<Fearwolf>> FEARWOLF = register( ApocalypseObjects.EntityTypes.FEARWOLF, CrustEntityHelper.monsterType( Fearwolf::new, 1.6F, 1.8F ) );
    public static final RegistryObject<EntityType<Shadefiend>> SHADEFIEND = register( ApocalypseObjects.EntityTypes.SHADEFIEND, CrustEntityHelper.monsterType( Shadefiend::new, 0.9F, 0.5F ) );
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers an entity to the deferred register. */
    @SuppressWarnings( "SameParameterValue" )
    private static <T extends Entity, B extends Entity> RegistryObject<EntityType<B>> register( RegistryObject<EntityType<T>> regObj, EntityType.Builder<B> builder ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();
        return REGISTRY.register( name, () -> builder.build( name ) );
    }
    
    /** Called when entity attributes are to be created and registered. */
    public static void createEntityAttributes( EntityAttributeCreationEvent event ) {
        event.put( GHOST.get(), Ghost.createGhostAttributes().build() );
        event.put( DESTROYER.get(), Destroyer.createDestroyerAttributes().build() );
        event.put( SEEKER.get(), Seeker.createSeekerAttributes().build() );
        event.put( GRUMP.get(), Grump.createGrumpAttributes().build() );
        event.put( BREECHER.get(), Breecher.createBreecherAttributes().build() );
        event.put( FEARWOLF.get(), Fearwolf.createAttributes().build() );
        event.put( SHADEFIEND.get(), Shadefiend.createAttributes().build() );
    }
    
    /** Called when it is time to register spawn placements. */
    public static void registerEntitySpawnPlacement( SpawnPlacementRegisterEvent event ) {
        spawnPlacement( event, GHOST, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.WORLD_SURFACE, Ghost::checkGhostSpawnRules );
        spawnPlacement( event, BREECHER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Mob::checkMobSpawnRules );
        spawnPlacement( event, DESTROYER, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, Destroyer::checkDestroyerSpawnRules );
        spawnPlacement( event, SEEKER, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, Seeker::checkSeekerSpawnRules );
        spawnPlacement( event, GRUMP, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, Grump::checkGrumpSpawnRules );
        spawnPlacement( event, FEARWOLF, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Fearwolf::checkFearwolfSpawnRules );
        spawnPlacement( event, SHADEFIEND, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, Shadefiend::checkShadefiendSpawnRules );
    }
    
    /** Registers spawn placement for an entity type. */
    @SuppressWarnings( "unchecked" )
    private static <T extends Entity, P extends Entity> void spawnPlacement( SpawnPlacementRegisterEvent event, RegistryObject<EntityType<T>> regObj,
                                                                             @Nullable SpawnPlacements.Type placementType, @Nullable Heightmap.Types heightmap,
                                                                             SpawnPlacements.SpawnPredicate<P> predicate ) {
        event.register( regObj.get(), placementType, heightmap, (SpawnPlacements.SpawnPredicate<T>) predicate, SpawnPlacementRegisterEvent.Operation.REPLACE );
    }
}
