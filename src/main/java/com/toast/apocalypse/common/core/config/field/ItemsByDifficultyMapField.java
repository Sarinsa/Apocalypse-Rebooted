package com.toast.apocalypse.common.core.config.field;

import com.toast.apocalypse.common.core.config.value.ItemsByDifficultyMap;
import fathertoast.crust.api.config.common.field.collection.FuzzyMapField;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a config field with difficulty levels and associated lists of item stacks.
 */
public class ItemsByDifficultyMapField extends FuzzyMapField<Double, List<FuzzyKey<ItemStack>>, ItemsByDifficultyMap> {
    
    /** Creates a new field. */
    public ItemsByDifficultyMapField( String key, ItemsByDifficultyMap defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /**
     * @return The entries in this map. Use {@link FuzzyEntry#matches(Object)} to test the entry condition,
     * and {@link FuzzyEntry#get()} for the items to use when the conditions are met.
     */
    public List<FuzzyEntry<Double, List<FuzzyKey<ItemStack>>>> entries() { return get().getList(); }
}