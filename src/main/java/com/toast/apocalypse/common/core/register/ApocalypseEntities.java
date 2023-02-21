package com.toast.apocalypse.common.core.register;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.*;
import com.toast.apocalypse.common.entity.projectile.DestroyerFireballEntity;
import com.toast.apocalypse.common.entity.projectile.MonsterFishHook;
import com.toast.apocalypse.common.entity.projectile.SeekerFireballEntity;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ApocalypseEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Apocalypse.MODID);


    public static final RegistryObject<EntityType<MonsterFishHook>> MONSTER_FISH_HOOK = register("monster_fish_hook", EntityType.Builder.<MonsterFishHook>of(MonsterFishHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5));
    public static final RegistryObject<EntityType<DestroyerFireballEntity>> DESTROYER_FIREBALL = register("destroyer_fireball", EntityType.Builder.<DestroyerFireballEntity>of(DestroyerFireballEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(4));
    public static final RegistryObject<EntityType<SeekerFireballEntity>> SEEKER_FIREBALL = register("seeker_fireball", EntityType.Builder.<SeekerFireballEntity>of(SeekerFireballEntity::new, MobCategory.MISC).sized(0.6F, 0.6F).clientTrackingRange(4).updateInterval(6));
    public static final RegistryObject<EntityType<Ghost>> GHOST = register("ghost", EntityType.Builder.of(Ghost::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
    public static final RegistryObject<EntityType<Destroyer>> DESTROYER = register("destroyer", EntityType.Builder.of(Destroyer::new, MobCategory.MONSTER).sized(4.5F, 4.5F).clientTrackingRange(10).fireImmune());
    public static final RegistryObject<EntityType<Seeker>> SEEKER = register("seeker", EntityType.Builder.of(Seeker::new, MobCategory.MONSTER).sized(4.5F, 4.5F).clientTrackingRange(10).fireImmune());
    public static final RegistryObject<EntityType<Grump>> GRUMP = register("grump", EntityType.Builder.of(Grump::new, MobCategory.MONSTER).sized(1.0F, 1.0F));
    public static final RegistryObject<EntityType<Breecher>> BREECHER = register("breecher", EntityType.Builder.of(Breecher::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8));
    public static final RegistryObject<EntityType<Fearwolf>> FEARWOLF = register("fearwolf", EntityType.Builder.of(Fearwolf::new, MobCategory.MONSTER).sized(1.6F, 1.8F));


    /**
     * Called during the EntityAttributeCreationEvent.
     * Our entities' attributes are created here.
     */
    public static void createEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(GHOST.get(), Ghost.createGhostAttributes().build());
        event.put(DESTROYER.get(), Destroyer.createDestroyerAttributes().build());
        event.put(SEEKER.get(), Seeker.createSeekerAttributes().build());
        event.put(GRUMP.get(), Grump.createGrumpAttributes().build());
        event.put(BREECHER.get(), Breecher.createBreecherAttributes().build());
        event.put(FEARWOLF.get(), Fearwolf.createAttributes().build());
    }

    /**
     * Called during the FMLCommonSetupEvent.
     * Our entities' spawn placements are registered here.
     */
    public static void registerEntitySpawnPlacement(SpawnPlacementRegisterEvent event) {
        event.register(GHOST.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.WORLD_SURFACE, Ghost::checkGhostSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(BREECHER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Mob::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(DESTROYER.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, Destroyer::checkDestroyerSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(SEEKER.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, Seeker::checkSeekerSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(GRUMP.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, Grump::checkGrumpSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(FEARWOLF.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Fearwolf::checkFearwolfSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    private static <I extends Entity> RegistryObject<EntityType<I>> register(String name, EntityType.Builder<I> builder) {
        return ENTITIES.register(name, () -> builder.build(name));
    }
}
