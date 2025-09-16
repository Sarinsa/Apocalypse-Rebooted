package com.toast.apocalypse.common.core.config.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.IStringArray;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * A list of entries where a block or block tag is linked to a result block state,
 * optionally copying over state properties. This is a very nichè thing and is
 * primarily used for in-world block transformation.
 */
public class BlockTransformList implements IStringArray {

    /** The entries in this list. */
    private final List<Entry> ENTRIES = new ArrayList<>();

    /**
     * Creates a new block transform list from an array of entries. Used for creating default configs.
     */
    public BlockTransformList(Entry... entries) {
        List<Block> encounteredBlocks = new ArrayList<>();
        List<TagKey<Block>> encounteredTags = new ArrayList<>();

        entryQuery:
        for (Entry entry : entries) {
            // Avoid entries with duplicate inputs, be it tag or block
            if (entry.inputBlock != null && encounteredBlocks.contains(entry.inputBlock))
                continue;
            else encounteredBlocks.add(entry.inputBlock);

            if (entry.inputTag != null) {
                for (TagKey<Block> tag : encounteredTags) {
                    if (tag.toString().equals(entry.inputTag.toString()))
                        continue entryQuery;
                }
                encounteredTags.add(entry.inputTag);
            }
            ENTRIES.add(entry);
        }
    }

    /**
     * Create a new block list from a list of block state strings.
     */
    public BlockTransformList(AbstractConfigField field, List<String> entries) {
        for(String line : entries) {
            String[] components = line.split(" ");

            // Each entry should consist of 3 parts; input, result block state and a boolean.
            // Skip entry if we don't get the expected number of components.
            if (components.length != 3) {
                ConfigUtil.LOG.warn("Invalid format for {} \"{}\"! Skipping entry. Expected 3 components in the entry, but got {}!",
                        field.getClass(), field.getKey(), line);
                continue;
            }
            Block inputBlock = null;
            TagKey<Block> inputTag = null;
            StateProperties resultState;
            boolean copyProperties;

            String inputString = components[0];
            String stateString = components[1];
            String copyPropsString = components[2];

            // Parse input string; should either be a block tag or a block ID
            if (inputString.startsWith("#")) {
                // Get substring after '#' and check if it passes as a valid resource location
                ResourceLocation tagLocation = ResourceLocation.tryParse(inputString.substring(1));

                // Not a valid resource location, outrageous
                if (tagLocation == null) {
                    ConfigUtil.LOG.warn("Invalid input tag key for {} \"{}\"! Skipping entry. Invalid tag key: {}",
                            field.getClass(), field.getKey(), inputString);
                    continue;
                }
                inputTag = BlockTags.create(tagLocation);
            }
            else {
                ResourceLocation blockId = ResourceLocation.tryParse(inputString);
                if (blockId == null || !ForgeRegistries.BLOCKS.containsKey(blockId)) {
                    ConfigUtil.LOG.warn("Invalid input block for {} \"{}\"! Block ID is malformed or does not exist in the registry, skipping entry. Invalid block ID: {}",
                            field.getClass(), field.getKey(), inputString);
                    continue;
                }
                inputBlock = ForgeRegistries.BLOCKS.getValue(blockId);
            }
            // Lazily parsing the string. As long as "true" means true,
            // anything else the user may have typed can be interpreted as false.
            copyProperties = Boolean.parseBoolean(copyPropsString);

            // Try parsing block state
            resultState = parseStateProperties(field, stateString);
            // Failed parsing, skip entry
            if (resultState == null) continue;

            // Success!
            ENTRIES.add(new Entry(inputBlock, inputTag, resultState, copyProperties));
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    private static StateProperties parseStateProperties(AbstractConfigField field, String stateString) {
        String[] stateComponents = stateString.split("\\[", 2);

        if (stateComponents.length != 2) {
            // There is no opening bracket, we can't parse the properties
            ConfigUtil.LOG.warn("Missing opening bracket on block state properties for {} \"{}\"! Block state string is malformed, skipping entry. Invalid block state: {}",
                    field.getClass(), field.getKey(), stateComponents[0]);
            return null;
        }
        ResourceLocation stateBlockId = ResourceLocation.tryParse(stateComponents[0]);

        // Check if the block ID of the block state string is valid and exists in the registry.
        if (stateBlockId == null || !ForgeRegistries.BLOCKS.containsKey(stateBlockId)) {
            ConfigUtil.LOG.warn("Invalid result block state for {} \"{}\"! Block state string is malformed, skipping entry. Invalid block state: {}",
                    field.getClass(), field.getKey(), stateComponents[0]);
            return null;
        }
        if (!stateComponents[1].endsWith("]")) {
            // No ending bracket, skip entry and warn
            ConfigUtil.LOG.warn("Missing closing bracket on block state properties for {} \"{}\"! Skipping entry. Invalid block state string: {}",
                    field.getClass(), field.getKey(), stateString);
            return null;
        }
        // Fetch the state container for the block
        final StateDefinition<Block, BlockState> stateContainer = ForgeRegistries.BLOCKS.getValue(stateBlockId).getStateDefinition();
        String propertiesLine = stateComponents[1].substring(0, stateComponents[1].length() - 1);
        String[] properties = propertiesLine.split(",");

        final Map<Property<?>, Comparable<?>> propertyMap = new HashMap<>();

        for (String propertyString : properties) {
            String[] pair = propertyString.split("=", 2);

            if (pair.length != 2) {
                ConfigUtil.LOG.warn("Invalid property-value pair in block state string for {} \"{}\"! Skipping property. Invalid property: {}",
                        field.getClass(), field.getKey(), propertyString);
                continue;
            }
            // Parse the property key
            final Property<? extends Comparable<?>> property = stateContainer.getProperty(pair[0]);

            if(property == null) {
                // Make a list of valid state keys to give better feedback
                List<Object> propertyNames = new ArrayList<>();

                for (Property<? extends Comparable<?>> allowed : stateContainer.getProperties()) {
                    propertyNames.add(allowed.getName());
                }
                ConfigUtil.LOG.warn("Invalid block property key for {} \"{}\". Valid property keys for '{}' are {}. " +
                                "Deleting property. Invalid property: {}", field.getClass(), field.getKey(),
                        ConfigUtil.toString(stateBlockId), TomlHelper.literalList(propertyNames), propertyString.trim());
                continue;
            }
            // Parse the property value
            final Optional<? extends Comparable<?>> value = property.getValue(pair[1]);

            if(value.isEmpty()) {
                // Make a list of valid property values to give better feedback
                List<Object> valueNames = new ArrayList<>();

                for (Comparable<?> allowed : property.getPossibleValues()) {
                    valueNames.add(property.getName(value(allowed)));
                }
                ConfigUtil.LOG.warn("Invalid block property value for {} \"{}\". Valid values for property '{}' are {}. " +
                                "Deleting property. Invalid property: {}", field.getClass(), field.getKey(), property.getName(),
                        TomlHelper.literalList(valueNames), propertyString.trim());
                continue;
            }
            propertyMap.put(property, value.get());
        }
        return new StateProperties(ForgeRegistries.BLOCKS.getValue(stateBlockId), propertyMap);
    }

    /** A helper method to get the name of a property's value; gets it around the weird generic type issues. */
    private static <T extends Comparable<T>> T value( Comparable<?> allowed ) {
        //noinspection unchecked
        return (T) allowed;
    }

    /** @return A string representation of this object. */
    @Override
    public String toString() { return TomlHelper.toLiteral(toStringList().toArray()); }

    /** @return A list of strings that will represent this object when written to a toml file. */
    @Override
    public List<String> toStringList() {
        // Create a list of the entries in string format
        List<String> list = new ArrayList<>();

        for(Entry entry : ENTRIES) {
            list.add(entry.toString());
        }
        return list;
    }

    /**
     * Checks if the given block state's block has an entry in the list,
     * and returns the appropriate result if so, optionally attempting
     * to copy over block state properties if configured to do so.
     */
    @Nullable
    public BlockState getResultFor(BlockState blockState) {
        for (Entry entry : ENTRIES) {
            if (entry.inputBlock == blockState.getBlock()
                    || (entry.inputTag != null && blockState.is(entry.inputTag))) {

                return entry.stateProperties.withProperties(entry.copyValues ? blockState : null);
            }
        }
        return null;
    }

    /** @return Returns true if there are no entries in this list. */
    public boolean isEmpty() {
        return ENTRIES.isEmpty();
    }

    /**
     * Adds the given entry to the list if there are no existing
     * entries with the same input.
     */
    private void addEntry(Entry transformEntry) {
        for (Entry entry : ENTRIES) {
            // Avoid entries with duplicate inputs, be it tag or block
            if (entry.inputBlock == transformEntry.inputBlock)
                continue;
            if (transformEntry.inputTag != null && entry.inputTag != null) {
                if (transformEntry.inputTag.toString().equals(entry.inputTag.toString()))
                    continue;
            }

            ENTRIES.add(entry);
        }
    }

    /**
     * Contains a block and the desired property values to apply
     * to its default state when fetched.
     */
    public static class StateProperties {

        private final Block block;
        private final Map<Property<?>, Comparable<?>> properties;


        StateProperties(Block block, Map<Property<?>, Comparable<?>> properties) {
            this.block = block;
            this.properties = properties;
        }

        /**
         * @return The default block state of {@link StateProperties#block} with
         *         this StateProperties holder's property values.
         *
         * @param toCopyFrom Optional block state to try and copy properties from
         *                   after we have copied the other properties.
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        protected BlockState withProperties(@Nullable BlockState toCopyFrom) {
            BlockState state = block.defaultBlockState();

            for (Map.Entry<Property<?>, Comparable<?>> property : properties.entrySet()) {
                state = state.trySetValue((Property) property.getKey(), (Comparable) property.getValue());
            }
            if (toCopyFrom != null) {
                for (Property<?> property : toCopyFrom.getProperties()) {
                    if (state.hasProperty(property))
                        state = state.trySetValue((Property) property, (Comparable) toCopyFrom.getValue(property));
                }
            }
            return state;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(ForgeRegistries.BLOCKS.getKey(block).toString());
            builder.append("[");

            for (Map.Entry<Property<?>, Comparable<?>> entry : properties.entrySet()) {
                builder.append(entry.getKey().getName())
                        .append("=")
                        .append(getPropertyName(entry.getKey(), entry.getValue()))
                        .append(",");
            }
            // Make sure we remain any trailing commas
            if (!properties.isEmpty()) {
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append("]");

            return builder.toString();
        }

        /** @return Returns the name of a property value. */
        private <T extends Comparable<T>> String getPropertyName(Property<T> property, Comparable<?> value) {
            //noinspection unchecked
            return property.getName((T) value);
        }

        /** Builder for easily creating instances for default configs. */
        public static class Builder {

            final Block block;
            final StateDefinition<Block, BlockState> stateContainer;
            final Map<Property<?>, Comparable<?>> properties;

            private Builder(@Nonnull Block block) {
                Objects.requireNonNull(block);
                this.block = block;
                this.stateContainer = block.getStateDefinition();
                this.properties = new HashMap<>();
            }

            /** @return A new builder instance with the specified block. */
            public static Builder builder(@Nonnull Block block) {
                return new Builder(block);
            }

            /** @return This builder instance with the given property-value pair. */
            public <T extends Comparable<T>, V extends T> StateProperties.Builder withValue(Property<T> property, V value) {
                Property<?> existingProperty = stateContainer.getProperty(property.getName());

                if (existingProperty == null) {
                    ConfigUtil.LOG.error("Attempted to construct StateProperties builder with an invalid property-value pair for a default config. This is bad!");
                    throw new IllegalArgumentException("StateProperties.Builder#withValue() called for block with a property that does not exist in the state container");
                }
                if (!existingProperty.getPossibleValues().contains(value)) {
                    ConfigUtil.LOG.error("Attempted to construct StateProperties builder with an invalid property-value pair for a default config. This is bad!");
                    throw new IllegalArgumentException("StateProperties.Builder#withValue() called for block with a value that does not exist in the state container");
                }
                properties.put(property, value);
                return this;
            }

            /** Converts this builder into a StateProperties instance. */
            public StateProperties build() {
                return new StateProperties(block, properties);
            }
        }
    }

    /**
     * Represents an entry for this list. Contains either a block input or a tag input,
     * a result block state and a flag for whether block state properties should be copied over for in-world operations.
     * <br><br>
     * Note that both an input block and input tag can be present, but the input block will be prioritized in most operations.
     */
    public record Entry(@Nullable Block inputBlock, @Nullable TagKey<Block> inputTag,
                                 StateProperties stateProperties, boolean copyValues) {

        public Entry(@Nullable Block inputBlock, @Nullable TagKey<Block> inputTag,
                              StateProperties stateProperties, boolean copyValues) {
            this.inputBlock = inputBlock;
            this.inputTag = inputTag;
            this.stateProperties = stateProperties;
            this.copyValues = copyValues;

            if (inputBlock == null && inputTag == null)
                throw new IllegalArgumentException("TransformEntry must either have an input entry or an input tag; both cannot be null!");
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            if (inputBlock == null) {
                builder.append(ConfigUtil.toString(inputTag));
            }
            else {
                builder.append(ConfigUtil.toString(ForgeRegistries.BLOCKS.getKey(inputBlock)));
            }
            builder.append(" ");
            builder.append(stateProperties);
            builder.append(" ");
            builder.append(copyValues);

            return builder.toString();
        }
    }
}
