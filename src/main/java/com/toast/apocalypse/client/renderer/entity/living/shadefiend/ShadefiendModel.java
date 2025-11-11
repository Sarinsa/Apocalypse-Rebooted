package com.toast.apocalypse.client.renderer.entity.living.shadefiend;

import com.toast.apocalypse.client.ApocalypseRenderTypes;
import com.toast.apocalypse.common.entity.living.Shadefiend;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.util.Mth;

public class ShadefiendModel extends HierarchicalModel<Shadefiend> {
    
    private final ModelPart root;
    private final ModelPart leftWingBase;
    private final ModelPart leftWingTip;
    private final ModelPart rightWingBase;
    private final ModelPart rightWingTip;
    private final ModelPart tailBase;
    private final ModelPart tailTip;
    
    public ShadefiendModel( ModelPart root ) {
        this.root = root;
        renderType = ( resourceLocation ) -> ApocalypseRenderTypes.entityCutoutNoCullBlend( resourceLocation, RenderStateShard.TransparencyStateShard.ADDITIVE_TRANSPARENCY );
        
        ModelPart body = root.getChild( "body" );
        
        tailBase = body.getChild( "tail_base" );
        tailTip = tailBase.getChild( "tail_tip" );
        leftWingBase = body.getChild( "left_wing_base" );
        leftWingTip = leftWingBase.getChild( "left_wing_tip" );
        rightWingBase = body.getChild( "right_wing_base" );
        rightWingTip = rightWingBase.getChild( "right_wing_tip" );
    }
    
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild( "body", CubeListBuilder.create().texOffs( 0, 8 ).addBox( -3.0F, -2.0F, -8.0F, 5.0F, 3.0F, 9.0F ), PartPose.rotation( -0.1F, 0.0F, 0.0F ) );
        PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild( "tail_base", CubeListBuilder.create().texOffs( 3, 20 ).addBox( -2.0F, 0.0F, 0.0F, 3.0F, 2.0F, 6.0F ), PartPose.offset( 0.0F, -2.0F, 1.0F ) );
        partdefinition2.addOrReplaceChild( "tail_tip", CubeListBuilder.create().texOffs( 4, 29 ).addBox( -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 6.0F ), PartPose.offset( 0.0F, 0.5F, 6.0F ) );
        PartDefinition partdefinition3 = partdefinition1.addOrReplaceChild( "left_wing_base", CubeListBuilder.create().texOffs( 23, 12 ).addBox( 0.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F ), PartPose.offsetAndRotation( 2.0F, -2.0F, -8.0F, 0.0F, 0.0F, 0.1F ) );
        partdefinition3.addOrReplaceChild( "left_wing_tip", CubeListBuilder.create().texOffs( 16, 24 ).addBox( 0.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F ), PartPose.offsetAndRotation( 6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1F ) );
        PartDefinition partdefinition4 = partdefinition1.addOrReplaceChild( "right_wing_base", CubeListBuilder.create().texOffs( 23, 12 ).mirror().addBox( -6.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F ), PartPose.offsetAndRotation( -3.0F, -2.0F, -8.0F, 0.0F, 0.0F, -0.1F ) );
        partdefinition4.addOrReplaceChild( "right_wing_tip", CubeListBuilder.create().texOffs( 16, 24 ).mirror().addBox( -13.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F ), PartPose.offsetAndRotation( -6.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1F ) );
        partdefinition1.addOrReplaceChild( "head", CubeListBuilder.create().texOffs( 0, 0 ).addBox( -4.0F, -2.0F, -5.0F, 7.0F, 3.0F, 5.0F ), PartPose.offsetAndRotation( 0.0F, 1.0F, -7.0F, 0.2F, 0.0F, 0.0F ) );
        
        return LayerDefinition.create( meshdefinition, 64, 64 );
    }
    
    @Override
    public ModelPart root() {
        return this.root;
    }
    
    @Override
    public void setupAnim( Shadefiend shadefiend, float limbSwing, float limbSwingAmount, float partialTick, float netHeadYaw, float headPitch ) {
        if( shadefiend.isAggressive() ) {
            float f = (((float) shadefiend.getId() * 3) + partialTick) * 7.448451F * ((float) Math.PI / 180F);
            
            leftWingBase.zRot = Mth.cos( f ) * 16.0F * ((float) Math.PI / 180F);
            leftWingTip.zRot = Mth.cos( f ) * 16.0F * ((float) Math.PI / 180F);
            rightWingBase.zRot = -leftWingBase.zRot;
            rightWingTip.zRot = -leftWingTip.zRot;
            tailBase.xRot = -(5.0F + Mth.cos( f * 2.0F ) * 5.0F) * ((float) Math.PI / 180F);
            tailTip.xRot = -(5.0F + Mth.cos( f * 2.0F ) * 5.0F) * ((float) Math.PI / 180F);
        }
        else {
            leftWingBase.zRot = 0.0F;
            leftWingTip.zRot = 0.0F;
            rightWingBase.zRot = 0.0F;
            rightWingTip.zRot = 0.0F;
            tailBase.xRot = 0.0F;
            tailTip.xRot = 0.0F;
        }
    }
}
