package com.toast.apocalypse.common.register;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.GhostEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ApocalypseEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Apocalypse.MODID);

    public static EntityType<GhostEntity> GHOST_TYPE;

    public static final RegistryObject<EntityType<GhostEntity>> GHOST = register("ghost", () -> GHOST_TYPE);

    /**
     * Initializing entity types for living entities in the mod class
     * constructor so that the entity types can be used for the spawn egg items
     * before entity types are registered.
     * Probably better ways of doing this, but it works.
     */
    public static void initTypes() {
        GHOST_TYPE = create("ghost", EntityType.Builder.of(GhostEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.8F).fireImmune());
    }


    /**
     * Called during the EntityAttributeCreationEvent.
     * Our entities' attributes are created here.
     */
    public static void createEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(GHOST.get(), GhostEntity.createGhostAttributes().build());
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
