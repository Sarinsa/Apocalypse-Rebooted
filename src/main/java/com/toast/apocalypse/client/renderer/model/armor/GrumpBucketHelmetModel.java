package com.toast.apocalypse.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.monster.Ghast;

public class GrumpBucketHelmetModel<T extends Ghast> extends EntityModel<T> {
    
    private final ModelPart bucket;
    
    public GrumpBucketHelmetModel( ModelPart root ) {
        super( RenderType::entityCutoutNoCull );
        bucket = root.getChild( "bucket" );
    }
    
    
    /** @return The layer definition for the Bucket Helmet model. */
    public static LayerDefinition createBodyLayer() {
        final MeshDefinition meshDef = new MeshDefinition();
        final PartDefinition root = meshDef.getRoot();
        
        final PartDefinition rope = root.addOrReplaceChild( "rope", CubeListBuilder.create(),
                PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        
        rope.addOrReplaceChild( "cube_r1", CubeListBuilder.create()
                        .texOffs( 0, 9 )
                        .addBox( -5.0F, 0.0F, 0.0F, 10.0F, 5.0F, 0.0F, new CubeDeformation( 0.0F ) ),
                PartPose.offsetAndRotation( 0.0F, -6.0F, 0.0F, -0.3927F, 0.0F, 0.0F ) );
        
        root.addOrReplaceChild( "bucket", CubeListBuilder.create()
                        .texOffs( 0, 14 )
                        .addBox( -4.0F, -20.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation( 4.5F ) ),
                PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        
        return LayerDefinition.create( meshDef, 32, 32 );
    }
    
    /** Called before rendering to set up model part rotations and whatnot. */
    @Override
    public void setupAnim( T grump, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) { }
    
    /** Renders this model. */
    @Override
    public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                                float red, float green, float blue, float alpha ) {
        bucket.render( poseStack, vertexConsumer, packedLight, packedOverlay );
    }
}
