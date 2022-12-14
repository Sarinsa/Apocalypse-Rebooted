package com.toast.apocalypse.common.tag;

import com.toast.apocalypse.common.core.Apocalypse;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.Tags;

public class ApocalypseEntityTags {

    public static final ITag.INamedTag<EntityType<?>> FLYING_ENTITIES = modTag("flying_entities");


    private static Tags.IOptionalNamedTag<EntityType<?>> modTag(String name) {
        return EntityTypeTags.createOptional(Apocalypse.resourceLoc(name));
    }

    public static void init() {}

    // Utility class, instantiation redundant
    private ApocalypseEntityTags() {}
}
