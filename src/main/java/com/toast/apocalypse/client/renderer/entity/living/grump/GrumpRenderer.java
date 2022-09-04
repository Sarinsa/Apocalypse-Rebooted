package com.toast.apocalypse.client.renderer.entity.living.grump;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import com.toast.apocalypse.common.entity.living.GrumpEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.GhastModel;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

public class GrumpRenderer<T extends GrumpEntity> extends MobRenderer<T, GhastModel<T>> {

    private static final ResourceLocation[] GRUMP_TEXTURES = new ResourceLocation[] {
            Apocalypse.resourceLoc("textures/entity/grump/grump.png"),
            Apocalypse.resourceLoc("textures/entity/grump/saddled_grump.png")
    };

    private static final ResourceLocation[] TAMED_GRUMP_TEXTURES = new ResourceLocation[] {
            Apocalypse.resourceLoc("textures/entity/grump/chill_grump.png"),
            Apocalypse.resourceLoc("textures/entity/grump/saddled_chill_grump.png")
    };


    public GrumpRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new GhastModel<>(), 0.5F);
        this.addLayer(new GrumpBucketHelmetLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(T grump) {
        boolean saddled = grump.getHeadItem().getItem() == Items.SADDLE;

        if (grump.getOwnerUUID() == null) {
            return saddled ? GRUMP_TEXTURES[1] : GRUMP_TEXTURES[0];
        }
        return saddled ? TAMED_GRUMP_TEXTURES[1] : TAMED_GRUMP_TEXTURES[0];
    }
}
