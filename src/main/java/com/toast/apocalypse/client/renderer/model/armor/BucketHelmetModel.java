package com.toast.apocalypse.client.renderer.model.armor;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.toast.apocalypse.client.ApocalypseModelLayers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;

public class BucketHelmetModel extends HumanoidModel<LivingEntity> {

    private final ModelPart rope;
    private final ModelPart bone;

    public BucketHelmetModel(ModelPart playerRoot, ModelPart helmetRoot) {
        super(playerRoot);
        rope = helmetRoot.getChild("rope");
        bone = helmetRoot.getChild("bone");
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition rope = partdefinition.addOrReplaceChild("rope", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r1 = rope.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 9).addBox(-5.0F, 2.0F, 0.0F, 10.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 14).addBox(-4.0F, -13.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    // Copy over head model rotations and positions here because weirdness.
    // I miss the old days.
    public BucketHelmetModel copyProps(HumanoidModel<?> humanoidModel) {
        rope.xRot = humanoidModel.head.xRot;
        rope.yRot = humanoidModel.head.yRot;
        rope.x = humanoidModel.head.x;
        rope.y = humanoidModel.head.y;
        rope.z = humanoidModel.head.z;
        bone.xRot = humanoidModel.head.xRot;
        bone.yRot = humanoidModel.head.yRot;
        bone.x = humanoidModel.head.x;
        bone.y = humanoidModel.head.y;
        bone.z = humanoidModel.head.z;
        return this;
    }

    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(rope, bone);
    }

    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of();
    }

    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}