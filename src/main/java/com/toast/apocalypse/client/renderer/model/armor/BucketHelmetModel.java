package com.toast.apocalypse.client.renderer.model.armor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class BucketHelmetModel extends HumanoidModel<LivingEntity> {
    
    private final ModelPart rope;
    private final ModelPart bone;
    
    public BucketHelmetModel( ModelPart playerRoot, ModelPart helmetRoot ) {
        super( playerRoot );
        rope = helmetRoot.getChild( "rope" );
        bone = helmetRoot.getChild( "bone" );
    }
    
    
    /** @return The layer definition for the Bucket Helmet model. */
    public static LayerDefinition createBodyLayer() {
        final MeshDefinition meshDef = new MeshDefinition();
        final PartDefinition root = meshDef.getRoot();
        
        final PartDefinition rope = root.addOrReplaceChild( "rope", CubeListBuilder.create(), PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        rope.addOrReplaceChild( "cube_r1", CubeListBuilder.create().texOffs( 0, 9 ).addBox( -5.0F, 2.0F, 0.0F, 10.0F, 5.0F, 0.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, -6.0F, 0.0F, -0.3927F, 0.0F, 0.0F ) );
        root.addOrReplaceChild( "bone", CubeListBuilder.create().texOffs( 0, 14 ).addBox( -4.0F, -13.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation( 0.25F ) ), PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        
        return LayerDefinition.create( meshDef, 32, 32 );
    }
    
    /**
     * Copies the head rotations and positions of the given humanoid model over to this model.
     *
     * @return Self, after copying over rotations.
     */
    public BucketHelmetModel copyPropsOf( HumanoidModel<?> other ) {
        rope.xRot = other.head.xRot;
        rope.yRot = other.head.yRot;
        rope.x = other.head.x;
        rope.y = other.head.y;
        rope.z = other.head.z;
        bone.xRot = other.head.xRot;
        bone.yRot = other.head.yRot;
        bone.x = other.head.x;
        bone.y = other.head.y;
        bone.z = other.head.z;
        return this;
    }
    
    /** @return An iterable of this model's head model parts. */
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of( rope, bone );
    }
    
    /** @return An iterable of this model's body model parts. */
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of();
    }
    
    /** Called before rendering to set up model part rotations and whatnot. */
    @Override
    public void setupAnim( LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) { }
}