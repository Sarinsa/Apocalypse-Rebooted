package com.toast.apocalypse.client.renderers.entity.ghost;

import com.google.common.collect.ImmutableList;
import com.toast.apocalypse.common.entity.GhostEntity;
import net.minecraft.client.renderer.entity.model.BlazeModel;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class GhostModel<T extends GhostEntity> extends SegmentedModel<T> {

    private final ModelRenderer head;
    private final ModelRenderer body;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;

    public GhostModel() {
        this.head = new ModelRenderer(this, 0, 0).addBox(-2.0F, -10.0F, -2.0F, 4, 10, 4);
        this.body = new ModelRenderer(this, 16, 10).addBox(-4.0F, 0.0F, -2.0F, 8, 18, 4);
        this.rightArm = new ModelRenderer(this, 0, 16).addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4);
        this.rightArm.setPos(-5.0F, 2.0F, 0.0F);
        this.leftArm = new ModelRenderer(this, 0, 16).addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4);
        this.leftArm.setPos(5.0F, 2.0F, 0.0F);
        this.leftArm.mirror = true;
    }

    @Override
    public void setupAnim(T ghostEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.head.yRot = headYaw * (float) Math.PI / 180.0F;
        this.head.xRot = headPitch * (float) Math.PI / 180.0F;
        this.leftArm.xRot = -(float) Math.PI / 2.1F;
        this.rightArm.xRot = this.leftArm.xRot;
    }

    @Override
    public Iterable<ModelRenderer> parts() {
        return ImmutableList.of(this.head, this.body, this.rightArm, this.leftArm);
    }
}
