package com.toast.apocalypse.common.register;

import com.toast.apocalypse.api.register.IRegistryHelper;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.DestroyerEntity;
import com.toast.apocalypse.common.entity.living.GhostEntity;
import com.toast.apocalypse.common.entity.living.GrumpEntity;
import com.toast.apocalypse.common.entity.projectile.DestroyerFireballEntity;
import com.toast.apocalypse.common.entity.projectile.MonsterFishHook;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ApocalypseEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Apocalypse.MODID);

    public static EntityType<GhostEntity> GHOST_TYPE;
    public static EntityType<DestroyerEntity> DESTROYER_TYPE;
    public static EntityType<GrumpEntity> GRUMP_TYPE;

    public static final RegistryObject<EntityType<MonsterFishHook>> MONSTER_FISH_HOOK = register("monster_fish_hook", EntityType.Builder.<MonsterFishHook>of(MonsterFishHook::new, EntityClassification.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5));
    public static final RegistryObject<EntityType<DestroyerFireballEntity>> DESTROYER_FIREBALL = register("destroyer_fireball", EntityType.Builder.<DestroyerFireballEntity>of(DestroyerFireballEntity::new, EntityClassification.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10));
    public static final RegistryObject<EntityType<GhostEntity>> GHOST = register("ghost", () -> GHOST_TYPE);
    public static final RegistryObject<EntityType<DestroyerEntity>> DESTROYER = register("destroyer", () -> DESTROYER_TYPE);
    public static final RegistryObject<EntityType<GrumpEntity>> GRUMP = register("grump", () -> GRUMP_TYPE);

    /**
     * Initializing entity types for living entities in the mod class
     * constructor so that the entity types can be used for the spawn egg items
     * before entity types are registered.
     * Probably better ways of doing this, but it works.
     */
    public static void initTypes() {
        GHOST_TYPE = create("ghost", EntityType.Builder.of(GhostEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.8F));
        DESTROYER_TYPE = create("destroyer", EntityType.Builder.of(DestroyerEntity::new, EntityClassification.MONSTER).sized(4.5F, 4.5F).clientTrackingRange(10).fireImmune());
        GRUMP_TYPE = create("grump", EntityType.Builder.of(GrumpEntity::new, EntityClassification.MONSTER).sized(1.0F, 1.0F));
    }


    /**
     * Called during the EntityAttributeCreationEvent.
     * Our entities' attributes are created here.
     */
    public static void createEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(GHOST.get(), GhostEntity.createGhostAttributes().build());
        event.put(DESTROYER.get(), DestroyerEntity.createDestroyerAttributes().build());
        event.put(GRUMP.get(), GrumpEntity.createGrumpAttributes().build());
    }

    /**
     * Registering our own stuff here.
     * Called during {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}
     * from our mod class.
     */
    public static void registerFullMoon(IRegistryHelper registryHelper) {
        registryHelper.registerFullMoonMob(ApocalypseEntities.GHOST.get(), 9, true);
        registryHelper.registerFullMoonMob(ApocalypseEntities.DESTROYER.get(), 5, true);
        registryHelper.registerFullMoonMob(ApocalypseEntities.GRUMP.get(), 3, true);
    }

    /**
     * Called during the FMLCommonSetupEvent.
     * Our entities' spawn placements are registered here.
     */
    public static void registerEntitySpawnPlacement() {
        EntitySpawnPlacementRegistry.register(GHOST.get(), EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GhostEntity::checkGhostSpawnRules);
        EntitySpawnPlacementRegistry.register(DESTROYER.get(), EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DestroyerEntity::checkDestroyerSpawnRules);
        EntitySpawnPlacementRegistry.register(GRUMP.get(), EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GrumpEntity::checkGrumpSpawnRules);
    }

    private static <I extends Entity> RegistryObject<EntityType<I>> register(String name, EntityType.Builder<I> builder) {
        return ENTITIES.register(name, () -> builder.build(name));
    }

    private static <I extends Entity> RegistryObject<EntityType<I>> register(String name, Supplier<EntityType<I>> entityTypeSupplier) {
        return ENTITIES.register(name, entityTypeSupplier);
    }

    private static <I extends Entity> EntityType<I> create(String name, EntityType.Builder<I> builder) {
        return builder.build(name);
    }
}
