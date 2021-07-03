package com.toast.apocalypse.client.renderers.entity.projectile.monsterhook;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.toast.apocalypse.common.core.Apocalypse;
import com.toast.apocalypse.common.entity.projectile.MonsterFishHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.FishRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

/** Copied from FishRenderer */
public class MonsterHookRenderer extends EntityRenderer<MonsterFishHook> {

    private static final ResourceLocation TEXTURE = Apocalypse.resourceLoc("textures/entity/projectile/monster_hook.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE);

    public MonsterHookRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(MonsterFishHook fishHook, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer buffer, int p_225623_6_) {
        LivingEntity livingEntity = fishHook.getLivingOwner();

        if (livingEntity != null) {
            matrixStack.pushPose();
            matrixStack.pushPose();
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            MatrixStack.Entry matrixstack$entry = matrixStack.last();
            Matrix4f matrix4f = matrixstack$entry.pose();
            Matrix3f matrix3f = matrixstack$entry.normal();
            IVertexBuilder ivertexbuilder = buffer.getBuffer(RENDER_TYPE);
            vertex(ivertexbuilder, matrix4f, matrix3f, p_225623_6_, 0.0F, 0, 0, 1);
            vertex(ivertexbuilder, matrix4f, matrix3f, p_225623_6_, 1.0F, 0, 1, 1);
            vertex(ivertexbuilder, matrix4f, matrix3f, p_225623_6_, 1.0F, 1, 1, 0);
            vertex(ivertexbuilder, matrix4f, matrix3f, p_225623_6_, 0.0F, 1, 0, 0);
            matrixStack.popPose();
            int i = livingEntity.getMainArm() == HandSide.RIGHT ? 1 : -1;

            float f = livingEntity.getAttackAnim(p_225623_3_);
            float f1 = MathHelper.sin(MathHelper.sqrt(f) * (float)Math.PI);
            double d4;
            double d5;
            double d6;
            float f3;

            if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson()) && livingEntity == Minecraft.getInstance().player) {
                double d7 = this.entityRenderDispatcher.options.fov;
                d7 = d7 / 100.0D;
                Vector3d vector3d = new Vector3d((double)i * -0.36D * d7, -0.045D * d7, 0.4D);
                vector3d = vector3d.xRot(-MathHelper.lerp(p_225623_3_, livingEntity.xRotO, livingEntity.xRot) * ((float)Math.PI / 180F));
                vector3d = vector3d.yRot(-MathHelper.lerp(p_225623_3_, livingEntity.yRotO, livingEntity.yRot) * ((float)Math.PI / 180F));
                vector3d = vector3d.yRot(f1 * 0.5F);
                vector3d = vector3d.xRot(-f1 * 0.7F);
                d4 = MathHelper.lerp(p_225623_3_, livingEntity.xo, livingEntity.getX()) + vector3d.x;
                d5 = MathHelper.lerp(p_225623_3_, livingEntity.yo, livingEntity.getY()) + vector3d.y;
                d6 = MathHelper.lerp(p_225623_3_, livingEntity.zo, livingEntity.getZ()) + vector3d.z;
                f3 = livingEntity.getEyeHeight();
            } else {
                d4 = MathHelper.lerp(p_225623_3_, livingEntity.xo, livingEntity.getX());
                d5 = livingEntity.yo + (double)livingEntity.getEyeHeight() + (livingEntity.getY() - livingEntity.yo) * (double)p_225623_3_ - 0.45D;
                d6 = MathHelper.lerp(p_225623_3_, livingEntity.zo, livingEntity.getZ());
                f3 = livingEntity.isCrouching() ? -0.1875F : 0.0F;
            }

            double d9 = MathHelper.lerp(p_225623_3_, fishHook.xo, fishHook.getX());
            double d10 = MathHelper.lerp(p_225623_3_, fishHook.yo, fishHook.getY()) + 0.25D;
            double d8 = MathHelper.lerp(p_225623_3_, fishHook.zo, fishHook.getZ());
            float f4 = (float)(d4 - d9);
            float f5 = (float)(d5 - d10) + f3;
            float f6 = (float)(d6 - d8);
            IVertexBuilder ivertexbuilder1 = buffer.getBuffer(RenderType.lines());
            Matrix4f matrix4f1 = matrixStack.last().pose();
            int j = 16;

            for(int k = 0; k < 16; ++k) {
                stringVertex(f4, f5, f6, ivertexbuilder1, matrix4f1, fraction(k, 16));
                stringVertex(f4, f5, f6, ivertexbuilder1, matrix4f1, fraction(k + 1, 16));
            }

            matrixStack.popPose();
            super.render(fishHook, p_225623_2_, p_225623_3_, matrixStack, buffer, p_225623_6_);
        }
    }

    private static float fraction(int p_229105_0_, int p_229105_1_) {
        return (float)p_229105_0_ / (float)p_229105_1_;
    }

    private static void vertex(IVertexBuilder vertexBuilder, Matrix4f matrix4f, Matrix3f matrix3f, int p_229106_3_, float p_229106_4_, int p_229106_5_, int p_229106_6_, int p_229106_7_) {
        vertexBuilder.vertex(matrix4f, p_229106_4_ - 0.5F, (float)p_229106_5_ - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float)p_229106_6_, (float)p_229106_7_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_229106_3_).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void stringVertex(float p_229104_0_, float p_229104_1_, float p_229104_2_, IVertexBuilder vertexBuilder, Matrix4f matrix4f, float p_229104_5_) {
        vertexBuilder.vertex(matrix4f, p_229104_0_ * p_229104_5_, p_229104_1_ * (p_229104_5_ * p_229104_5_ + p_229104_5_) * 0.5F + 0.25F, p_229104_2_ * p_229104_5_).color(0, 0, 0, 255).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(MonsterFishHook fishHook) {
        return TEXTURE;
    }
}
