package com.toast.apocalypse.client.renderer.entity.living.grump;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.toast.apocalypse.api.MethodsReturnNonnullByDefault;
import com.toast.apocalypse.client.renderer.model.armor.GrumpBucketHelmetModel;
import com.toast.apocalypse.common.core.register.ApocalypseItems;
import com.toast.apocalypse.common.entity.living.GrumpEntity;
import com.toast.apocalypse.common.item.BucketHelmetItem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.GhastModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class GrumpBucketHelmetLayer<T extends GrumpEntity, M extends GhastModel<T>> extends LayerRenderer<T, M> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(BucketHelmetItem.TEXTURE);
    private final GrumpBucketHelmetModel<T> bucketModel = new GrumpBucketHelmetModel<>();

    public GrumpBucketHelmetLayer(IEntityRenderer<T, M> entityRenderer) {
        super(entityRenderer);
    }


    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T grump, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack headStack = grump.getHeadItem();

        if (!headStack.isEmpty() && headStack.getItem() == ApocalypseItems.BUCKET_HELM.get()) {
            boolean hasFoil = headStack.hasFoil();

            IVertexBuilder vertexBuilder = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(TEXTURE), false, hasFoil);
            this.bucketModel.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
