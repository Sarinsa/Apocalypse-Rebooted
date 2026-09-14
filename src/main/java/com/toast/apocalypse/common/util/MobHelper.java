package com.toast.apocalypse.common.util;

import fathertoast.crust.api.config.common.field.DoubleField;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MobHelper {
    
    /** Note to include in duration multiplier config fields. */
    public static final String EFFECT_DURATION_NOTE =
            "The base duration is 140 ticks (7 seconds) in hard mode and 80 ticks (4 seconds) otherwise.";
    
    /** Applies the mob effect to an entity based on the config duration multiplier. */
    public static void applyEffect( LivingEntity entity, @Nullable Entity source, Supplier<MobEffect> effect,
                                    int amplifier, DoubleField multiplier ) {
        applyEffect( entity, source, effect.get(), amplifier, multiplier );
    }
    
    /** Applies the mob effect to an entity based on the config duration multiplier. */
    public static void applyEffect( LivingEntity entity, @Nullable Entity source, MobEffect effect,
                                    int amplifier, DoubleField multiplier ) {
        double durationMulti = multiplier.getDouble();
        if( durationMulti > 0.0F ) {
            int duration = Mth.ceil( (entity.level().getDifficulty() == Difficulty.HARD ? 140 : 80) * durationMulti );
            entity.addEffect( new MobEffectInstance( effect, duration, amplifier ),
                    source );
        }
    }
    
    /** Applies the deathtouch effect to an entity based on the config value. */
    public static void applyDeathtouch( LivingEntity entity, DoubleField deathtouch ) {
        float reduction = deathtouch.getFloat();
        if( reduction > 0.0F ) {
            float health = entity.getHealth();
            if( health > 1.0F ) {
                entity.setHealth( Math.max( 1.0F, health - reduction ) );
            }
        }
    }
    
    /**
     * Gets a list of loaded entities of X type, with a size limit.
     *
     * @param entityClass The class of the entity to search for instances of.
     * @param level       The level to search in.
     * @param box         The area to search in.
     * @param predicate   An optional predicate for filtering entities.
     * @param cap         The desired entity count cap. If cap is < 1, an empty list is returned early.
     * @return A capped list of entities of the specified type within the given AABB.
     * Entities are first collected through {@link net.minecraft.world.level.Level#getEntitiesOfClass(Class, AABB, Predicate)},
     * and if the size of the resulting list is greater than the desired cap, entries are removed from the top of the list
     * until the size matches the cap.
     */
    public static <T extends Entity> List<? extends T> getLoadedEntitiesCapped( Class<? extends T> entityClass, LevelAccessor level, AABB box, @Nullable Predicate<? super T> predicate, final int cap ) {
        if( cap < 1 ) return List.of();
        final List<? extends T> list = level.getEntitiesOfClass( entityClass, box, predicate == null
                ? ( entity ) -> true
                : predicate );
        // Return early if list is empty
        if( list.isEmpty() ) return list;
        
        // Limit the amount of mobs.
        while( list.size() > cap ) {
            list.remove( list.size() - cap );
        }
        return list;
    }
}