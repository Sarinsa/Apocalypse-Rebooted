package com.toast.apocalypse.client.renderer.entity.living.fearwolf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.toast.apocalypse.common.entity.living.Fearwolf;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * A modified copy-paste of {@link net.minecraft.client.model.WolfModel}.
 */
public class FearwolfModel<T extends Fearwolf> extends EntityModel<T> {
    
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private final ModelPart upperBody;
    
    
    public FearwolfModel( ModelPart root ) {
        head = root.getChild( "head" );
        body = root.getChild( "body" );
        upperBody = root.getChild( "upper_body" );
        rightHindLeg = root.getChild( "right_hind_leg" );
        leftHindLeg = root.getChild( "left_hind_leg" );
        rightFrontLeg = root.getChild( "right_front_leg" );
        leftFrontLeg = root.getChild( "left_front_leg" );
        tail = root.getChild( "tail" );
    }
    
    /** @return The layer definition for the Fearwolf model. */
    public static LayerDefinition createBodyLayer() {
        final MeshDefinition meshDef = new MeshDefinition();
        final PartDefinition root = meshDef.getRoot();
        
        final PartDefinition head = root.addOrReplaceChild( "head", CubeListBuilder.create(), PartPose.offset( -1.0F, 13.5F, -7.0F ) );
        head.addOrReplaceChild( "real_head", CubeListBuilder.create().texOffs( 0, 0 ).addBox( -2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F ).texOffs( 16, 14 ).addBox( -2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F ).texOffs( 16, 14 ).addBox( 2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F ).texOffs( 0, 10 ).addBox( -0.5F, -0.001F, -5.0F, 3.0F, 3.0F, 4.0F ), PartPose.ZERO );
        
        root.addOrReplaceChild( "body", CubeListBuilder.create().texOffs( 18, 14 ).addBox( -3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F ), PartPose.offsetAndRotation( 0.0F, 14.0F, 2.0F, ((float) Math.PI / 2F), 0.0F, 0.0F ) );
        root.addOrReplaceChild( "upper_body", CubeListBuilder.create().texOffs( 21, 0 ).addBox( -3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F ), PartPose.offsetAndRotation( -1.0F, 14.0F, -3.0F, ((float) Math.PI / 2F), 0.0F, 0.0F ) );
        CubeListBuilder builder = CubeListBuilder.create().texOffs( 0, 18 ).addBox( 0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F );
        root.addOrReplaceChild( "right_hind_leg", builder, PartPose.offset( -2.5F, 16.0F, 7.0F ) );
        root.addOrReplaceChild( "left_hind_leg", builder, PartPose.offset( 0.5F, 16.0F, 7.0F ) );
        root.addOrReplaceChild( "right_front_leg", builder, PartPose.offset( -2.5F, 16.0F, -4.0F ) );
        root.addOrReplaceChild( "left_front_leg", builder, PartPose.offset( 0.5F, 16.0F, -4.0F ) );
        
        final PartDefinition tail = root.addOrReplaceChild( "tail", CubeListBuilder.create(), PartPose.offsetAndRotation( -1.0F, 12.0F, 8.0F, ((float) Math.PI / 5F), 0.0F, 0.0F ) );
        tail.addOrReplaceChild( "real_tail", CubeListBuilder.create().texOffs( 9, 18 ).addBox( 0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F ), PartPose.ZERO );
        
        return LayerDefinition.create( meshDef, 64, 32 );
    }
    
    /** Called before rendering to set up misc model properties. */
    @Override
    public void prepareMobModel( T fearwolf, float position, float speed, float partialTick ) {
        tail.yRot = 0.0F;
        
        body.setPos( 0.0F, 14.0F, 2.0F );
        body.xRot = ((float) Math.PI / 2F);
        upperBody.setPos( -1.0F, 14.0F, -3.0F );
        upperBody.xRot = body.xRot;
        tail.setPos( -1.0F, 12.0F, 8.0F );
        rightHindLeg.setPos( -2.5F, 16.0F, 7.0F );
        leftHindLeg.setPos( 0.5F, 16.0F, 7.0F );
        rightFrontLeg.setPos( -2.5F, 16.0F, -4.0F );
        leftFrontLeg.setPos( 0.5F, 16.0F, -4.0F );
        rightHindLeg.xRot = Mth.cos( position * 0.6662F ) * 1.4F * speed;
        leftHindLeg.xRot = Mth.cos( position * 0.6662F + (float) Math.PI ) * 1.4F * speed;
        rightFrontLeg.xRot = Mth.cos( position * 0.6662F + (float) Math.PI ) * 1.4F * speed;
        leftFrontLeg.xRot = Mth.cos( position * 0.6662F ) * 1.4F * speed;
    }
    
    /** Called before rendering to set up model part rotations and whatnot. */
    @Override
    public void setupAnim( T fearwolf, float limbSwing, float limbSwingAmount, float partialTick, float netHeadYaw, float headPitch ) {
        head.xRot = headPitch * ((float) Math.PI / 180F);
        head.yRot = netHeadYaw * ((float) Math.PI / 180F);
    }
    
    /** Renders this model. */
    @Override
    public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                                float red, float green, float blue, float alpha ) {
        head.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        body.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        rightHindLeg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        leftHindLeg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        rightFrontLeg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        leftFrontLeg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        tail.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        upperBody.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
    }
}