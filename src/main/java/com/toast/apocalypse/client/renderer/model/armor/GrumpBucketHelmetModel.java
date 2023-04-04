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

    private final ModelPart bone;

    public GrumpBucketHelmetModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition rope = partdefinition.addOrReplaceChild("rope", CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r1 = rope.addOrReplaceChild("cube_r1", CubeListBuilder.create()
                .texOffs(0, 9)
                .addBox(-5.0F, 0.0F, 0.0F, 10.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create()
                .texOffs(0, 14)
                .addBox(-4.0F, -20.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(4.5F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(T grump, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelPart modelPart, float x, float y, float z) {
        modelPart.xRot = x;
        modelPart.yRot = y;
        modelPart.zRot = z;
    }
}
