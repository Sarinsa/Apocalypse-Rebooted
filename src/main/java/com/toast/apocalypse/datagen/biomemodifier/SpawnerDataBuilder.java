package com.toast.apocalypse.datagen.biomemodifier;

import net.minecraft.util.random.Weight;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

/** A builder-style helper for building a list of {@link net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData spawner data} entries. */
public class SpawnerDataBuilder {
    
    /** The internal list of spawner data instances. */
    private final List<MobSpawnSettings.SpawnerData> spawnerData = new ArrayList<>();
    
    
    /** Adds a spawner data instance to the list. */
    public <T extends LivingEntity> SpawnerDataBuilder add( RegistryObject<EntityType<T>> registryObject, int weight, int minCount, int maxCount ) {
        spawnerData.add( new MobSpawnSettings.SpawnerData( registryObject.get(), weight, minCount, maxCount ) );
        return this;
    }
    
    /** Adds a spawner data instance to the list. */
    public <T extends LivingEntity> SpawnerDataBuilder add( EntityType<T> entityType, int weight, int minCount, int maxCount ) {
        spawnerData.add( new MobSpawnSettings.SpawnerData( entityType, weight, minCount, maxCount ) );
        return this;
    }
    
    /** Adds a spawner data instance to the list. */
    public <T extends LivingEntity> SpawnerDataBuilder add( EntityType<T> entityType, Weight weight, int minCount, int maxCount ) {
        spawnerData.add( new MobSpawnSettings.SpawnerData( entityType, weight, minCount, maxCount ) );
        return this;
    }
    
    /** Adds a spawner data instance to the list. */
    public <T extends LivingEntity> SpawnerDataBuilder add( RegistryObject<EntityType<T>> registryObject, Weight weight, int minCount, int maxCount ) {
        spawnerData.add( new MobSpawnSettings.SpawnerData( registryObject.get(), weight, minCount, maxCount ) );
        return this;
    }
    
    /** @return The internal list of spawner data. */
    public List<MobSpawnSettings.SpawnerData> build() {
        return spawnerData;
    }
}
