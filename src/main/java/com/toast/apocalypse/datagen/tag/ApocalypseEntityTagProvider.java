package com.toast.apocalypse.datagen.tag;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseEntities;
import com.toast.apocalypse.common.tag.ApocalypseEntityTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.EntityTypeTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class ApocalypseEntityTagProvider extends EntityTypeTagsProvider {

    public ApocalypseEntityTagProvider(DataGenerator dataGenerator, @Nullable ExistingFileHelper fileHelper) {
        super(dataGenerator, Apocalypse.MODID, fileHelper);
    }

    @Override
    protected void addTags() {
        tag(ApocalypseEntityTags.FLYING_ENTITIES).add(
                ApocalypseEntities.DESTROYER.get(),
                ApocalypseEntities.SEEKER.get(),
                ApocalypseEntities.GHOST.get(),
                ApocalypseEntities.GRUMP.get()
        );
    }
}
