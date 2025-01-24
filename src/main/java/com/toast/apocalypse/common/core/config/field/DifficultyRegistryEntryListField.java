package com.toast.apocalypse.common.core.config.field;

import com.toast.apocalypse.common.core.config.value.DifficultyRegListEntry;
import com.toast.apocalypse.common.core.config.value.DifficultyRegistryEntryList;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.GenericField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class DifficultyRegistryEntryListField<T> extends GenericField<DifficultyRegistryEntryList<T>> {

    /**
     *  Optional predicate for checking if a registry object should be allowed
     *  in this list even if it exists in the target registry.
     */
    @Nullable
    private Predicate<T> registryEntryPredicate;


    /** Provides a detailed description of how to use difficulty registry entry lists. Recommended putting at the top of any file using these lists. */
    public static List<String> verboseDescription() {
        List<String> comment = new ArrayList<>();
        comment.add("Difficulty Registry Entry List fields: General format = [ \"difficulty_level namespace:path namespace:path...\", ... ]");
        comment.add("  Difficulty registry entry lists are lists of difficulty levels linked to one or multiple registry keys of the same type. " +
                "Many things in the game, such as blocks or potions, are defined by their registry key within a registry. " +
                "For example, all items are registered in the \"minecraft:item\" registry.");
        comment.add("  Entries can so to speak hold as many values as desired, but no entries can have the same difficulty level.");
        comment.add("  It is also important to note that some lists may use custom predicates for what items are allowed. " +
                "Such lists SHOULD provide information about their special conditions.");
        return comment;
    }

    /** Creates a new field. */
    public DifficultyRegistryEntryListField(String key, DifficultyRegistryEntryList<T> defaultValue, @Nullable String... description) {
        super(key, defaultValue, description);

    }

    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo(List<String> comment) {
        comment.add(TomlHelper.fieldInfoFormat( "\"" + ConfigUtil.toString(valueDefault.getRegistry().getRegistryName()) +
                "\" Difficulty Registry Entry List", valueDefault, "[ \"namespace:entry_name\", ... ]"));
    }

    /** Sets the registry object validator predicate for this field and returns it. */
    public DifficultyRegistryEntryListField<T> setCustomPredicate(@Nullable Predicate<T> predicate) {
        this.registryEntryPredicate = predicate;
        return this;
    }

    /**
     * Loads this field's value from the given value or raw toml. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value and print a warning explaining the change.
     */
    @Override
    public void load(@Nullable Object raw) {
        if(raw == null) {
            value = valueDefault;
            return;
        }

        if(raw instanceof DifficultyRegistryEntryList) {
            try {
                //noinspection unchecked
                value = (DifficultyRegistryEntryList<T>) raw;
            }
            catch (ClassCastException e) {
                ConfigUtil.LOG.warn("Invalid value for {} \"{}\" (wrong registry)! Falling back to default. Invalid value: {}",
                        getClass(), getKey(), raw);
                value = valueDefault;
            }
        }
        else {
            // All the actual loading is done through the objects
            value = new DifficultyRegistryEntryList<>(this, registryEntryPredicate, valueDefault.getRegistry(), TomlHelper.parseStringList(raw));
        }
    }


    // Convenience methods

    /** @return The registry this list draws from. */
    public IForgeRegistry<T> getRegistry() { return get().getRegistry(); }

    /** @return The entries in this list. Does not account for tag or namespace entries! */
    public Set<DifficultyRegListEntry<T>> getEntries() { return get().getEntries(); }

    /** @return Returns true if there are no entries in this list. */
    public boolean isEmpty() { return get().isEmpty(); }

    /**
     * @param difficulty The raw difficulty of a player (not divided by day length).
     *
     * @return The registry entries of the list entry with the closest difficulty level.
     */
    public List<T> getClosestValues(long difficulty) { return get().getClosestValues(getRegistry(), registryEntryPredicate, difficulty); }

    /**
     * @param difficulty The raw difficulty of a player (not divided by day length).
     *
     * @return The registry entries of all list entries with a difficulty level
     *         lower or equal to the given difficulty.
     */
    public List<T> getAllUntil(long difficulty) { return get().getAllUntil(getRegistry(), registryEntryPredicate, difficulty); }
}
