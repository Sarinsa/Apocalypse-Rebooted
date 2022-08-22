package com.toast.apocalypse.client.renderer.entity.living.ghost;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.toast.apocalypse.client.ApocalypseRenderTypes;
import com.toast.apocalypse.common.entity.living.GhostEntity;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16
//
// Model author: Frenderman

public class GhostModel<T extends GhostEntity> extends SegmentedModel<T> {

    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer skull;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;

    public GhostModel() {
        super((resourceLocation) -> ApocalypseRenderTypes.entityCutoutNoCullBlend(resourceLocation, ApocalypseRenderTypes.GHOST_ALPHA));
        texWidth = 64;
        texHeight = 64;

        body = new ModelRenderer(this);
        body.setPos(0.0F, 4.0F, 0.0F);
        setRotationAngle(body, 0.3054F, 0.0F, 0.0F);
        body.texOffs(20, 41).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 19.0F, 4.0F, 0.0F, false);

        head = new ModelRenderer(this);
        head.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(head);


        skull = new ModelRenderer(this);
        skull.setPos(0.0F, 0.0F, 0.0F);
        head.addChild(skull);
        setRotationAngle(skull, -0.2182F, 0.0F, 0.0F);
        skull.texOffs(0, 0).addBox(-5.0F, -13.0F, -4.0F, 10.0F, 13.0F, 8.0F, 0.0F, false);

        rightArm = new ModelRenderer(this);
        rightArm.setPos(-4.0F, 2.0F, 0.0F);
        body.addChild(rightArm);
        setRotationAngle(rightArm, -1.7453F, 0.2618F, 0.0F);
        rightArm.texOffs(0, 32).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        leftArm = new ModelRenderer(this);
        leftArm.setPos(4.0F, 2.0F, 0.0F);
        body.addChild(leftArm);
        setRotationAngle(leftArm, -1.7453F, -0.2618F, 0.0F);
        leftArm.texOffs(0, 32).addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, true);
    }

    @Override
    public Iterable<ModelRenderer> parts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public void setupAnim(T ghost, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch){
        this.head.yRot = headYaw * (float) Math.PI / 180.0F;
        this.head.xRot = headPitch * (float) Math.PI / 180.0F;
        bobArmsAndHead(this.rightArm, this.leftArm, this.head, ageInTicks);
    }

    private static void bobArmsAndHead(ModelRenderer rightArm, ModelRenderer leftArm, ModelRenderer head, float ageInTicks) {
        float f = MathHelper.sin(1 * (float)Math.PI);
        float f1 = MathHelper.sin((float) Math.PI);
        leftArm.zRot = 0.0F;
        rightArm.zRot = 0.0F;
        leftArm.yRot = -(0.1F - f * 0.6F);
        rightArm.yRot = 0.1F - f * 0.6F;
        float f2 = -(float)Math.PI / 2.25F;
        leftArm.xRot = f2;
        rightArm.xRot = f2;
        leftArm.xRot += f * 1.2F - f1 * 0.4F;
        rightArm.xRot += f * 1.2F - f1 * 0.4F;
        ModelHelper.bobArms(rightArm, leftArm, ageInTicks);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
