package com.toast.apocalypse.common.core.config.field;

import com.toast.apocalypse.common.core.config.value.MobEffectsByDifficultyMap;
import com.toast.apocalypse.common.core.config.value.PairListValueCodec;
import fathertoast.crust.api.config.common.field.collection.FuzzyMapField;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import net.minecraft.world.effect.MobEffect;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a config field with difficulty levels and associated lists of item stacks.
 */
public class MobEffectsByDifficultyMapField extends FuzzyMapField<Double, List<PairListValueCodec.Pair<FuzzyKey<MobEffect>, Integer>>, MobEffectsByDifficultyMap> {
    
    /** Creates a new field. */
    public MobEffectsByDifficultyMapField( String key, MobEffectsByDifficultyMap defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /**
     * @return The entries in this map. Use {@link FuzzyEntry#matches(Object)} to test the entry condition,
     * and {@link FuzzyEntry#get()} for the items to use when the conditions are met.
     */
    public List<FuzzyEntry<Double, List<PairListValueCodec.Pair<FuzzyKey<MobEffect>, Integer>>>> // wowee
    entries() { return get().getList(); }
}