package com.toast.apocalypse.client.renderer.entity.living.grump;

import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.living.Grump;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class GrumpRenderer<T extends Grump> extends MobRenderer<T, GhastModel<T>> {

    private static final ResourceLocation[] GRUMP_TEXTURES = new ResourceLocation[] {
            Apocalypse.resourceLoc("textures/entity/grump/grump.png"),
            Apocalypse.resourceLoc("textures/entity/grump/saddled_grump.png")
    };

    private static final ResourceLocation[] TAMED_GRUMP_TEXTURES = new ResourceLocation[] {
            Apocalypse.resourceLoc("textures/entity/grump/chill_grump.png"),
            Apocalypse.resourceLoc("textures/entity/grump/saddled_chill_grump.png")
    };

    private static final ResourceLocation[] POUTING_GRUMP_TEXTURES = new ResourceLocation[] {
            Apocalypse.resourceLoc("textures/entity/grump/pouting_grump.png"),
            Apocalypse.resourceLoc("textures/entity/grump/saddled_pouting_grump.png")
    };


    public GrumpRenderer(EntityRendererProvider.Context context) {
        super(context, new GhastModel<>(context.bakeLayer(ModelLayers.GHAST)), 0.5F);
        this.addLayer(new GrumpBucketHelmetLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(T grump) {
        boolean saddled = grump.getHeadItem().getItem() == Items.SADDLE;

        if (grump.getTarget() != null || grump.getOwnerUUID() == null) {
            return saddled ? GRUMP_TEXTURES[1] : GRUMP_TEXTURES[0];
        }
        if (grump.shouldStandBy()) {
            return saddled ? POUTING_GRUMP_TEXTURES[1] : POUTING_GRUMP_TEXTURES[0];
        }
        return saddled ? TAMED_GRUMP_TEXTURES[1] : TAMED_GRUMP_TEXTURES[0];
    }
}
