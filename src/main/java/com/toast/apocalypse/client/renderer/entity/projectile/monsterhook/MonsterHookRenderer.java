package com.toast.apocalypse.client.renderer.entity.projectile.monsterhook;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.projectile.MonsterFishHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/** Copied from FishRenderer */
public class MonsterHookRenderer extends EntityRenderer<MonsterFishHook> {

    private static final ResourceLocation TEXTURE = Apocalypse.resourceLoc("textures/entity/projectile/monster_hook.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE);

    public MonsterHookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MonsterFishHook fishHook, float p_225623_2_, float p_225623_3_, PoseStack poseStack, MultiBufferSource buffer, int p_225623_6_) {
        LivingEntity livingEntity = fishHook.getLivingOwner();

        if (livingEntity != null) {
            poseStack.pushPose();
            poseStack.pushPose();
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();
            VertexConsumer vertexConsumer = buffer.getBuffer(RENDER_TYPE);
            vertex(vertexConsumer, matrix4f, matrix3f, p_225623_6_, 0.0F, 0, 0, 1);
            vertex(vertexConsumer, matrix4f, matrix3f, p_225623_6_, 1.0F, 0, 1, 1);
            vertex(vertexConsumer, matrix4f, matrix3f, p_225623_6_, 1.0F, 1, 1, 0);
            vertex(vertexConsumer, matrix4f, matrix3f, p_225623_6_, 0.0F, 1, 0, 0);
            poseStack.popPose();
            int i = livingEntity.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;

            float f = livingEntity.getAttackAnim(p_225623_3_);
            float f1 = Mth.sin(Mth.sqrt(f) * (float)Math.PI);
            double d4;
            double d5;
            double d6;
            float f3;

            if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson()) && livingEntity == Minecraft.getInstance().player) {
                double fov = this.entityRenderDispatcher.options.fov().get();
                fov = fov / 100.0D;
                Vec3 vec3 = new Vec3((double)i * -0.36D * fov, -0.045D * fov, 0.4D);
                vec3 = vec3.xRot(-Mth.lerp(p_225623_3_, livingEntity.xRotO, livingEntity.getXRot()) * ((float)Math.PI / 180F));
                vec3 = vec3.yRot(-Mth.lerp(p_225623_3_, livingEntity.yRotO, livingEntity.getYRot()) * ((float)Math.PI / 180F));
                vec3 = vec3.yRot(f1 * 0.5F);
                vec3 = vec3.xRot(-f1 * 0.7F);
                d4 = Mth.lerp(p_225623_3_, livingEntity.xo, livingEntity.getX()) + vec3.x;
                d5 = Mth.lerp(p_225623_3_, livingEntity.yo, livingEntity.getY()) + vec3.y;
                d6 = Mth.lerp(p_225623_3_, livingEntity.zo, livingEntity.getZ()) + vec3.z;
                f3 = livingEntity.getEyeHeight();
            } else {
                d4 = Mth.lerp(p_225623_3_, livingEntity.xo, livingEntity.getX());
                d5 = livingEntity.yo + (double)livingEntity.getEyeHeight() + (livingEntity.getY() - livingEntity.yo) * (double)p_225623_3_ - 0.45D;
                d6 = Mth.lerp(p_225623_3_, livingEntity.zo, livingEntity.getZ());
                f3 = livingEntity.isCrouching() ? -0.1875F : 0.0F;
            }

            double d9 = Mth.lerp(p_225623_3_, fishHook.xo, fishHook.getX());
            double d10 = Mth.lerp(p_225623_3_, fishHook.yo, fishHook.getY()) + 0.25D;
            double d8 = Mth.lerp(p_225623_3_, fishHook.zo, fishHook.getZ());
            float f4 = (float)(d4 - d9);
            float f5 = (float)(d5 - d10) + f3;
            float f6 = (float)(d6 - d8);
            VertexConsumer vertexConsumer1 = buffer.getBuffer(RenderType.lines());
            Matrix4f matrix4f1 = poseStack.last().pose();
            int j = 16;

            for(int k = 0; k < 16; ++k) {
                stringVertex(f4, f5, f6, vertexConsumer1, matrix4f1, fraction(k, 16));
                stringVertex(f4, f5, f6, vertexConsumer1, matrix4f1, fraction(k + 1, 16));
            }
            poseStack.popPose();
            super.render(fishHook, p_225623_2_, p_225623_3_, poseStack, buffer, p_225623_6_);
        }
    }

    private static float fraction(int p_229105_0_, int p_229105_1_) {
        return (float)p_229105_0_ / (float)p_229105_1_;
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, int p_229106_3_, float p_229106_4_, int p_229106_5_, int p_229106_6_, int p_229106_7_) {
        vertexConsumer.vertex(matrix4f, p_229106_4_ - 0.5F, (float)p_229106_5_ - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float)p_229106_6_, (float)p_229106_7_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_229106_3_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void stringVertex(float p_229104_0_, float p_229104_1_, float p_229104_2_, VertexConsumer vertexConsumer, Matrix4f matrix4f, float p_229104_5_) {
        vertexConsumer.vertex(matrix4f, p_229104_0_ * p_229104_5_, p_229104_1_ * (p_229104_5_ * p_229104_5_ + p_229104_5_) * 0.5F + 0.25F, p_229104_2_ * p_229104_5_).color(0, 0, 0, 255).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(MonsterFishHook fishHook) {
        return TEXTURE;
    }
}
