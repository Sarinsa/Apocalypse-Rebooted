package com.toast.apocalypse.client.renderers.entity.ghost;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

public class TransparentModelRenderer extends ModelRenderer {

    public TransparentModelRenderer(Model model) {
        super(model);
    }

    public TransparentModelRenderer(Model model, int offsetX, int offsetY) {
        super(model, offsetX, offsetY);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int p_228309_3_, int p_228309_4_, float p_228309_5_, float p_228309_6_, float p_228309_7_, float p_228309_8_) {
        if (this.visible) {
            if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
                matrixStack.pushPose();

                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
                RenderSystem.depthMask(this.visible);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

                this.translateAndRotate(matrixStack);
                this.compile(matrixStack.last(), vertexBuilder, p_228309_3_, p_228309_4_, p_228309_5_, p_228309_6_, p_228309_7_, p_228309_8_);

                for(ModelRenderer modelrenderer : this.children) {
                    modelrenderer.render(matrixStack, vertexBuilder, p_228309_3_, p_228309_4_, p_228309_5_, p_228309_6_, p_228309_7_, p_228309_8_);
                }
                matrixStack.popPose();
            }
        }
    }
}
