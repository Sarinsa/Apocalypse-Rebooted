package com.toast.apocalypse.client.renderers.entity.living.fearwolf;

import com.google.common.collect.ImmutableList;
import com.toast.apocalypse.common.entity.living.FearwolfEntity;
import net.minecraft.client.renderer.entity.model.TintedAgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * Basically a copy of the vanilla WolfModel.
 */
public class FearwolfModel <T extends FearwolfEntity> extends TintedAgeableModel<T> {

    private final ModelRenderer head;
    private final ModelRenderer realHead;
    private final ModelRenderer body;
    private final ModelRenderer leg0;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer leg3;
    private final ModelRenderer tail;
    private final ModelRenderer realTail;
    private final ModelRenderer upperBody;

    public FearwolfModel() {
        this.head = new ModelRenderer(this, 0, 0);
        this.head.setPos(-1.0F, 13.5F, -7.0F);
        this.realHead = new ModelRenderer(this, 0, 0);
        this.realHead.addBox(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, 0.0F);
        this.head.addChild(this.realHead);
        this.body = new ModelRenderer(this, 18, 14);
        this.body.addBox(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, 0.0F);
        this.body.setPos(0.0F, 14.0F, 2.0F);
        this.upperBody = new ModelRenderer(this, 21, 0);
        this.upperBody.addBox(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, 0.0F);
        this.upperBody.setPos(-1.0F, 14.0F, 2.0F);
        this.leg0 = new ModelRenderer(this, 0, 18);
        this.leg0.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
        this.leg0.setPos(-2.5F, 16.0F, 7.0F);
        this.leg1 = new ModelRenderer(this, 0, 18);
        this.leg1.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
        this.leg1.setPos(0.5F, 16.0F, 7.0F);
        this.leg2 = new ModelRenderer(this, 0, 18);
        this.leg2.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
        this.leg2.setPos(-2.5F, 16.0F, -4.0F);
        this.leg3 = new ModelRenderer(this, 0, 18);
        this.leg3.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
        this.leg3.setPos(0.5F, 16.0F, -4.0F);
        this.tail = new ModelRenderer(this, 9, 18);
        this.tail.setPos(-1.0F, 12.0F, 8.0F);
        this.realTail = new ModelRenderer(this, 9, 18);
        this.realTail.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
        this.tail.addChild(this.realTail);
        this.realHead.texOffs(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
        this.realHead.texOffs(16, 14).addBox(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
        this.realHead.texOffs(0, 10).addBox(-0.5F, 0.0F, -5.0F, 3.0F, 3.0F, 4.0F, 0.0F);
    }

    @Override
    protected Iterable<ModelRenderer> headParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelRenderer> bodyParts() {
        return ImmutableList.of(this.body, this.leg0, this.leg1, this.leg2, this.leg3, this.tail, this.upperBody);
    }

    @Override
    public void prepareMobModel(T fearwolf, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
        this.body.setPos(0.0F, 14.0F, 2.0F);
        this.body.xRot = ((float) Math.PI / 2F);
        this.upperBody.setPos(-1.0F, 14.0F, -3.0F);
        this.upperBody.xRot = this.body.xRot;
        this.tail.setPos(-1.0F, 12.0F, 8.0F);
        this.leg0.setPos(-2.5F, 16.0F, 7.0F);
        this.leg1.setPos(0.5F, 16.0F, 7.0F);
        this.leg2.setPos(-2.5F, 16.0F, -4.0F);
        this.leg3.setPos(0.5F, 16.0F, -4.0F);
        this.leg0.xRot = MathHelper.cos(p_212843_2_ * 0.6662F) * 1.4F * p_212843_3_;
        this.leg1.xRot = MathHelper.cos(p_212843_2_ * 0.6662F + (float) Math.PI) * 1.4F * p_212843_3_;
        this.leg2.xRot = MathHelper.cos(p_212843_2_ * 0.6662F + (float) Math.PI) * 1.4F * p_212843_3_;
        this.leg3.xRot = MathHelper.cos(p_212843_2_ * 0.6662F) * 1.4F * p_212843_3_;
    }

    public void setupAnim(T fearwolf, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        this.head.xRot = p_225597_6_ * ((float) Math.PI / 180F);
        this.head.yRot = p_225597_5_ * ((float) Math.PI / 180F);
    }
}