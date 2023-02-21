package com.toast.apocalypse.client.renderer.entity.living.fearwolf;

import com.google.common.collect.ImmutableList;
import com.toast.apocalypse.common.entity.living.Fearwolf;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/**
 * Basically a copy of the vanilla WolfModel.
 */
public class FearwolfModel <T extends Fearwolf> extends HierarchicalModel<T> {

    /*
    private final ModelPart head;
    private final ModelPart realHead;
    private final ModelPart body;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart tail;
    private final ModelPart realTail;
    private final ModelPart upperBody;

     */

    public FearwolfModel() {
        /*
        head = new ModelPart(this, 0, 0);
        head.setPos(-1.0F, 13.5F, -7.0F);
        realHead = new ModelRenderer(this, 0, 0);
        realHead.addBox(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, 0.0F);
        head.addChild(realHead);
        body = new ModelRenderer(this, 18, 14);
        body.addBox(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, 0.0F);
        body.setPos(0.0F, 14.0F, 2.0F);
        upperBody = new ModelRenderer(this, 21, 0);
        upperBody.addBox(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, 0.0F);
        upperBody.setPos(-1.0F, 14.0F, 2.0F);
        leg0 = new ModelRenderer(this, 0, 18);
        leg0.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
        leg0.setPos(-2.5F, 16.0F, 7.0F);
        leg1 = new ModelRenderer(this, 0, 18);
        leg1.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
        leg1.setPos(0.5F, 16.0F, 7.0F);
        leg2 = new ModelRenderer(this, 0, 18);
        leg2.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
        leg2.setPos(-2.5F, 16.0F, -4.0F);
        leg3 = new ModelRenderer(this, 0, 18);
        leg3.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
        leg3.setPos(0.5F, 16.0F, -4.0F);
        tail = new ModelRenderer(this, 9, 18);
        tail.setPos(-1.0F, 12.0F, 8.0F);
        realTail = new ModelRenderer(this, 9, 18);
        realTail.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
        tail.addChild(this.realTail);
        realHead.texOffs(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
        realHead.texOffs(16, 14).addBox(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
        realHead.texOffs(0, 10).addBox(-0.5F, 0.0F, -5.0F, 3.0F, 3.0F, 4.0F, 0.0F);

         */
    }

    @Override
    public ModelPart root() {
        //return body;
        return null;
    }


    /*
    @Override
    public void prepareMobModel(T fearwolf, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
        body.setPos(0.0F, 14.0F, 2.0F);
        body.xRot = ((float) Math.PI / 2F);
        upperBody.setPos(-1.0F, 14.0F, -3.0F);
        upperBody.xRot = body.xRot;
        tail.setPos(-1.0F, 12.0F, 8.0F);
        leg0.setPos(-2.5F, 16.0F, 7.0F);
        leg1.setPos(0.5F, 16.0F, 7.0F);
        leg2.setPos(-2.5F, 16.0F, -4.0F);
        leg3.setPos(0.5F, 16.0F, -4.0F);
        leg0.xRot = Mth.cos(p_212843_2_ * 0.4662F) * 1.1F * p_212843_3_;
        leg1.xRot = Mth.cos(p_212843_2_ * 0.4662F + (float) Math.PI) * 1.1F * p_212843_3_;
        leg2.xRot = Mth.cos(p_212843_2_ * 0.4662F + (float) Math.PI) * 1.1F * p_212843_3_;
        leg3.xRot = Mth.cos(p_212843_2_ * 0.4662F) * 1.1F * p_212843_3_;
        tail.xRot = -35.0F;
    }
    */

    public void setupAnim(T fearwolf, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        //this.head.xRot = headPitch * ((float) Math.PI / 180F);
        //this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
    }
}