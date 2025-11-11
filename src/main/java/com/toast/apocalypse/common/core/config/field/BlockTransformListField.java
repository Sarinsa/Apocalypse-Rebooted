package com.toast.apocalypse.common.core.config.field;

import com.toast.apocalypse.common.core.config.value.BlockTransformList;
import fathertoast.crust.api.config.common.field.GenericField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a block transform list value.
 */
public class BlockTransformListField extends GenericField<BlockTransformList> {
    
    /** Provides a detailed description of how to use a block transform list. Recommended putting at the top of any file using block transform lists. */
    public static List<String> verboseDescription() {
        List<String> comment = new ArrayList<>();
        comment.add( "Block Transform List fields: General format = [ \"namespace:block_name|#namespace:tag_name namespace:block_name[property1=value1,...] true|false\", ... ]" );
        comment.add( "  Block transform lists are arrays of blocks linked to a resulting block state and a flag for optionally copying " +
                "properties from the input block over to the resulting block state." );
        comment.add( "  Blocks are defined by their key in the block registry, usually following the pattern " +
                "'namespace:block_name'." );
        comment.add( "  A block tag can be used as input instead of a block ID here. To declare a tag, start with a '#' followed by the rest of the tag path." );
        comment.add( "  Tag example: '#minecraft:beehive_inhabitors'" );
        comment.add( "  List entries can have blank state properties (types as []) if you want the default block state as a result. " +
                "The block states to match can be narrowed down " +
                "by specifying properties. The syntax for block state properties is the same as for commands. Any " +
                "properties not specified will match any value. For example, 'minecraft:beehive[honey_level=5]' will " +
                "match any full beehives, regardless of the direction they face." );
        return comment;
    }
    
    /** Creates a new field. */
    public BlockTransformListField( String key, BlockTransformList defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( "Block Transform List", valueDefault,
                "[ \"namespace:block_name|#namespace:tag_name namespace:block_name[properties] true|false\", ... ]" ) );
    }
    
    /**
     * Loads this field's value from the given value or raw toml. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value and print a warning explaining the change.
     */
    @Override
    public void load( @Nullable Object raw ) {
        if( raw == null ) {
            value = valueDefault;
            return;
        }
        if( raw instanceof BlockTransformList ) {
            value = (BlockTransformList) raw;
        }
        else {
            // All the actual loading is done through the objects
            value = new BlockTransformList( this, TomlHelper.parseStringList( raw ) );
        }
    }
    
    // Convenience methods
    
    /**
     * @return Returns true if there are no entries in this block transform list.
     */
    public boolean isEmpty() { return get().isEmpty(); }
    
    /**
     * @return Returns the resulting block state from the input block state.
     * Returns null if the transform list has no entries for the input state.
     */
    @Nullable
    public BlockState getResultFor( BlockState blockState ) { return get().getResultFor( blockState ); }
}
