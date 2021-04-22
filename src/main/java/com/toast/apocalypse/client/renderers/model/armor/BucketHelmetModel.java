package com.toast.apocalypse.client.renderers.model.armor;

// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16
//
// Author: Frenderman


import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class BucketHelmetModel extends BipedModel<LivingEntity> {

    private final ModelRenderer rope;
    private final ModelRenderer cube_r1;

    public BucketHelmetModel() {
        super(1.0F);
        this.texWidth = 32;
        this.texHeight = 32;

        this.head = new ModelRenderer(this);
        this.head.setPos(0.0F, 0.0F, 0.0F);
        this.head.texOffs(0, 14).addBox(-4.0F, -13.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.0F, false);

        this.rope = new ModelRenderer(this);
        this.rope.setPos(0.0F, 0.0F, 0.0F);
        this.head.addChild(this.rope);

        this.cube_r1 = new ModelRenderer(this);
        this.cube_r1.setPos(0.0F, -4.0F, 0.0F);
        this.rope.addChild(cube_r1);
        setRotationAngle(cube_r1, -0.3927F, 0.0F, 0.0F);
        this.cube_r1.texOffs(0, 9).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 5.0F, 0.0F, 0.0F, false);
    }

    @Override
    protected Iterable<ModelRenderer> headParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelRenderer> bodyParts() {
        return ImmutableList.of();
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}