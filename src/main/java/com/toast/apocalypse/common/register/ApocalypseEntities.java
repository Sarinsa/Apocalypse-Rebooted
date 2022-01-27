package com.toast.apocalypse.common.register;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.*;
import com.toast.apocalypse.common.entity.projectile.DestroyerFireballEntity;
import com.toast.apocalypse.common.entity.projectile.MonsterFishHook;
import com.toast.apocalypse.common.entity.projectile.SeekerFireballEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ApocalypseEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Apocalypse.MODID);


    public static final RegistryObject<EntityType<MonsterFishHook>> MONSTER_FISH_HOOK = register("monster_fish_hook", EntityType.Builder.<MonsterFishHook>of(MonsterFishHook::new, EntityClassification.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5));
    public static final RegistryObject<EntityType<DestroyerFireballEntity>> DESTROYER_FIREBALL = register("destroyer_fireball", EntityType.Builder.<DestroyerFireballEntity>of(DestroyerFireballEntity::new, EntityClassification.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(4));
    public static final RegistryObject<EntityType<SeekerFireballEntity>> SEEKER_FIREBALL = register("seeker_fireball", EntityType.Builder.<SeekerFireballEntity>of(SeekerFireballEntity::new, EntityClassification.MISC).sized(0.6F, 0.6F).clientTrackingRange(4).updateInterval(6));
    public static final RegistryObject<EntityType<GhostEntity>> GHOST = register("ghost", EntityType.Builder.of(GhostEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
    public static final RegistryObject<EntityType<DestroyerEntity>> DESTROYER = register("destroyer", EntityType.Builder.of(DestroyerEntity::new, EntityClassification.MONSTER).sized(4.5F, 4.5F).clientTrackingRange(10).fireImmune());
    public static final RegistryObject<EntityType<SeekerEntity>> SEEKER = register("seeker", EntityType.Builder.of(SeekerEntity::new, EntityClassification.MONSTER).sized(4.5F, 4.5F).clientTrackingRange(10).fireImmune());
    public static final RegistryObject<EntityType<GrumpEntity>> GRUMP = register("grump", EntityType.Builder.of(GrumpEntity::new, EntityClassification.MONSTER).sized(1.0F, 1.0F));
    public static final RegistryObject<EntityType<BreecherEntity>> BREECHER = register("breecher", EntityType.Builder.of(BreecherEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8));
    public static final RegistryObject<EntityType<FearwolfEntity>> FEARWOLF = register("fearwolf", EntityType.Builder.of(FearwolfEntity::new, EntityClassification.MONSTER).sized(1.6F, 1.8F));


    /**
     * Called during the EntityAttributeCreationEvent.
     * Our entities' attributes are created here.
     */
    public static void createEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(GHOST.get(), GhostEntity.createGhostAttributes().build());
        event.put(DESTROYER.get(), DestroyerEntity.createDestroyerAttributes().build());
        event.put(SEEKER.get(), SeekerEntity.createSeekerAttributes().build());
        event.put(GRUMP.get(), GrumpEntity.createGrumpAttributes().build());
        event.put(BREECHER.get(), BreecherEntity.createBreecherAttributes().build());
        event.put(FEARWOLF.get(), WolfEntity.createAttributes().build());
    }

    /**
     * Called during the FMLCommonSetupEvent.
     * Our entities' spawn placements are registered here.
     */
    public static void registerEntitySpawnPlacement() {
        EntitySpawnPlacementRegistry.register(GHOST.get(), EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.WORLD_SURFACE, GhostEntity::checkGhostSpawnRules);
        EntitySpawnPlacementRegistry.register(BREECHER.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, MobEntity::checkMobSpawnRules);
        EntitySpawnPlacementRegistry.register(DESTROYER.get(), EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING, DestroyerEntity::checkDestroyerSpawnRules);
        EntitySpawnPlacementRegistry.register(SEEKER.get(), EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING, SeekerEntity::checkSeekerSpawnRules);
        EntitySpawnPlacementRegistry.register(GRUMP.get(), EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING, GrumpEntity::checkGrumpSpawnRules);
        EntitySpawnPlacementRegistry.register(FEARWOLF.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FearwolfEntity::checkMobSpawnRules);
    }

    private static <I extends Entity> RegistryObject<EntityType<I>> register(String name, EntityType.Builder<I> builder) {
        return ENTITIES.register(name, () -> builder.build(name));
    }
}
