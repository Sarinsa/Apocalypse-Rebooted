package com.toast.apocalypse.common.tag;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ApocalypseItemTags {
    
    public static final TagKey<Item> COOKIES = forgeTag( "cookies" );
    
    
    private static TagKey<Item> modTag( String name ) {
        return create( Apocalypse.resourceLoc( name ) );
    }
    
    private static TagKey<Item> forgeTag( String name ) {
        return create( new ResourceLocation( "forge", name ) );
    }
    
    private static TagKey<Item> create( ResourceLocation id ) {
        return ItemTags.create( id );
    }
    
    // Utility class, instantiation redundant
    private ApocalypseItemTags() { }
}
